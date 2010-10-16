package jsmp.dei.sd.client.tcp;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Vector;

import jsmp.dei.sd.utils.User;

public class Client {
		private volatile ObjectInputStream in;
	    private volatile ObjectOutputStream out;
	    private int serverPort = 6000;
	    private String hostname;
	    private volatile Socket socket;
	    private boolean loggedin = false;
	    private boolean connected = false;
	    private User user;
	    private Vector<Thread> thread_list = new Vector<Thread>();
	    private Reader r;
	    private CLI cli;
    
	public Client(String hostname) {
		this.hostname = hostname;
		try {
			
			socket = new Socket(hostname, serverPort);
			System.out.println("SOCKET=" + socket);
			connected = true;

			/*
			 * Order is important. ObjectOutputStream needs to come first.
			 * ObjectInputStream(InputStream in) will block until the corresponding ObjectOutputStream has written and flushed the header.
			 * For some reason in the server code, seems to be indifferent. 
			 * @see http://www.jguru.com/faq/view.jsp?EID=333392
			 */
			out = new ObjectOutputStream(socket.getOutputStream());
			out.flush(); // force the magic number and version number out
			in = new ObjectInputStream(socket.getInputStream());
			
		} catch (java.net.ConnectException e) {
			System.out.println("Server is not running.");
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("IO: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	public void boot() {
		// Start reader thread
		if (in != null)
			thread_list.add(r = new Reader(this));
	    
	    // Start command line interface thread
		if (out != null)
			thread_list.add(cli = new CLI(this));
	}
	
	public static void main(String args[]) {
		
		if (args.length == 0) {
		    System.out.println("java Client hostname");
		    System.exit(0);
		}

		Client client = new Client(args[0]);
		client.boot();
	}

	
	public Vector<Thread> getThreadList() {
		return thread_list;
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

}
