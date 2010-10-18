package jsmp.dei.sd.server.tcp;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import jsmp.dei.sd.db.Database;
import jsmp.dei.sd.utils.Bet;
import jsmp.dei.sd.utils.ServerMessage;
import jsmp.dei.sd.utils.User;
import jsmp.dei.sd.utils.Utils;

import pt.uc.dei.sd.BetManager;

public class MatchHandler extends Thread{

	private Hashtable<String, ServerConnection> clients;
	private Timer timer;
	private BetManager man;
	private int seconds = 30; // FIXME should read from config
	private Database db;
	private int round = 1;	
	
	public MatchHandler(Database db, Hashtable<String, ServerConnection> clients) {
		this.clients = clients;
		this.db = db;
		this.man = new BetManager();
		this.timer = new Timer();
	    this.timer.scheduleAtFixedRate(new MatchesTask(), 10*1000/*this.seconds*2*1000*/, this.seconds*1000);
	    this.start();
	}

	class MatchesTask extends TimerTask {
		@Override
		public void run() {
			System.out.println(Utils.timeNow() + " ~> Round {" + round + "} New set of matches genereated.");
			matchesAvailable(); // tell clients about new matches
			
			db.doCreateMatches(man, round); // FIXME maybe only latest ~> push matches to the database
			
			// next round up
			man.refreshMatches();
			if (round > 1) {
				checkRoundResults(round - 1);
			}
			
			round++;
		}
    }
	
	/**
	 * Notifies online users of new round
	 * It makes more sense to cycle through online users and fetch
	 * associated thread from clients list to send message.
	 * There might be entries in the clients list that do not
	 * correspond to online users, so sending messages would be wrong.
	 */
	public void matchesAvailable() {
		Vector<User> onlineUsers = db.doGetOnlineUsers();
		               
		if (onlineUsers.size() != 0) {
			ServerConnection co;
			
			for (User user : onlineUsers) {
				co = clients.get(user.getScid()); // TODO maybe raise exception to handle fake logins - scid was not renewed
				
				try {
					co.out.writeObject(new ServerMessage("matches", "Round {"+round+"} has started. Place your bets."));
					co.out.flush();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	public void checkRoundResults(int round) {
		Vector<Bet> wonBets = db.checkRoundResults(round);
		
		if (wonBets.size() != 0) {
			for(Bet bet : wonBets) {
				try {
					clients.get(bet.getSubmitter().getScid()).out.writeObject(new ServerMessage("won", "Round {" + bet.getRound() + "} results: WON " + bet.getAmount() + " credits on game " + bet.getMatch().toString()));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}					
		}
	}
}