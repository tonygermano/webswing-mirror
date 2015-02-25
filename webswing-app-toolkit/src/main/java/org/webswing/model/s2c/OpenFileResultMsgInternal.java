package org.webswing.model.s2c;

import java.io.File;

import org.webswing.model.MsgInternal;

public class OpenFileResultMsgInternal implements MsgInternal {

	private static final long serialVersionUID = 2490892979442744806L;
	private File f;
	private String clientId;

	public File getF() {
		return f;
	}

	public void setF(File f) {
		this.f = f;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

}
