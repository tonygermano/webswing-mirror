package org.webswing.model.app.out;

import org.webswing.model.MsgOut;

public class AppHandshakeMsgOut implements MsgOut {

	private static final long serialVersionUID = 8346698410554059038L;
	
	private String secretMessage;

	public String getSecretMessage() {
		return secretMessage;
	}

	public void setSecretMessage(String secretMessage) {
		this.secretMessage = secretMessage;
	}

}
