package jsmp.dei.sd.client.rmi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;

import jsmp.dei.sd.server.rmi.RMIConnection;
import jsmp.dei.sd.utils.Bet;
import jsmp.dei.sd.utils.ClientMessage;
import jsmp.dei.sd.utils.User;
import jsmp.dei.sd.utils.Utils;
import jsmp.dei.sd.utils.Utils.Commands;

public class RMIClient extends UnicastRemoteObject implements IClient {

	private static final long serialVersionUID = 1L;
	RMIConnection rc;
	User user;
	String scid;
	InputStreamReader input;
	BufferedReader reader;

	protected RMIClient() throws RemoteException {
		super();
		
		input = new InputStreamReader(System.in);
		reader = new BufferedReader(input);

		try {
			rc = (RMIConnection) LocateRegistry.getRegistry(7000).lookup("rmi://localhost/rmiconnect");
		} catch (NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		scid = rc.subscribe(this);
	}

	public void message_client(String message) throws RemoteException {
		System.out.println("\n\n\t\t\t\t\t\t "+message);
	}

	public static void main(String args[]) {

		//System.getProperties().put("java.security.policy", "policy.all");
		//System.setSecurityManager(new RMISecurityManager());
 
		try {
			RMIClient ci = new RMIClient();
			System.out.println("Client sent subscription to server");
			
			while (true) {
				
				ci.parseOption(ci.reader.readLine());
			
			}

		} catch (Exception e) {
			System.out.println("Exception in main: " + e);
		}

	}

	private void parseOption(String option) throws RemoteException, IOException {
		
		if (option.equalsIgnoreCase("")) {
			if(user != null && user.isLoggedin())
				System.out.print(user.getLogin() + ":");
			System.out.print("client$ ");
		} else {
			
			switch (Commands.toOption(option.toUpperCase())) {
			
				case HELP: Utils.optionHelp(); break;
				case LOGIN: optionLogin(option); break;
				case LOGOUT: optionLogout(); break;
				case CREDITS: optionCredits(); break;
				case RESET: optionResetCredits(); break;
				case MATCHES: optionMatches(); break;
				case BET: optionBet(); break;
				case WHO: optionWho(); break;
				case MESSAGE: optionMessage(); break;
				case BROADCAST: optionBroadcast(); break;
				case REGISTER: optionRegister(); break;
				default: { 
					System.out.println(option + ": command not found");
					if (user != null && user.isLoggedin())
						System.out.print(user.getLogin() + ":");
					System.out.print("client$ "); break;} 
			}
		}
	}

	private void optionRegister() throws RemoteException, IOException {
		System.out.println("login: ");
		String login = reader.readLine();
		System.out.println("email: ");
		String email = reader.readLine();
		System.out.println("password: ");
		String password = reader.readLine();
		
		rc.sRegister(new User(login, email, password));
	}

	private void optionBroadcast() throws RemoteException, IOException {
		System.out.print("Message: ");
		String message = reader.readLine();
		
		rc.sPublic(user.getLogin(), message);
	}

	private void optionMessage() throws RemoteException, IOException {
		System.out.print("User to send: ");
		String target = reader.readLine();
		System.out.print("Message: ");
		String message = reader.readLine();
		
		rc.sPrivate(user.getLogin(), target, message);
	}

	private void optionWho() throws RemoteException {
		rc.sWho(user.getLogin());
	}

	private void optionBet() throws NumberFormatException, IOException, RemoteException {
		System.out.print("game id: ");
		int gameId = Integer.parseInt(reader.readLine().trim());
		System.out.print("bet [0] tie [1] home [2] away: ");
		int hunch = Integer.parseInt(reader.readLine().trim());
		System.out.print("amount: ");
		int amount = Integer.parseInt(reader.readLine().trim());
		
		rc.sBet(new Bet(user, gameId, hunch, amount));
	}

	private void optionMatches() throws RemoteException {
		rc.sMatches();
	}

	private void optionResetCredits() throws RemoteException {
		rc.sReset(user.getLogin());
	}

	private void optionCredits() throws RemoteException {
		rc.sCredits(user.getLogin());
	}

	private void optionLogout() throws RemoteException {
		rc.sLogout(user.getLogin());
	}

	private void optionLogin(String option) throws RemoteException, IOException {
		System.out.println("login: ");
		String login = reader.readLine();
		System.out.println("password: ");
		String password = reader.readLine();
		
		user = rc.sLogin(new ClientMessage(option, new User(login, password)), scid);		
	}
}
