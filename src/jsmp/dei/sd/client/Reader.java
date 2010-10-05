package jsmp.dei.sd.client;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

import pt.uc.dei.sd.IMatch;

import jsmp.dei.sd.utils.ServerMessage;
import jsmp.dei.sd.utils.Utils.Commands;
import jsmp.dei.sd.utils.Utils.MessageCode;
import jsmp.dei.sd.utils.User;

public class Reader extends Thread {

	ObjectInputStream in;
	ObjectOutputStream out;
	Client client = null;
	
	public Reader(Client client, ObjectInputStream in, ObjectOutputStream out) {
		this.client = client;
		this.in = in;
		this.out = out;
		this.start();
	}

	public void run() {
		
		while (true) {
			
			try {
				
				ServerMessage message = (ServerMessage) in.readObject();;		
				parseMessages(message);
				
			} catch (EOFException e) {
			    System.out.println("[READER THREAD] EOFException: Server went away. TODO: Launch recovery thread here.");
			    e.printStackTrace();
			    break;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	private void parseMessages(ServerMessage message) {
		if (message.getMessage() != null)
			System.out.println("\n\n\t\t\t\t\t\t FROM SERVER: " + message.getMessage());
		
		switch(Commands.toOption(message.getName().toUpperCase())) {
			
			case LOGIN: {
				if (message.getCode() == MessageCode.OK) {
					client.setUser((User) message.getPayload());
				}
				break;
			}
			case LOGOUT: {
				if (message.getCode() == MessageCode.OK) {
					client.setUser(null);
				}
				break;	
			}
			case BET: break; // NOOP 
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
