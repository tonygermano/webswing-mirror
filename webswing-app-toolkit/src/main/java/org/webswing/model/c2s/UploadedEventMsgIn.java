package org.webswing.model.c2s;

import java.util.List;

import org.webswing.model.MsgIn;

public class UploadedEventMsgIn implements MsgIn {

	private static final long serialVersionUID = 75198619L;
	public List<String> files;
	public String clientId;

}