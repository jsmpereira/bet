package jsmp.dei.sd;

public class User {
	
	User(String login, String email, String password) {
		this.login = login;
		this.email = email;
		this.password = password;
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

	private String login;
	private String email;
	private String password;
	private int credits = 100; // this should be read from config file
}
