package jsmp.dei.sd.client.tcp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import jsmp.dei.sd.utils.Bet;
import jsmp.dei.sd.utils.ClientMessage;
import jsmp.dei.sd.utils.User;
import jsmp.dei.sd.utils.Utils;
import jsmp.dei.sd.utils.Utils.Commands;

public class CLI {
	InputStreamReader input;
	BufferedReader reader;
	private boolean running;
	Client client = null;
	
	public CLI(Client client) {
		this.client = client;
		input = new InputStreamReader(System.in);
		reader = new BufferedReader(input);
		running = true;
	}
	
	public void run() {

		System.out.println("Welcome.");
		System.out.println("Type help for help.");
		System.out.print("client$ ");

		while (running) {
			
			try {
				
				parseOption(reader.readLine());
		
			} catch (Exception e) {
			    System.out.println("[CLI THREAD] java.net.SocketException: Broken pipe. TODO: Launch recovery thread.");
			    client.setConnected(false);
			    new TCPConnectionHandler(client);
			    //e.printStackTrace();
			    //break;
			}
		}
	}
	
	private void parseOption(String option) throws IOException {
		
		if (option.equalsIgnoreCase("")) {
			if(client.user != null && client.user.isLoggedin())
				System.out.print(client.user.getLogin() + ":");
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
				case DATE: {System.out.println(Utils.timeNow()); break;
				}
				default: { 
					System.out.println(option + ": command not found");
					if (client.user != null && client.user.isLoggedin())
						System.out.print(client.user.getLogin() + ":");
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
		
		Bet bet = new Bet(client.user, gameId, hunch, amount);
		
		client.send(new ClientMessage(option, bet, client.user.getScid()));
	}
	
	/**
	 * Some options might share the same method, since selected option
	 * is passed in and client message is built the same way.
	 * 
	 */
	private void optionResetCredits(String option) throws IOException {
		client.send(new ClientMessage(option, new User(client.user.getLogin())));
	}
	
	private void optionCredits(String option) throws IOException {
		client.send(new ClientMessage(option, new User(client.user.getLogin())));
	}
	
	private void optionWho(String option) throws IOException {
		client.send(new ClientMessage(option, new User(client.user.getLogin())));
	}
	
	
	private void optionLogin(String option) throws IOException {
		// Login
		System.out.println("login: ");
		String login = reader.readLine();
		System.out.println("password: ");
		String password = reader.readLine();
		
		client.send(new ClientMessage(option, new User(login, password)));
	}
	
	private void optionLogout(String option) throws IOException {
		client.send(new ClientMessage(option, new User(client.user.getLogin())));
	}
	
	private void optionRegister(String option) throws IOException {
		System.out.println("login: ");
		String login = reader.readLine();
		System.out.println("email: ");
		String email = reader.readLine();
		System.out.println("password: ");
		String password = reader.readLine();
	
		client.send(new ClientMessage(option, new User(login, email, password)));
	}
	
	private void optionMatches(String option) throws IOException {
		client.send(new ClientMessage(option, client.user.getLogin()));
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
				
		client.send(new ClientMessage(option, new User(client.user.getLogin()), new User(target), message));
	}

	public void setRunning(boolean running) {
		this.running = running;
	}

	public boolean isRunning() {
		return running;
	}
}
