package jsmp.dei.sd.utils;

/**
 * @author josesantos
 *
 */
public class ClientMessage extends Message {

	private static final long serialVersionUID = 1L;
	private String login;
	private String email;
	private String password;
	private int game_id;
	private int bet;
	private int amount;
	
	/**
	 * Login command
	 * 
	 * @param command
	 * @param login
	 * @param password
	 */
	public ClientMessage(String command, String login, String password) {
		super(command);
		this.login = login;
		this.password = password;
	}
	
	/**
	 * Bet command
	 * 
	 * @param command
	 * @param game_id
	 * @param bet
	 */
	public ClientMessage(String command, String login, int game_id, int bet, int amount) {
		super(command);
		this.login = login;
		this.game_id = game_id;
		this.bet = bet;
		this.amount = amount;
	}
	
	/**
	 * Query commands, request command and login
	 * 
	 * Applies to: credits, reset, matches, logout
	 * 
	 * @param command
	 * @param login
	 */
	public ClientMessage(String command, String login) {
		super(command);
		this.login = login;
	}
	
	/**
	 * Register command
	 * 
	 * @param command
	 * @param login
	 * @param email
	 * @param password
	 */
	public ClientMessage(String command, String login, String email, String password) {
		super(command);
		this.setLogin(login);
		this.setEmail(email);
		this.setPassword(password);
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

	public void setGame_id(int game_id) {
		this.game_id = game_id;
	}

	public int getGame_id() {
		return game_id;
	}

	public void setBet(int bet) {
		this.bet = bet;
	}

	public int getBet() {
		return bet;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public int getAmount() {
		return amount;
	}
}
