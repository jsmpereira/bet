package jsmp.dei.sd.client.tcp;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Enumeration;
import java.util.Hashtable;
import jsmp.dei.sd.utils.ClientMessage;
import jsmp.dei.sd.utils.User;

public class Client {
		private volatile ObjectInputStream in;
	    private volatile ObjectOutputStream out;
	    private int serverPort = 6000;
	    private String hostname;
	    private volatile Socket socket;
	    private boolean loggedin = false;
	    private boolean connected = false;
	    private int message_counter = 0;
	    protected User user;
	    protected Reader reader;
		protected Hashtable<Integer, ClientMessage> messageBuffer;
    
	public Client(String hostname) {
		this.hostname = hostname;
		messageBuffer = new Hashtable<Integer, ClientMessage>();
		try {
			
			socket = new Socket(hostname, serverPort);
			connected = true;

			/*
			 * Order is important. ObjectOutputStream needs to come first.
			 * ObjectInputStream(InputStream in) will block until the corresponding ObjectOutputStream has written and flushed the header.
			 * Basically it needs to be opposite order from server. 
			 * @see http://www.jguru.com/faq/view.jsp?EID=333392
			 */
			out = new ObjectOutputStream(socket.getOutputStream());
			out.flush(); // force the magic number and version number out
			in = new ObjectInputStream(socket.getInputStream());
			
		} catch (java.net.ConnectException e) {
			connected = false;
			new TCPConnectionHandler(this);
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("IO: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	public void boot() {
		// Start reader thread if there is a connection to the server
		if (in != null) {
			reader = new Reader(this);
		}
	    
	    // Start command line interface
		new CLI(this).run();
	}
	
	public void reboot() throws IOException {
		reader = new Reader(this);

		connected = true;
		send_buffered();
	}
	
	/**
	 * In case of reconnection
	 * @throws IOException
	 */
	public void send_buffered() throws IOException {
		if (connected) {
			
			Enumeration<ClientMessage> e = messageBuffer.elements();
			
			while (e.hasMoreElements()) {
				send(e.nextElement());
			}
		}
	}

	public void send(ClientMessage message) throws IOException {
		/**
		 * Buffer message to send
		 * 
		 * FIXME need to choose which messages to buffer.
		 * There will be some situations where it doesn't make sense
		 * to buffer messages. 
		 */
		addBufferedMessage(message);
		
		if (connected) {
			//send message
			out.writeObject(message);
		}
	}
	
	public static void main(String args[]) {
		
		if (args.length == 0) {
		    System.out.println("java Client hostname");
		    System.exit(0);
		}

		Client client = new Client(args[0]);
		client.boot();
	}

	public void addBufferedMessage(ClientMessage message) {
		
		// no duplicates
		if(!messageBuffer.contains(message)) {
			message.setM_number(message_counter++);
			System.out.println("Buffering message #"+message.getM_number());
			messageBuffer.put(message.getM_number(), message);
		}
	}
	
	public void removeBufferedMessage(int message_number) {
		messageBuffer.remove(message_number);
	}
	
	public void setLoggedin(boolean loggedin) {
		this.loggedin = loggedin;
	}

	public boolean isLoggedin() {
		return loggedin;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public User getUser() {
		return user;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public String getHostname() {
		return hostname;
	}
	
	public int getServerPort() {
		return serverPort;
	}
	
	public void setSocket(Socket socket) {
		this.socket = socket;
	}
	
	public Socket getSocket() {
		return socket;
	}
	
	public void setOut(ObjectOutputStream out) {
		this.out = out;
	}
	
	public ObjectOutputStream getOut() {
		return out;
	}
	
	public void setIn(ObjectInputStream in) {
		this.in = in;
	}
	
	public ObjectInputStream getIn() {
		return in;
	}

	public void setConnected(boolean connected) {
		this.connected = connected;
	}

	public boolean isConnected() {
		return connected;
	}

	public void setMessage_counter(int message_counter) {
		this.message_counter = message_counter;
	}

	public int getMessage_counter() {
		return message_counter;
	}

}
