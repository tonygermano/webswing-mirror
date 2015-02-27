package org.webswing.model.c2s;

import org.webswing.model.MsgIn;

public class SimpleEventMsgIn implements MsgIn {

	private static final long serialVersionUID = 5832849328825358575L;

	public enum SimpleEventType {
		unload, killSwing, paintAck, repaint, downloadFile, deleteFile, hb
	}

	private SimpleEventType type;
	private String clientId;

	public SimpleEventType getType() {
		return type;
	}

	public void setType(SimpleEventType type) {
		this.type = type;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

}
