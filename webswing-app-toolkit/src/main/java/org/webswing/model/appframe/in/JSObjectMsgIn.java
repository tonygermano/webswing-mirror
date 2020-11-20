package org.webswing.model.appframe.in;

import org.webswing.model.MsgIn;

public class JSObjectMsgIn implements MsgIn {
	private static final long serialVersionUID = -5961292110459877432L;
	private String id;

	public JSObjectMsgIn() {
	}

	public JSObjectMsgIn(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

}
