package jsmp.dei.sd.utils;

import jsmp.dei.sd.utils.Utils.MessageCode;

public class ServerMessage extends Message {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String message;
	
	public ServerMessage(String command, String message) {
		super(command);
		this.message = message;
	}
	
	public ServerMessage(String command, Object payload) {
		super(command, payload);
	}
	
	public ServerMessage(String command, MessageCode code, String message) {
		super(command, code);
		this.message = message;
	}
	
	public ServerMessage(String command, MessageCode code, String message, Object payload) {
		super(command, code, payload);
		this.message = message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

}
