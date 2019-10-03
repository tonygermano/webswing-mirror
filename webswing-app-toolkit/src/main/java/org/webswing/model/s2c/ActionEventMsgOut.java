package org.webswing.model.s2c;

import org.webswing.model.MsgOut;

public class ActionEventMsgOut implements MsgOut {

	private static final long serialVersionUID = 7638639060975520851L;

	public ActionEventMsgOut() {
	}

	private String windowId;
	private String actionName;
	private String data;
	private byte[] binaryData;

	public String getActionName() {
		return actionName;
	}

	public void setActionName(String actionName) {
		this.actionName = actionName;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}
	
	public byte[] getBinaryData() {
		return binaryData;
	}
	
	public void setBinaryData(byte[] binaryData) {
		this.binaryData = binaryData;
	}

	public String getWindowId() {
		return windowId;
	}

	public void setWindowId(String windowId) {
		this.windowId = windowId;
	}

}
