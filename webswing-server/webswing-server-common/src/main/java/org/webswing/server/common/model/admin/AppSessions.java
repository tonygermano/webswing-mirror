package org.webswing.server.common.model.admin;

public class AppSessions extends Sessions {

	private boolean sessionLoggingEnabled;

	public boolean isSessionLoggingEnabled() {
		return sessionLoggingEnabled;
	}

	public void setSessionLoggingEnabled(boolean sessionLoggingEnabled) {
		this.sessionLoggingEnabled = sessionLoggingEnabled;
	}
	
}
