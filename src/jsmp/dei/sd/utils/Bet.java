package jsmp.dei.sd.utils;

import pt.uc.dei.sd.Result;

public class Bet {
	
	private String submitter;
	private int game_id;
	private Result bet;
	private int amount;
	private boolean won  = false;
	
	public Bet(String submitter, int game_id, int amount) {
		this.submitter = submitter;
		this.game_id = game_id;
		this.amount = amount;
	}
	
	public Bet(String submitter, int game_id, int bet, int amount) {
		this.submitter = submitter;
		this.game_id = game_id;
		this.bet = parseBet(bet);
		this.amount = amount;
	}

	public void setGame_id(int game_id) {
		this.game_id = game_id;
	}

	public int getGame_id() {
		return game_id;
	}

	public void setBet(Result bet) {
		this.bet = bet;
	}

	public Result getBet() {
		return bet;
	}

	public void setSubmitter(String submitter) {
		this.submitter = submitter;
	}

	public String getSubmitter() {
		return submitter;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public int getAmount() {
		return amount;
	}

	public void setWon(boolean won) {
		this.won = won;
	}

	public boolean isWon() {
		return won;
	}
	
	public String toString() {
		return String.format("[%s] bet %d on game %d to %s ~~~~> %s", submitter, amount, game_id, bet.toString(), won);
	}
	
	private Result parseBet(int bet) {
		Result res = null;
		switch (bet) {
		case 0: res = Result.TIE; break;
		case 1: res =  Result.HOME; break;
		case 2: res = Result.AWAY; break;
		}
		return res;
	}

}
