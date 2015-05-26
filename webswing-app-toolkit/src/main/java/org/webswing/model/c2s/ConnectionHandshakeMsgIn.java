package org.webswing.model.c2s;

import org.webswing.model.MsgIn;

public class ConnectionHandshakeMsgIn implements MsgIn {

	private static final long serialVersionUID = -7143564320373144470L;

	private String clientId;
	private String sessionId;
	private Integer desktopWidth;
	private Integer desktopHeight;
	private String applicationName;
	private String documentBase;
	private boolean applet;
	private boolean mirrored;
	private boolean directDrawSupported;

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public Integer getDesktopWidth() {
		return desktopWidth;
	}

	public void setDesktopWidth(Integer desktopWidth) {
		this.desktopWidth = desktopWidth;
	}

	public Integer getDesktopHeight() {
		return desktopHeight;
	}

	public void setDesktopHeight(Integer desktopHeight) {
		this.desktopHeight = desktopHeight;
	}

	public String getApplicationName() {
		return applicationName;
	}

	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}

	public boolean isMirrored() {
		return mirrored;
	}

	public void setMirrored(boolean mirrored) {
		this.mirrored = mirrored;
	}

	public boolean isDirectDrawSupported() {
		return directDrawSupported;
	}

	public void setDirectDrawSupported(boolean directDrawSupported) {
		this.directDrawSupported = directDrawSupported;
	}

	public boolean isApplet() {
		return applet;
	}

	public void setApplet(boolean applet) {
		this.applet = applet;
	}

	public String getDocumentBase() {
		return documentBase;
	}

	public void setDocumentBase(String documentBase) {
		this.documentBase = documentBase;
	}
}
