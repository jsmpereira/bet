package jsmp.dei.sd.server.tcp;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.rmi.RemoteException;
import java.util.Vector;

import pt.uc.dei.sd.IMatch;
import jsmp.dei.sd.db.Database;
import jsmp.dei.sd.db.Users;
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
			out.writeObject(new ServerMessage(MessageCode.NOTIFY, "Welcome."));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		while(true) {
		
			try {
				message = (Message) in.readObject();
				
				/*
				 *  TODO validate serverConnectioID sent by the client
				 *  (needs to be the same as ServerConnection) for requests 
				 *  except login and register. 
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
				out.reset(); // to go around object caching. Reseting the stream all the time might not be the best practice.
				out.writeObject(new ServerMessage(name, matches)); break;
			}
			case WHO: {
				Vector<User> onlineUsers = db.doGetOnlineUsers();
				out.reset();
				out.writeObject(new ServerMessage(name, onlineUsers)); break;
			}
			case MESSAGE: {
				Users user = db.findByLogin(aMessage.getRecipient().getLogin()); // TODO We probably don't want Users instance here, but User
				ServerConnection co = matchHandler.getClients().get(user.getScid());
				co.out.reset();
				co.out.writeObject(new ServerMessage(name, MessageCode.MESSAGE, aMessage.getMessage(), aMessage.getUser())); break;
			}
			case BROADCAST: {
				Vector<User> onlineUsers = db.doGetOnlineUsers();
				ServerConnection co;
				
				for (User user : onlineUsers) {
					co = matchHandler.getClients().get(user.getScid()); // TODO maybe raise exception to handle fake logins - scid was not renewed
					try {
						co.out.writeObject(new ServerMessage(name, MessageCode.BROADCAST, aMessage.getMessage(), aMessage.getUser()));
						co.out.flush();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				break;
			}
			case LOGOUT: {
				if(db.doLogout(aMessage.getUser().getLogin())) {
					matchHandler.getClients().remove(aMessage.getUser().getScid()); // remove reference from clients list
					out.writeObject(new ServerMessage(name, MessageCode.OK, "Logout Successful."));
				}
			}
		}
	}
}
