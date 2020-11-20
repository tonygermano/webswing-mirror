package org.webswing.model.app.out;

import org.webswing.model.MsgOut;

public class SessionDataMsgOut implements MsgOut {

	private static final long serialVersionUID = 4505813670370834105L;

	private boolean applet;
	private boolean sessionLoggingEnabled;
	private boolean recording;
	private String recordingFile;
	private boolean statisticsLoggingEnabled;

	public SessionDataMsgOut() {
	}

	public SessionDataMsgOut(boolean applet, boolean sessionLoggingEnabled, boolean recording, String recordingFile, boolean statisticsLoggingEnabled) {
		super();
		this.applet = applet;
		this.sessionLoggingEnabled = sessionLoggingEnabled;
		this.recording = recording;
		this.recordingFile = recordingFile;
		this.statisticsLoggingEnabled = statisticsLoggingEnabled;
	}

	public boolean isApplet() {
		return applet;
	}

	public void setApplet(boolean applet) {
		this.applet = applet;
	}

	public boolean isSessionLoggingEnabled() {
		return sessionLoggingEnabled;
	}

	public void setSessionLoggingEnabled(boolean sessionLoggingEnabled) {
		this.sessionLoggingEnabled = sessionLoggingEnabled;
	}

	public boolean isRecording() {
		return recording;
	}

	public void setRecording(boolean recording) {
		this.recording = recording;
	}

	public String getRecordingFile() {
		return recordingFile;
	}

	public void setRecordingFile(String recordingFile) {
		this.recordingFile = recordingFile;
	}

	public boolean isStatisticsLoggingEnabled() {
		return statisticsLoggingEnabled;
	}

	public void setStatisticsLoggingEnabled(boolean statisticsLoggingEnabled) {
		this.statisticsLoggingEnabled = statisticsLoggingEnabled;
	}

}
