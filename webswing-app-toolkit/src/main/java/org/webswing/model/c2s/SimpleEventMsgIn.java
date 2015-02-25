package org.webswing.model.c2s;

import org.webswing.model.MsgIn;

public class SimpleEventMsgIn implements MsgIn {

	private static final long serialVersionUID = 5832849328825358575L;

	public enum Type {
		unload, killSwing, paintAck, repaint, downloadFile, deleteFile, hb
	}

	public Type type;
	public String clientId;

}
