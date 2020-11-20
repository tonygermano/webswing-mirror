package org.webswing.model.appframe.out;

import org.webswing.model.MsgOut;

public class StartApplicationMsgOut implements MsgOut {

	private static final long serialVersionUID = -7176092462203716782L;

	private String serverId;
	private String sessionPoolId;

	public StartApplicationMsgOut() {
	}

	public StartApplicationMsgOut(String serverId, String sessionPoolId) {
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
