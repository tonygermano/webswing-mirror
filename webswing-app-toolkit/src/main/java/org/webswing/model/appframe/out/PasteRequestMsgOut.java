package org.webswing.model.appframe.out;

import org.webswing.model.MsgOut;

public class PasteRequestMsgOut implements MsgOut {
	private static final long serialVersionUID = -1153413346164509155L;
	
	private String title;
	private String message;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
