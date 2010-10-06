package jsmp.dei.sd.utils;

/**
 * @author josesantos
 * 
 * Client message object
 * Should always send scid to the server
 *
 */
public class ClientMessage extends Message {

	private static final long serialVersionUID = 1L;
	private User user;
	private int game_id;
	private Bet bet;
	private int amount;
	private String scid;
	
	/**
	 * Login command
	 * 
	 * @param command
	 * @param login
	 * @param password
	 */
	public ClientMessage(String command, User user) {
		super(command);
		this.setUser(user);
	}
	
	
	/**
	 * Query commands, request command and login
	 * 
	 * Applies to: credits, reset, matches, logout
	 * 
	 * @param command
	 * @param login
	 */
	public ClientMessage(String command, String scid) {
		super(command);
		this.scid = scid;
	}
	
	/**
	 * Bet command
	 * 
	 * @param command
	 * @param game_id
	 * @param bet
	 */
	public ClientMessage(String command, Bet bet, String scid) {
		super(command);
		this.setScid(scid);
		this.bet = bet;
	}

	public void setGame_id(int game_id) {
		this.game_id = game_id;
	}

	public int getGame_id() {
		return game_id;
	}

	public void setBet(Bet bet) {
		this.bet = bet;
	}

	public Bet getBet() {
		return bet;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public int getAmount() {
		return amount;
	}

	public void setScid(String scid) {
		this.scid = scid;
	}

	public String getScid() {
		return scid;
	}


	public void setUser(User user) {
		this.user = user;
	}


	public User getUser() {
		return user;
	}
}
