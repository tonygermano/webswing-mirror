package org.webswing.model.adminconsole.out;

import org.webswing.model.MsgOut;

public class SessionPoolAppMsgOut implements MsgOut {

	private static final long serialVersionUID = 5221688801361445457L;

	private String path;
	private int instanceCount;

	public SessionPoolAppMsgOut() {
	}

	public SessionPoolAppMsgOut(String path, int instanceCount) {
		super();
		this.path = path;
		this.instanceCount = instanceCount;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public int getInstanceCount() {
		return instanceCount;
	}

	public void setInstanceCount(int instanceCount) {
		this.instanceCount = instanceCount;
	}

}
