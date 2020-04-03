package org.webswing.model.s2c;

import org.webswing.model.Msg;

public class WindowDockMsg implements Msg {

	private static final long serialVersionUID = -5473405345399477117L;

	private String windowId;
	
	public WindowDockMsg() {
	}

	public String getWindowId() {
		return windowId;
	}

	public void setWindowId(String windowId) {
		this.windowId = windowId;
	}

}
