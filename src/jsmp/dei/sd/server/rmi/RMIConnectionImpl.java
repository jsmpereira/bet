package jsmp.dei.sd.server.rmi;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.Vector;

import pt.uc.dei.sd.IMatch;

import jsmp.dei.sd.client.rmi.IClient;
import jsmp.dei.sd.db.Database;
import jsmp.dei.sd.db.Users;
import jsmp.dei.sd.utils.Bet;
import jsmp.dei.sd.utils.ClientMessage;
import jsmp.dei.sd.utils.User;

public class RMIConnectionImpl extends UnicastRemoteObject implements RMIConnection {

	private static final long serialVersionUID = 1L;
	private Database db;
	private IClient client;
	
	public RMIConnectionImpl(Database db) throws RemoteException {
		super();
		this.db = db;
	}

	public User sLogin(ClientMessage message) throws RemoteException {
		
		User user = db.doLogin(message.getUser(), "sad");
		if (user != null) {
			client.message_client("Login Successful.");
			return user;
		} else {
			client.message_client("Wrong login/password combination.");
			return null;
		}
	}
	
	public void sRegister(User user) throws RemoteException {
		db.doRegister(user);
		client.message_client("Registration successful");
	}

	public void sCredits(String login) throws RemoteException {
		client.message_client("You have " + db.doGetCredits(login) + " credits.");
	}

	public void sReset(String login) throws RemoteException {
		client.message_client("Credits reset. You have " + db.doUpdateCredits(login) + " credits.");
	}

	public void sBet(Bet bet) throws RemoteException {
		db.doCreateBet(bet);
		client.message_client("Bet submitted successfuly.");
	}

	public void sMatches() throws RemoteException {
		String matches_output = "";
		Vector<IMatch> matches = db.doGetMatches();
		if (matches != null) {
			for (IMatch m : (List<IMatch>) matches) {
				matches_output += "["+ m.getCode() + "] " + m.getHomeTeam() + " vs " + m.getAwayTeam()+"\n";
			}
		}
		client.message_client(matches_output);
	}

	// FIXME build a single message to send instead of multiple calls to the client callback
	public void sWho(String login) throws RemoteException {
		String who_output = "";
		
		Vector<User> onlineUsers = db.doGetOnlineUsers();
		if (onlineUsers.size() != 0) {
			who_output += "Online Users";
			for (User user : onlineUsers) {
				who_output += "\n"+user.getLogin();
				if (user.getLogin().equalsIgnoreCase(login))
					who_output += "(this is you)";
			}
		}
		client.message_client(who_output);
	}

	public void sPrivate(String login, String target, String message) throws RemoteException {
		Users user = db.findByLogin(target); // TODO We probably don't want Users instance here, but User
		if (user != null) {
			//ServerConnection co = matchHandler.getClients().get(user.getScid()); // here use clients callback
			//FIXME get target from connections list and send message through callback
			//target.message_client(login + " says: "+message);
			
			client.message_client("Message sento to "+target);	
		} else {
			client.message_client("User not found.");
		}
	}

	public void sPublic(String login, String message) throws RemoteException {
		Vector<User> onlineUsers = db.doGetOnlineUsers();
		
		// Clients callback here also
		//ServerConnection co;
		
		for (User user : onlineUsers) {
			
			//FIXME on each user send message through callback
			// user.message_client(
			/*co = matchHandler.getClients().get(user.getScid()); // TODO maybe raise exception to handle fake logins - scid was not renewed
			try {
				co.out.writeObject(new ServerMessage(name, MessageCode.BROADCAST, aMessage.getMessage(), aMessage.getUser()));
				co.out.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
		}
		client.message_client("Message broadcasted.");
	}

	public void sLogout(String login) throws RemoteException {
		if(db.doLogout(login)) {
			//matchHandler.getClients().remove(aMessage.getUser().getScid()); // remove reference from clients list
			client.message_client("Logout Successful.");
		}
	}

	public void subscribe(String name, IClient client) throws RemoteException {
		System.out.println("Subscribed "+name);
		this.client = client;
	}
}