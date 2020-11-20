package org.webswing.model.adminconsole.in;

import org.webswing.model.MsgIn;

public class StopAppMsgIn implements MsgIn {

	private static final long serialVersionUID = 4359795607415450588L;
	
	private String path;

	public StopAppMsgIn() {
	}
	
	public StopAppMsgIn(String path) {
		this.path = path;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
	
}
