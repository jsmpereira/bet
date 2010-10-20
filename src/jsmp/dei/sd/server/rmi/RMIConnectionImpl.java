package jsmp.dei.sd.server.rmi;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.Vector;

import pt.uc.dei.sd.IMatch;

import jsmp.dei.sd.client.rmi.IClient;
import jsmp.dei.sd.db.Users;
import jsmp.dei.sd.server.tcp.Server;
import jsmp.dei.sd.utils.Bet;
import jsmp.dei.sd.utils.ClientMessage;
import jsmp.dei.sd.utils.User;

public class RMIConnectionImpl extends UnicastRemoteObject implements RMIConnection {

	private static final long serialVersionUID = 1L;
	private Server server;
	private IClient client;
	
	public RMIConnectionImpl(Server server) throws RemoteException {
		super();
		this.server = server;
	}

	public User sLogin(ClientMessage message, String scid) throws RemoteException {
		
		User user = server.getDB().doLogin(message.getUser(), scid);
		if (user != null) {
			client.message_client("Login Successful.", true);
			return user;
		} else {
			client.message_client("Wrong login/password combination.", true);
			return null;
		}
	}
	
	public void sRegister(User user) throws RemoteException {
		server.getDB().doRegister(user);
		client.message_client("Registration successful", true);
	}

	public void sCredits(String login) throws RemoteException {
		client.message_client("You have " + server.getDB().doGetCredits(login) + " credits.", true);
	}

	public void sReset(String login) throws RemoteException {
		client.message_client("Credits reset. You have " + server.getDB().doUpdateCredits(login) + " credits.", true);
	}

	public void sBet(Bet bet) throws RemoteException {
				
		if (server.getDB().doCreateBet(bet))
			client.message_client("Bet submitted successfuly.", true);
		else
			client.message_client("Incorrect Game ID for current round", true);
	}

	public void sMatches() throws RemoteException {
		String matches_output = "";
		Vector<IMatch> matches = server.getDB().doGetMatches();
		if (matches != null) {
			for (IMatch m : (List<IMatch>) matches) {
				matches_output += "["+ m.getCode() + "] " + m.getHomeTeam() + " vs " + m.getAwayTeam()+"\n";
			}
		}
		client.message_client(matches_output, false);
	}

	// FIXME build a single message to send instead of multiple calls to the client callback
	public void sWho(String login) throws RemoteException {
		String who_output = "";
		
		Vector<User> onlineUsers = server.getDB().doGetOnlineUsers();
		if (onlineUsers.size() != 0) {
			who_output += "Online Users";
			for (User user : onlineUsers) {
				who_output += "\n"+user.getLogin();
				if (user.getLogin().equalsIgnoreCase(login))
					who_output += "(this is you)";
			}
		}
		client.message_client(who_output, false);
	}

	public void sPrivate(String login, String target, String message) throws RemoteException {
		Users user = server.getDB().findByLogin(target); // TODO We probably don't want Users instance here, but User
		if (user != null) {
			//ServerConnection co = matchHandler.getClients().get(user.getScid()); // here use clients callback
			//FIXME get target from connections list and send message through callback
			//target.message_client(login + " says: "+message);
			
			client.message_client("Message sento to "+target, true);	
		} else {
			client.message_client("User not found.", true);
		}
	}

	public void sPublic(String login, String message) throws RemoteException {		
		server.broadcast(message);
		
		//client.message_client("Message broadcasted.", true);
	}

	public void sLogout(String login) throws RemoteException {
		if(server.getDB().doLogout(login)) {
			//matchHandler.getClients().remove(aMessage.getUser().getScid()); // remove reference from clients list
			client.message_client("Logout Successful.", true);
		}
	}

	public String subscribe(IClient client) throws RemoteException {
		String scid = server.addClient(client);
		this.client = client;
		
		System.out.println("Subscribed RMIClient: "+scid);
		client.message_client("At your disposal", true);
		return scid;
	}
}