package jsmp.dei.sd.client.tcp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;

import jsmp.dei.sd.utils.Bet;
import jsmp.dei.sd.utils.ClientMessage;
import jsmp.dei.sd.utils.User;
import jsmp.dei.sd.utils.Utils;
import jsmp.dei.sd.utils.Utils.Commands;

public class CLI extends Thread{
	InputStreamReader input;
	BufferedReader reader;
	ObjectOutputStream out;
	private boolean online = false;
	Client client = null;
	
	
	public CLI(Client client) {
		this.client = client;
		out = client.getOut();
		input = new InputStreamReader(System.in);
		reader = new BufferedReader(this.input);
		online = true;
		this.start();
	}
	
	public void run() {

		System.out.println("Welcome.");
		System.out.println("Type help for help.");
		System.out.print("client$ ");

		while (online) {
			
			try {
				
				parseOption(reader.readLine());
				
		
			} catch (Exception e) {
			    System.out.println("[CLI THREAD] java.net.SocketException: Broken pipe. TODO: Launch recovery thread.");
			    new ConnectionHandler(client);
			    //e.printStackTrace();
			    //break;
			}
			System.out.println("CLI ONLINE? "+online);
		}
	}
	
	private void parseOption(String option) throws IOException {
		
		if (option.equalsIgnoreCase("")) {
			if(client.getUser() != null && client.getUser().isLoggedin())
				System.out.print(client.getUser().getLogin() + ":");
			System.out.print("client$ ");
		} else {
			
			switch (Commands.toOption(option.toUpperCase())) {
			
				case HELP: Utils.optionHelp(); break;
				case LOGIN: optionLogin(option); break;
				case LOGOUT: optionLogout(option); break;
				case CREDITS: optionCredits(option); break;
				case RESET: optionResetCredits(option); break;
				case MATCHES: optionMatches(option); break;
				case BET: optionBet(option); break;
				case WHO: optionWho(option); break;
				case MESSAGE: optionMessage(option, false); break;
				case BROADCAST: optionMessage(option, true); break;
				case REGISTER: optionRegister(option); break;
				default: { 
					System.out.println(option + ": command not found");
					if (client.getUser() != null && client.getUser().isLoggedin())
						System.out.print(client.getUser().getLogin() + ":");
					System.out.print("client$ "); break;} 
			}
		}
	}
	
	/*
	 * Options handling
	 */
	
	private void optionBet(String option) throws IOException {
		// Bet
		System.out.print("game id: ");
		int gameId = Integer.parseInt(reader.readLine().trim());
		System.out.print("bet [0] tie [1] home [2] away: ");
		int hunch = Integer.parseInt(reader.readLine().trim());
		System.out.print("amount: ");
		int amount = Integer.parseInt(reader.readLine().trim());
		
		Bet bet = new Bet(client.getUser(), gameId, hunch, amount);
		
		out.writeObject(new ClientMessage(option, bet, client.getUser().getScid()));
	}
	
	/**
	 * Some options might share the same method, since selected option
	 * is passed in and client message is built the same way.
	 * 
	 */
	private void optionResetCredits(String option) throws IOException {
		out.writeObject(new ClientMessage(option, new User(client.getUser().getLogin())));
	}
	
	private void optionCredits(String option) throws IOException {
		out.writeObject(new ClientMessage(option, new User(client.getUser().getLogin())));
	}
	
	private void optionWho(String option) throws IOException {
		out.writeObject(new ClientMessage(option, new User(client.getUser().getLogin())));
	}
	
	
	private void optionLogin(String option) throws IOException {
		// Login
		System.out.println("login: ");
		String login = reader.readLine();
		System.out.println("password: ");
		String password = reader.readLine();
	
		out.writeObject(new ClientMessage(option, new User(login, password)));
	}
	
	private void optionLogout(String option) throws IOException {
		out.writeObject(new ClientMessage(option, new User(client.getUser().getLogin())));
	}
	
	private void optionRegister(String option) throws IOException {
		System.out.println("login: ");
		String login = reader.readLine();
		System.out.println("email: ");
		String email = reader.readLine();
		System.out.println("password: ");
		String password = reader.readLine();
	
		out.writeObject(new ClientMessage(option, new User(login, email, password)));
	}
	
	private void optionMatches(String option) throws IOException {
		out.writeObject(new ClientMessage(option, client.getUser().getLogin()));
	}
	
	private void optionMessage(String option, boolean broadcast) throws IOException {
		String target;

		if (!broadcast) {
			System.out.print("User to send: ");
			target = reader.readLine();
		} else {
			target = "all";
		}
		System.out.print("Message: ");
		String message = reader.readLine();
				
		out.writeObject(new ClientMessage(option, new User(client.getUser().getLogin()), new User(target), message));
	}

	public void setOnline(boolean online) {
		this.online = online;
	}

	public boolean isOnline() {
		return online;
	}
}
