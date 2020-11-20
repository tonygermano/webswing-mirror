package org.webswing.model.browser.out;

import org.webswing.model.MsgOut;

public class ConnectionInfoMsgOut implements MsgOut {

	private static final long serialVersionUID = -23686931923075716L;

	private String serverId;
	private String sessionPoolId;

	public ConnectionInfoMsgOut() {
		super();
	}

	public ConnectionInfoMsgOut(String serverId, String sessionPoolId) {
		super();
		this.serverId = serverId;
		this.sessionPoolId = sessionPoolId;
	}

	public String getServerId() {
		return serverId;
	}

	public void setServerId(String serverId) {
		this.serverId = serverId;
	}

	public String getSessionPoolId() {
		return sessionPoolId;
	}

	public void setSessionPoolId(String sessionPoolId) {
		this.sessionPoolId = sessionPoolId;
	}

}
