package jsmp.dei.sd.utils;

import java.io.Serializable;

import jsmp.dei.sd.utils.Utils.MessageCode;

public class Message implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String name;
	private MessageCode code;
	private Object payload;
	
	public Message(String name) {
		this.name = name;
	}
	
	public Message(String name, MessageCode code) {
		this.name = name;
		this.code = code;
	}
	
	public Message(String name, Object payload) {
		this.name = name;
		this.payload = payload;
	}
	
	public Message(String name, MessageCode code, Object payload) {
		this.name = name;
		this.payload = payload;
		this.code = code;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setCode(MessageCode code) {
		this.code = code;
	}

	public MessageCode getCode() {
		return code;
	}

	public void setPayload(Object payload) {
		this.payload = payload;
	}

	public Object getPayload() {
		return payload;
	}
	
}