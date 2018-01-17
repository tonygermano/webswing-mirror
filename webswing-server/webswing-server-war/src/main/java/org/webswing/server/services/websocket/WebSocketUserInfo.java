package org.webswing.server.services.websocket;

import org.webswing.server.util.ServerUtil;

import java.util.Date;

public class WebSocketUserInfo {

	private final String userId;
	private final String userBrowser;
	private final String customArgs;
	private final int debugPort;
	private final String userIp;
	private final String userOs;
	private Date disconnectedSince;

	WebSocketUserInfo(WebSocketConnection conn) {
		this.userId = conn.getUserId();
		this.customArgs = ServerUtil.getCustomArgs(conn.getRequest());
		this.debugPort = ServerUtil.getDebugPort(conn.getRequest());
		this.userIp = ServerUtil.getClientIp(conn);
		this.userOs = ServerUtil.getClientOs(conn);
		this.userBrowser = ServerUtil.getClientBrowser(conn);
	}

	public String getUserId() {
		return userId;
	}

	public String getCustomArgs() {
		return customArgs;
	}

	public int getDebugPort() {
		return debugPort;
	}

	public String getUserIp() {
		return userIp;
	}

	public String getUserOs() {
		return userOs;
	}

	public String getUserBrowser() {
		return userBrowser;
	}

	public Date getDisconnectedSince() {
		return disconnectedSince;
	}

	public void setDisconnected() {
		disconnectedSince=new Date();
	}
}
