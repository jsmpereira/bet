package jsmp.dei.sd.db;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Vector;

import pt.uc.dei.sd.BetManager;
import pt.uc.dei.sd.IMatch;
import pt.uc.dei.sd.Match;

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
			// No database, no game
			System.out.println("Oops. Database init failed. Is the db process running?");
			System.out.println("A working db connection is needed. Shutting down...");
			System.exit(-1);
		}
		this.start();
	}
	
	public void doRegister(User user) {
		Users dbuser = null;
		
		if (userExists(user.getLogin())) {
			System.out.println("Login name already taken");
		} else {
			try {
				dbuser= manager.create(Users.class);
			} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			}
			dbuser.setLogin(user.getLogin());
			dbuser.setEmail(user.getEmail());
			dbuser.setPassword(user.getPassword());
			dbuser.setCredits(100); // TODO should read from config
			dbuser.setLogged_in(false);
			dbuser.save();
		
			System.out.println("Created: " + dbuser.toString());
		}
	}
	
	public User doLogin(User user, String scid) {
		Users[] users = null;
		try {
			users = manager.find(Users.class, "login = ? AND password = ?", user.getLogin(), user.getPassword());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
						
		if (users.length >= 1) {
			users[0].setLogged_in(true);
			users[0].setScid(scid);
			users[0].save();
						
			user.setLogin(users[0].getLogin());
			user.setLoggedin(users[0].isLogged_in());
			user.setScid(scid);
		} else {
			user = null;
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
			//FIXME throw custom DatabaseException
		}
		
		// We do this conversion so that Database Entity Users class is not exposed to the client
		if (users != null) {
			for(Users user : users) {
				User on = new User(user.getLogin());
				on.setScid(user.getScid());
				onlineUsers.add(on);
			}
		}
		return onlineUsers;
	}
	
	public Vector<IMatch> doGetMatches() {
		Matches[] matches = null;
		Vector<IMatch> current_matches = new Vector<IMatch>();
		
		try {
			matches = manager.findWithSQL(Matches.class, "id", "SELECT id FROM matches WHERE round = (SELECT max(round) from matches)");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		for(Matches m : matches)
			current_matches.add(new Match(m.getCode(), m.getHome(), m.getAway()));
		
		return current_matches;
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
	
	public boolean doCreateBet(Bet bet) {
		Matches[] matches = null;
		/*
		 * Validate bet - check to see if the match belongs to latest round
		 * If not, bet was late or had wrong game_id(code). 
		 */
		
		try {
			matches = manager.findWithSQL(Matches.class, "id", "SELECT id, code FROM matches WHERE round = (SELECT max(round) from matches) HAVING code = ?", bet.getGame_id());
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		// no Match found on current round with supplied ID
		if (matches.length == 0) {
			return false;
		}
		
		try {
			manager.create(Bets.class,
					new DBParam("submitter", bet.getSubmitter().getLogin()),
					new DBParam("game_id", bet.getGame_id()),
					new DBParam("bet", bet.getBet().toString()),
					new DBParam("amount", bet.getAmount()),
					new DBParam("won", bet.isWon()));
			
			// update users credits
			Users user = findByLogin(bet.getSubmitter().getLogin());
			user.setCredits(user.getCredits() - 
					bet.getAmount());
			user.save();
			return true;
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * TODO: Needs some re-factoring
	 * 
	 * @param round
	 */
	public ArrayList<Vector<Bet>> checkRoundResults(int round) {
		Matches[] matches = null;
		Bets[] bets = null;
		ArrayList<Vector<Bet>> wonAndLost = new ArrayList<Vector<Bet>>(); // oh multiple return values where art thou? 
		Vector<Bet> wonBets = new Vector<Bet>();
		Vector<Bet> lostBets = new Vector<Bet>();
		
		try {
			matches = manager.find(Matches.class, "round = ?", round);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("Round {" + round + "} results");
		for (Matches m : matches) {
			
			try {
				bets = manager.find(Bets.class, "game_id = ?", m.getCode());
			
				for(Bets b : bets) {
				
					if (m.getCode() == b.getGame_id()) {
						if (m.getResult().equalsIgnoreCase(b.getBet())) {
							b.setWon(true);
							b.save();
					
							// Update user's credits -- triple the amount
							Users user = findByLogin(b.getSubmitter());
							user.setCredits(user.getCredits() + b.getAmount()*3);
							user.save();
						
							// Add to wonBets vector
							User submitter = new User(user.getLogin());
							submitter.setScid(user.getScid());
							
							Bet won = new Bet(submitter, b.getGame_id(), b.getAmount()*3);
							won.setMatch(new Match(b.getGame_id(), m.getHome(), m.getAway()));
							won.setRound(m.getRound());
							wonBets.add(won);
						
							System.out.println("WON ~> " + b.getSubmitter() + " on Round " + m.getRound() + ", game " + m.getCode() + " RES: " + m.getResult());
						} else {
							Users user = findByLogin(b.getSubmitter());
							
							User submitter = new User(user.getLogin());
							submitter.setScid(user.getScid());
							
							Bet lost = new Bet(submitter, b.getGame_id(), b.getAmount());
							lost.setMatch(new Match(b.getGame_id(), m.getHome(), m.getAway()));
							lost.setRound(m.getRound());
							lostBets.add(lost);
							
							System.out.println("LOST ~> " + b.getSubmitter() + " on Round " + m.getRound() + ", game " + m.getCode() + " RES: " + b.getBet() + "/" + m.getResult());
						}
					}
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		wonAndLost.add(wonBets);
		wonAndLost.add(lostBets);
		return wonAndLost;
	}
	
	public Users findByLogin(String login) {
		Users[] users = null;
		try {
			users = manager.find(Users.class, "login = ?", login);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return users.length == 1 ? users[0] : null;
	}	
	
	// Private Methods
	
	private boolean userExists(String login) {
		Users user = findByLogin(login);
		
		if (user != null)
			return true;
		else
			return false;
	}
	
	
}
