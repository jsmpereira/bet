package jsmp.dei.sd.server.tcp;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import jsmp.dei.sd.utils.Bet;
import jsmp.dei.sd.utils.Utils;

import pt.uc.dei.sd.BetManager;

public class MatchHandler extends Thread{

	private Timer timer;
	private BetManager man;
	private int seconds = 30; // FIXME should read from config
	private Server server;
	private int round = 1;	
	
	public MatchHandler(Server server) {
		this.server = server;
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
			
			server.db.doCreateMatches(man, round); // FIXME maybe only latest ~> push matches to the database
			
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
	 */
	public void matchesAvailable() {
		server.broadcast("Round {"+round+"} has started. Place your bets.");
	}
	
	public void checkRoundResults(int round) {
		ArrayList<Vector<Bet>> wonAndLost = server.db.checkRoundResults(round); 
		Vector<Bet> wonBets = wonAndLost.get(0);
		Vector<Bet> lostBets = wonAndLost.get(1);
		
		if (wonBets.size() != 0) {
			for(Bet bet : wonBets) {
				String message = "Round {" + bet.getRound() + "} results: WON " + bet.getAmount() + " credits on game " + bet.getMatch().toString();
				server.send(bet.getSubmitter().getScid(), message);
			}
		}
		
		if (lostBets.size() != 0) {
			for(Bet bet : lostBets) {
				String message = "Round {" + bet.getRound() + "} results: LOST " + bet.getAmount() + " credits on game " + bet.getMatch().toString();
				server.send(bet.getSubmitter().getScid(), message);
			}
		}
	}
	
	public int getRound() {
		return round;
	}
}