package jsmp.dei.sd.utils;

import java.io.Serializable;

public class User implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public User(String login, String email, String password) {
		this.login = login;
		this.email = email;
		this.password = password;
	}
	
	public User(String login, boolean loggedin) {
		this.login = login;
		this.setLoggedin(loggedin);
	}

	public void setLogin(String login) {
		this.login = login;
	}
	
	public String getLogin() {
		return login;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getEmail() {
		return email;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPassword() {
		return password;
	}

	public void setCredits(int credits) {
		this.credits = credits;
	}

	public int getCredits() {
		return credits;
	}
	
	public void setLoggedin(boolean loggedin) {
		this.loggedin = loggedin;
	}

	public boolean isLoggedin() {
		return loggedin;
	}
	
	public String toString() {
		
		return "Login: " + login +
				"\n Email: " + email +
				"\n Password: " + password;
	}

	private String login;
	private String email;
	private String password;
	private int credits = 100; // this should be read from config file
	private boolean loggedin = false;
}
