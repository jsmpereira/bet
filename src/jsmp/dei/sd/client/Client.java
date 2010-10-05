package jsmp.dei.sd.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import jsmp.dei.sd.utils.User;

public class Client {
	private ObjectInputStream in;
    private ObjectOutputStream out;
    private int serverSocket = 6000;
    private Socket socket;
    private boolean loggedin = false;
    private User user;
    
	public Client(String hostname) {
		
		try {
			socket = new Socket(hostname, serverSocket);
			System.out.println("SOCKET=" + socket);

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
			new Reader(this, in, out);
	    
	    // Start command line interface thread
		if (out != null)
			new CLI(this, out);
	}
	
	public static void main(String args[]) {
		
		if (args.length == 0) {
		    System.out.println("java Client hostname");
		    System.exit(0);
		}

		Client client = new Client(args[0]);
		client.boot();
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

}
