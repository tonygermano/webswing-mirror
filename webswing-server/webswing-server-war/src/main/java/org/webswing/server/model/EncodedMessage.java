package org.webswing.server.model;

import org.webswing.model.MsgOut;
import org.webswing.server.util.ServerUtil;

public class EncodedMessage {
	private MsgOut message;
	private String jsonMessage;
	private byte[] protoMessage;

	public EncodedMessage(MsgOut message) {
		this.message = message;
	}

	public String getJsonMessage() {
		if (jsonMessage == null) {
			jsonMessage = ServerUtil.encode2Json(message);
		}
		return jsonMessage;
	}

	public byte[] getProtoMessage() {
		if (protoMessage == null) {
			protoMessage = ServerUtil.encode2Proto(message);
		}
		return protoMessage;
	}

}
