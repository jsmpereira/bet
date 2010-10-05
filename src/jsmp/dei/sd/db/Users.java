package jsmp.dei.sd.db;

import net.java.ao.Entity;

public interface Users extends Entity{
	
	public void setLogin(String login);
	
	public String getLogin();

	public void setEmail(String email);

	public String getEmail();

	public void setPassword(String password);

	public String getPassword();

	public void setCredits(int credits);

	public int getCredits();

	public void setLogged_in(boolean logged_in);

	public boolean isLogged_in();

}
