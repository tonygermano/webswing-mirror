package org.webswing.model.c2s;

import org.webswing.model.MsgIn;

public class PasteEventMsgIn implements MsgIn {

	private static final long serialVersionUID = -2857821597134135941L;
	private String clientId;
	private String content;

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

}
