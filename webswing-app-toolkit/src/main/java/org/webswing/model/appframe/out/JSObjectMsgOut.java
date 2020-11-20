package org.webswing.model.appframe.out;

import org.webswing.model.MsgOut;

public class JSObjectMsgOut implements MsgOut {
	private static final long serialVersionUID = -5961292110459877432L;
	private String id;

	public JSObjectMsgOut() {
	}

	public JSObjectMsgOut(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

}
