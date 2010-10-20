package jsmp.dei.sd.utils;

import jsmp.dei.sd.utils.Utils.MessageCode;

public class ServerMessage extends Message {
	
	private static final long serialVersionUID = 1L;
	private String message;
	
	public ServerMessage(MessageCode code, String message) {
		super(code);
		this.message = message;
	}
	
	public ServerMessage(int m_number, MessageCode code, String message) {
		super(code);
		super.setM_number(m_number);
		this.message = message;
	}
	
	public ServerMessage(int m_number, String command, String message) {
		super(command);
		super.setM_number(m_number);
		this.message = message;
	}
	
	public ServerMessage(int m_number, String command, Object payload) {
		super(command, payload);
		super.setM_number(m_number);
	}
	
	public ServerMessage(int m_number, String command, MessageCode code, String message) {
		super(command, code);
		super.setM_number(m_number);
		this.message = message;
	}
	
	public ServerMessage(int m_number, String command, MessageCode code, String message, Object payload) {
		super(command, code, payload);
		super.setM_number(m_number);
		this.message = message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

}
