package org.webswing.model.appframe.in;

import org.webswing.model.MsgIn;

public class UploadEventMsgIn implements MsgIn {

	private static final long serialVersionUID = -7188733550212761231L;

	private String fileId;

	public String getFileId() {
		return fileId;
	}

	public void setFileId(String fileId) {
		this.fileId = fileId;
	}

}
