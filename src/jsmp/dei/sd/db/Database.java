package jsmp.dei.sd.db;

import java.sql.SQLException;
import java.util.Vector;

import pt.uc.dei.sd.BetManager;
import pt.uc.dei.sd.IMatch;

import jsmp.dei.sd.utils.Bet;
import jsmp.dei.sd.utils.Utils;
import jsmp.dei.sd.utils.User;

import net.java.ao.DBParam;
import net.java.ao.EntityManager;

public class Database extends Thread{

	EntityManager manager;
	
	@SuppressWarnings("unchecked")
	public Database () {
		manager = new EntityManager("jdbc:mysql://localhost/sdist", "root", "lala");
		
		try {
			manager.migrate(Users.class, Matches.class, Bets.class);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.start();
	}
	
	public void doRegister(String login, String email, String password) {
		Users user = null;
		
		if (userExists(login)) {
			System.out.println("Login name already taken");
		} else {
			try {
				user= manager.create(Users.class);
			} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			}
			user.setLogin(login);
			user.setEmail(email);
			user.setPassword(password);
			user.setCredits(100);
			user.setLogged_in(false);
			user.save();
		
			System.out.println("Created: " + user.toString());
		}
	}
	
	public User doLogin(String login, String password) {
		Users[] users = null;
		User user = null;
		try {
			users = manager.find(Users.class, "login = ? AND password = ?", login, password);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (users.length == 1) {
			users[0].setLogged_in(true);
			users[0].save();
			user = new User(users[0].getLogin(), users[0].isLogged_in());
		}
		return user;
	}
	
	/**
	 * Should implement some consistency checks.
	 * 
	 * @param login
	 * @return
	 */
	public boolean doLogout(String login) {
		Users user = findByLogin(login);
		
		user.setLogged_in(false);
		user.save();
		return true;
	}
	
	public int doUpdateCredits(String login) {
		Users user = findByLogin(login);
		
		user.setCredits(Utils.CREDITS);
		user.save();
		System.out.println("Credits reset for "+ login + ": " + user.getCredits() + " credits.");
		return user.getCredits();
	}
	
	public int doGetCredits(String login) {
		Users user = findByLogin(login);
		return user.getCredits();
	}
	
	public Vector<User> doGetOnlineUsers() {
		Users[] users = null;
		Vector<User> onlineUsers = new Vector<User>();
		try {
			users = manager.find(Users.class, "logged_in = ?", true);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// We do this conversion so that Database Entity Users class is not exposed to the client
		for(Users user : users)
			onlineUsers.add(new User(user.getLogin()));
		
		return onlineUsers;
	}
	
	public void doCreateMatches(BetManager man, int round) {
				
		for (IMatch m : man.getMatches()) {
			try {
				manager.create(Matches.class,
					new DBParam("code", Integer.parseInt(m.getCode())),
					new DBParam("result", man.getResult(m).toString()),
					new DBParam("home", m.getHomeTeam()),
					new DBParam("away", m.getAwayTeam()),
					new DBParam("round", round));
				
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}	
	}
	
	public void doCreateBet(Bet bet) {
		try {
			manager.create(Bets.class,
					new DBParam("submitter", bet.getSubmitter()),
					new DBParam("game_id", bet.getGame_id()),
					new DBParam("bet", bet.getBet().toString()),
					new DBParam("amount", bet.getAmount()),
					new DBParam("won", bet.isWon()));
			
			// update users credits
			Users user = findByLogin(bet.getSubmitter());
			user.setCredits(user.getCredits() - 
					bet.getAmount());
			user.save();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * TODO: Needs some re-factoring
	 * 
	 * @param round
	 */
	public Vector<Bet> checkRoundResults(int round) {
		Matches[] matches = null;
		Bets[] bets = null;
		Vector<Bet> wonBets = new Vector<Bet>();
		
		try {
			matches = manager.find(Matches.class, "round = ?", round);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("ROUND "+round+" results");
		for (Matches m : matches) {
			
			try {
				bets = manager.find(Bets.class, "game_id = ?", m.getCode());
			
				for(Bets b : bets) {
				
					if (m.getCode() == b.getGame_id()) {
						if(m.getResult().equalsIgnoreCase(b.getBet())) {
							b.setWon(true);
							b.save();
					
							// Update user's credits -- triple the amount
							Users user = findByLogin(b.getSubmitter());
							user.setCredits(user.getCredits() + b.getAmount()*3);
							user.save();
						
							// Add to wonBets vector
							Bet won = new Bet(b.getSubmitter(), b.getGame_id(), b.getAmount()*3);
							wonBets.add(won);
						
							System.out.println(b.getSubmitter() + " on game " + m.getCode() + " RES: "+ m.getResult());
						}
					}
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return wonBets;
	}
	
	// Private Methods
	
	private boolean userExists(String login) {
		Users user = findByLogin(login);
		
		if (user != null)
			return true;
		else
			return false;
	}
	
	private Users findByLogin(String login) {
		Users[] users = null;
		try {
			users = manager.find(Users.class, "login = ?", login);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return users.length == 1 ? users[0] : null;
	}
}
