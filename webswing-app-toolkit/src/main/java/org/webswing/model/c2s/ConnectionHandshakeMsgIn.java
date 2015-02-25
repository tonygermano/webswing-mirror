package org.webswing.model.c2s;

import org.webswing.model.MsgIn;

public class ConnectionHandshakeMsgIn implements MsgIn {

	private static final long serialVersionUID = -7143564320373144470L;

	public String clientId;
	public String sessionId;
	public Integer desktopWidth;
	public Integer desktopHeight;
	public String applicationName;
	public boolean mirrored;
	public boolean directDrawSupported;

}
