package jsmp.dei.sd.client.tcp;

import java.io.EOFException;
import java.io.IOException;
import java.util.List;
import java.util.Vector;

import pt.uc.dei.sd.IMatch;

import jsmp.dei.sd.utils.ServerMessage;
import jsmp.dei.sd.utils.Utils.Commands;
import jsmp.dei.sd.utils.Utils.MessageCode;
import jsmp.dei.sd.utils.User;

public class Reader extends Thread {

	Client client = null;
	private boolean online = false;
	
	public Reader(Client client) {
		this.client = client;
		online = true;
		this.start();
	}
	
	public void setOnline(boolean online) {
		this.online = online;
	}

	public void run() {
		
		while (online) {
			
			try {
				
				ServerMessage message = (ServerMessage) client.getIn().readObject();;		
				parseMessages(message);
				
			} catch (EOFException e) {
			    System.out.println("[READER THREAD] EOFException: Server went away. TODO: Launch recovery thread here.");
			    new ConnectionHandler(client);
			    e.printStackTrace();
			    break;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("READER ONLINE? "+online);
		}
	}
	
	@SuppressWarnings("unchecked")
	private void parseMessages(ServerMessage message) {
		if (message.getCode() == MessageCode.NOTIFY)
			System.out.println("\n\n\t\t\t\t\t\t FROM SERVER: " + message.getMessage());
		else {
			
			System.out.println("READER READ; "+message.toString());
			
			switch(Commands.toOption(message.getName().toUpperCase())) {
			
				case LOGIN: {
					if (message.getCode() == MessageCode.OK) {
						client.setUser((User) message.getPayload());					
					}
					System.out.println("\n\n\t\t\t\t\t\t FROM SERVER: " + message.getMessage());
					break;
				}
				case LOGOUT: {
					if (message.getCode() == MessageCode.OK) {
						client.setUser(null);
					}
					System.out.println("\n\n\t\t\t\t\t\t FROM SERVER: " + message.getMessage());
					break;	
				}
				case BET: break; // NOOP
				case WHO: {
					Vector<User> onlineUsers = (Vector<User>) message.getPayload();
				
					if (onlineUsers.size() != 0) {
						System.out.println("Online Users");
						for (User user : onlineUsers) {
							System.out.print(user.getLogin());
							if (user.getLogin().equalsIgnoreCase(client.getUser().getLogin()))
								System.out.println("(this is you)");
						}
					}
					break;
				}
				case MESSAGE: {
					User user = (User) message.getPayload();
					String msg = message.getMessage();
					System.out.println("\n\n\t\t\t\t\t\t Private Message from " + user.getLogin() + " ~> " +msg);
					break;
				}
				case BROADCAST: {
					User user = (User) message.getPayload();
					String msg = message.getMessage();
					System.out.println("\n\n\t\t\t\t\t\t Broadcast Message from " + user.getLogin() + " ~> " +msg);
					break;
				}
				case MATCHES: {
					/* When server notifies the clients of new matches this case
					 * gets hit, but there's no payload.
					 * 
					 * Payload could be set and and sent along with with the notify message.
					 * Client would set a local variable with the payload.
					 * _matches_ command would read that local variable and not hit the server everytime time.
					 * 
					 * *** TO CONSIDER ***
					 */
					
					if (message.getPayload() != null) {
						for (IMatch m : (List<IMatch>) message.getPayload()) {
							System.out.println("["+ m.getCode() + "] " + m.getHomeTeam() + " vs " + m.getAwayTeam());
						}
					}
					break;
				}
				//default: System.out.println("server sent something");
			}
		}
		
	}
}
