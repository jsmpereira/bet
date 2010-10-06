package jsmp.dei.sd.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Vector;

import pt.uc.dei.sd.IMatch;
import jsmp.dei.sd.db.Database;
import jsmp.dei.sd.utils.*;
import jsmp.dei.sd.utils.Utils.Commands;
import jsmp.dei.sd.utils.Utils.MessageCode;

public class ServerConnection extends Thread {
	ObjectInputStream in;
    ObjectOutputStream out;
    Socket clientSocket;
    Database db;
    MatchHandler matchHandler;
    String scid;
    String user_login;
	
	public ServerConnection(String scid, Database db, MatchHandler matchHandler, Socket clientSocket) {
		this.scid = scid;
		this.matchHandler = matchHandler;
		this.clientSocket = clientSocket;
		this.db = db;	
		try {
			out = new ObjectOutputStream(clientSocket.getOutputStream());
			out.flush();
			in = new ObjectInputStream(clientSocket.getInputStream());
		} catch(IOException e){System.out.println("Connection: " + e.getMessage());}
		this.start();
	}
	
	public String getSCID() { return scid; }
	public String getUserLogin() { return user_login; }
	
	public void run() {
	
		Message message; // class to handle commands
		
		try {
			out.writeObject(new ServerMessage("login", "Welcome."));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		while(true) {
		
			try {
				message = (Message) in.readObject();
				
				/*
				 *  TODO validate serverConnectioID sent by the client,
				 *  needs to be the same as ServerConnection 
				 */

				parseMessage(message);
				
			} catch (Exception e) {
				System.out.println("[SERVER CONNECTION THREAD] EOFException: Client abnornal quit.");
				e.printStackTrace(); break;}
			/*finally {
				try {
					in.close();
					out.close();
					clientSocket.close();
				} catch (IOException e) {e.printStackTrace();}
			}*/
		}
	}
	
	private void parseMessage(Message message) throws IOException {
				
		ClientMessage aMessage = (ClientMessage) message;
		String name = aMessage.getName();
		
		System.out.print(Utils.timeNow() + " ["+this.getName()+"]");
		if (aMessage.getUser() != null && aMessage.getUser().getLogin() != null)
			System.out.print(" ~> " + aMessage.getUser().getLogin() + " ");
		System.out.println("requested ~> " + aMessage.getName());
			
		switch(Commands.toOption(name.toUpperCase())) {
			case LOGIN: {
				User user = db.doLogin(aMessage.getUser(), scid);
				if (user != null) {
					out.reset();
					out.writeObject(new ServerMessage(name, MessageCode.OK, "Login Successful.", user)); // might need out.reset()
					user_login = user.getLogin();
				} else
					out.writeObject(new ServerMessage(name, MessageCode.FAIL, "Wrong login/password combination."));
				break;
			}
			case REGISTER: db.doRegister(aMessage.getUser()); break;
			case CREDITS: {
				out.writeObject(new ServerMessage(name, MessageCode.OK, "You have " + db.doGetCredits(aMessage.getUser().getLogin()) + " credits."));
				break;
			}
			case RESET: {
				out.writeObject(new ServerMessage(name, MessageCode.OK, "Credits reset. You have " + db.doUpdateCredits(aMessage.getUser().getLogin()) + " credits."));
				break;
			}
			case BET: {
				db.doCreateBet(aMessage.getBet());
				out.writeObject(new ServerMessage(name, MessageCode.OK, "Bet submitted."));
				break;
			}
			case MATCHES: {
				Vector<IMatch> matches = db.doGetMatches();
				out.reset(); // to go around object caching. Reseting the stream all might not be the best practice.
				out.writeObject(new ServerMessage(name, matches)); break;
			}
			case WHO: {
				Vector<User> onlineUsers = db.doGetOnlineUsers();
				out.reset();
				out.writeObject(new ServerMessage(name, onlineUsers)); break;
			}
			case LOGOUT: {
				if(db.doLogout(aMessage.getUser().getLogin())) {
					out.writeObject(new ServerMessage(name, MessageCode.OK, "Logout Successful."));
				}
			}
		}
	}
}
