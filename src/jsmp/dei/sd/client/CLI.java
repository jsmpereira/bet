package jsmp.dei.sd.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;

import jsmp.dei.sd.utils.ClientMessage;
import jsmp.dei.sd.utils.Utils.Commands;

public class CLI extends Thread{
	InputStreamReader input;
	BufferedReader reader;
	ObjectOutputStream out;
	Client client = null;
	
	
	public CLI(Client client, ObjectOutputStream out) {
		this.client = client;
		this.out = out;
		input = new InputStreamReader(System.in);
		reader = new BufferedReader(this.input);
		this.start();
	}
	
	public void run() {

		System.out.println("Welcome.");
		System.out.println("Type help for help.");
		System.out.print("client$ ");

		while (true) {
			
			try {
				
				parseOption(reader.readLine());
				
		
			} catch (Exception e) {
			    System.out.println("[CLI THREAD] java.net.SocketException: Broken pipe. TODO: Launch recovery thread.");
			    e.printStackTrace();
			}
		}
	}
	
	private void parseOption(String option) throws IOException {
		
		if (option.equalsIgnoreCase("")) {
			if(client.getUser() != null && client.getUser().isLoggedin())
				System.out.print(client.getUser().getLogin() + ":");
			System.out.print("client$ ");
		} else {
			
			switch (Commands.toOption(option.toUpperCase())) {
			
				case HELP: optionHelp(); break;
				case LOGIN: optionLogin(option); break;
				case LOGOUT: optionLogout(option); break;
				case CREDITS: optionCredits(option); break;
				case RESET: optionResetCredits(option); break;
				case MATCHES: optionMatches(option); break;
				case BET: optionBet(option); break;
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
		int bet = Integer.parseInt(reader.readLine().trim());
		System.out.print("amount: ");
		int amount = Integer.parseInt(reader.readLine().trim());
		
		out.writeObject(new ClientMessage(option, client.getUser().getLogin(), gameId, bet, amount));
	}
	
	/**
	 * Some options might share the same method, since selected option
	 * is passed in and client message is built the same way.
	 * 
	 */
	private void optionResetCredits(String option) throws IOException {
		out.writeObject(new ClientMessage(option, client.getUser().getLogin()));
	}
	
	private void optionCredits(String option) throws IOException {
		out.writeObject(new ClientMessage(option, client.getUser().getLogin()));
	}
	
	
	private void optionLogin(String option) throws IOException {
		// Login
		System.out.println("login: ");
		String login = reader.readLine();
		System.out.println("password: ");
		String password = reader.readLine();
	
		out.writeObject(new ClientMessage(option, login, password));
	}
	
	private void optionLogout(String option) throws IOException {
		out.writeObject(new ClientMessage(option, client.getUser().getLogin()));
	}
	
	private void optionRegister(String option) throws IOException {
		System.out.println("login: ");
		String login = reader.readLine();
		System.out.println("email: ");
		String email = reader.readLine();
		System.out.println("password: ");
		String password = reader.readLine();
	
		out.writeObject(new ClientMessage(option, login, email, password));
	}
	
	private void optionMatches(String option) throws IOException {
		out.writeObject(new ClientMessage(option, client.getUser().getLogin()));
	}
	
	private void optionHelp() {
		System.out.println(
				"\nThis is help." +
				"\n\nAvailable commands:" +
				
				"\n\n Require no authentication:" +
				"\n\t register - create a new account" +
				"\n\t login - enter your credentials to authenticate" +
				
				"\n\n Require authentication: " +
				"\n\t credits - send a tweet" +
				"\n\t reset - list all tweets (you + following)" +
				"\n\t matches - list users you are following" +
				"\n\t bet - list users that follow you" +
				"\n\t who - follow a given user" +
				"\n\t message - user search" +				
				"\n\t broadcast - terminate your session" +
				
				"\n\n Other:" +
				"\n\t help - print this help message" +
				"\n\t date - print the current date and time" +
				"\n\t quit - exit the client\n\n");
	}
}
