package org.webswing.model.admin.s2c;

import java.util.Date;

import org.webswing.model.Msg;

public class MessageMsg implements Msg {

	private static final long serialVersionUID = -9026878807789576842L;

	public enum Type {
		danger, success
	}

	private Type type;
	private String text;
	private Date time;

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

}
