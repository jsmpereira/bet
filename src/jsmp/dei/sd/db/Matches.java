package jsmp.dei.sd.db;

import net.java.ao.Entity;
import net.java.ao.OneToMany;

public interface Matches extends Entity {
	
	public int getRound();
	public void setRound(int round);
	
	public int getCode();
	public void setCode(int code);
    
    public String getHome();
    public void setHome(String home);
    
    public String getAway();
    public void setAway(String away);
    
    public String getResult();
    public void setResult(String result);
    
    @OneToMany 
	public Bets[] getBets();
}
