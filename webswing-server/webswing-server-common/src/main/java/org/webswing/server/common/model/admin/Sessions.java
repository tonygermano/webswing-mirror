package org.webswing.server.common.model.admin;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Sessions implements Serializable {
	private List<SwingSession> sessions = new ArrayList<SwingSession>();
	private List<SwingSession> closedSessions = new ArrayList<SwingSession>();
	private List<String> recordings = new ArrayList<>();

	public List<SwingSession> getSessions() {
		return sessions;
	}

	public void setSessions(List<SwingSession> sessions) {
		this.sessions = sessions;
	}

	public List<SwingSession> getClosedSessions() {
		return closedSessions;
	}

	public void setClosedSessions(List<SwingSession> closedSessions) {
		this.closedSessions = closedSessions;
	}

	public List<String> getRecordings() {
		return recordings;
	}

	public void setRecordings(List<String> recordings) {
		this.recordings = recordings;
	}
}
