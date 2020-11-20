package org.webswing.model.adminconsole.in;

import org.webswing.model.MsgIn;

public class ToggleRecordingMsgIn implements MsgIn {

	private static final long serialVersionUID = -5866985862298757516L;

	private String path;
	private String instanceId;

	public ToggleRecordingMsgIn() {
	}
	
	public ToggleRecordingMsgIn(String path, String instanceId) {
		super();
		this.path = path;
		this.instanceId = instanceId;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getInstanceId() {
		return instanceId;
	}

	public void setInstanceId(String instanceId) {
		this.instanceId = instanceId;
	}
	
}
