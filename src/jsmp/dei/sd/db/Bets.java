package jsmp.dei.sd.db;

import net.java.ao.Entity;

public interface Bets extends Entity {

	public int getAmount();
	public void setAmount(int amount);
	
	public int getGame_id();
	public void setGame_id(int game_id);
	
	public boolean isWon();
	public void setWon(boolean won);
	
	public String getBet();
	public void setBet(String bet);
	
	public String getSubmitter();
	public void setSubmitter(String submitter);
}
