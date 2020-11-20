package org.webswing.model.adminconsole.in;

import org.webswing.model.MsgIn;

public class AdminConsoleHandshakeMsgIn implements MsgIn {

	private static final long serialVersionUID = 7531089764777425520L;
	
	private String secretMessage;

	public String getSecretMessage() {
		return secretMessage;
	}

	public void setSecretMessage(String secretMessage) {
		this.secretMessage = secretMessage;
	}
	
}
