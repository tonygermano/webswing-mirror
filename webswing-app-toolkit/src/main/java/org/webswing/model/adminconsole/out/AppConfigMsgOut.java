package org.webswing.model.adminconsole.out;

import org.webswing.model.MsgOut;

public class AppConfigMsgOut implements MsgOut {

	private static final long serialVersionUID = -7226371902803856086L;

	private byte[] appConfig;
	private String sessionPoolId;
	private String error;

	public AppConfigMsgOut() {
	}

	public AppConfigMsgOut(String error) {
		super();
		this.error = error;
	}

	public AppConfigMsgOut(byte[] appConfig, String sessionPoolId) {
		super();
		this.appConfig = appConfig;
		this.sessionPoolId = sessionPoolId;
	}

	public byte[] getAppConfig() {
		return appConfig;
	}

	public void setAppConfig(byte[] appConfig) {
		this.appConfig = appConfig;
	}

	public String getSessionPoolId() {
		return sessionPoolId;
	}

	public void setSessionPoolId(String sessionPoolId) {
		this.sessionPoolId = sessionPoolId;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

}
