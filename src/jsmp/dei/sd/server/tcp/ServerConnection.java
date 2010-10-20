package jsmp.dei.sd.server.tcp;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Vector;

import pt.uc.dei.sd.IMatch;
import jsmp.dei.sd.db.Users;
import jsmp.dei.sd.utils.*;
import jsmp.dei.sd.utils.Utils.Commands;
import jsmp.dei.sd.utils.Utils.MessageCode;

public class ServerConnection extends Thread {
	ObjectInputStream in;
    ObjectOutputStream out;
    Socket clientSocket;
    String scid;
    Server server;
    String user_login;
	
	public ServerConnection(Server server, Socket clientSocket) {
		this.server = server;
		this.clientSocket = clientSocket;
		try {
			out = new ObjectOutputStream(clientSocket.getOutputStream());
			out.flush();
			in = new ObjectInputStream(clientSocket.getInputStream());
		} catch(IOException e){System.out.println("Connection: " + e.getMessage());}
		this.start();
	}
	
	public String getSCID() { return scid; }
	public void setSCID(String scid) { this.scid = scid; }
	public String getUserLogin() { return user_login; }
	
	public void run() {
	
		Message message; // class to handle commands
		
		try {
			out.writeObject(new ServerMessage(MessageCode.NOTIFY, "At your disposal."));
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
				User user = server.db.doLogin(aMessage.getUser(), scid);
				if (user != null) {
					send(new ServerMessage(name, MessageCode.OK, "Login Successful.", user));
					user_login = user.getLogin();
				} else
					send(new ServerMessage(name, MessageCode.FAIL, "Wrong login/password combination."));
				break;
			}
			case REGISTER: server.db.doRegister(aMessage.getUser()); break;
			case CREDITS: {
				send(new ServerMessage(name, MessageCode.NOTIFY, "You have " + server.db.doGetCredits(aMessage.getUser().getLogin()) + " credits."));
				break;
			}
			case RESET: {
				send(new ServerMessage(name, MessageCode.NOTIFY, "Credits reset. You have " + server.db.doUpdateCredits(aMessage.getUser().getLogin()) + " credits."));
				break;
			}
			case BET: {
				if (server.db.doCreateBet(aMessage.getBet()))
					send(new ServerMessage(name, MessageCode.OK, "Bet submitted."));
				else
					send(new ServerMessage(name, MessageCode.FAIL, "Incorrect Game ID for current round."));
				break;
			}
			case MATCHES: {
				Vector<IMatch> matches = server.db.doGetMatches();
				send(new ServerMessage(name, matches)); break;
			}
			case WHO: {
				Vector<User> onlineUsers = server.db.doGetOnlineUsers();
				
				send(new ServerMessage(name, onlineUsers)); break;
			}
			case MESSAGE: {
				Users user = server.db.findByLogin(aMessage.getRecipient().getLogin()); // TODO We probably don't want Users instance here, but User
				server.send(user.getScid(), aMessage.getMessage());
				break;
			}
			case BROADCAST: {
				server.broadcast(aMessage.getMessage());
				break;
			}
			case LOGOUT: {
				if(server.db.doLogout(aMessage.getUser().getLogin())) {
					server.clients.remove(aMessage.getUser().getScid()); // remove reference from clients list
					send(new ServerMessage(name, MessageCode.OK, "Logout Successful."));
				}
			}
		}
	}
	
	public void send(ServerMessage message) {
		try {
			out.reset();
			out.writeObject(message);
			System.out.println("Sending message to {"+scid+"}");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
