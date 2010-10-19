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
	private String scid;
	private int m_number;
	
	public Message(String name) {
		this.name = name;
	}
	public Message(MessageCode code) {
		this.code = code;
	}
	
	public Message(String name, String scid) {
		this.name = name;
		this.scid = scid;
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

	public void setScid(String scid) {
		this.scid = scid;
	}

	public String getScid() {
		return scid;
	}
	public void setM_number(int m_number) {
		this.m_number = m_number;
	}
	public int getM_number() {
		return m_number;
	}
	
}
