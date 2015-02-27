package org.webswing.model.c2s;

import java.util.List;

import org.webswing.model.MsgIn;

public class UploadedEventMsgIn implements MsgIn {

	private static final long serialVersionUID = 75198619L;
	private List<String> files;
	private String clientId;

	public List<String> getFiles() {
		return files;
	}

	public void setFiles(List<String> files) {
		this.files = files;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

}