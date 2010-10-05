package jsmp.dei.sd.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import jsmp.dei.sd.db.Database;
import jsmp.dei.sd.utils.Bet;
import jsmp.dei.sd.utils.ServerMessage;
import jsmp.dei.sd.utils.Utils;

import pt.uc.dei.sd.BetManager;
import pt.uc.dei.sd.IMatch;

public class MatchHandler extends Thread{

	private ArrayList<ServerConnection> clients;
	private Timer timer;
	private BetManager man;
	private int seconds = 30;
	private Database db;
	private int round = 1;
	
	public MatchHandler(Database db, ArrayList<ServerConnection> clients) {
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
			System.out.println(Utils.timeNow() + " ~> New set of matches genereated.");
			matchesAvailable(); // tell clients about new matches
			
			db.doCreateMatches(man, round); // push matches to the database
			
			// next round up
			man.refreshMatches();
			if (round > 1) {
				checkRoundResults(round - 1);
			}
			
			round++;
		}
    }
		
	public List<IMatch> getMatches() {
		return man.getMatches();
	}
	
	public void matchesAvailable() {
		synchronized (clients) {
			for (int i=0; i<this.clients.size(); i++) {
		  	ServerConnection co = (ServerConnection) clients.get(i);
				try {
					co.out.writeObject(new ServerMessage("matches", "Round {"+round+"} has started. Place your bets."));
					co.out.flush();
				} catch (IOException e) {e.printStackTrace();}
			}
		}
	}
	
	public void addBet(Bet bet) {
		db.doCreateBet(bet);
	}
	
	public void checkRoundResults(int round) {
		
		Vector<Bet> wonBets = db.checkRoundResults(round);
		
		if (wonBets.size() != 0) {
			synchronized(clients) {
				for(ServerConnection conn : clients) {
					for(Bet bet : wonBets) {
						if (bet.getSubmitter().equalsIgnoreCase(conn.getUserLogin())) {
							try {
								conn.out.writeObject(new ServerMessage("won", "You have won bet on " + bet.getGame_id()));
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}	
				}
				
			}
		}
	}
}