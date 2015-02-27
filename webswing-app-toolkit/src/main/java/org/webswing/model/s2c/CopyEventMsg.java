package org.webswing.model.s2c;

import org.webswing.model.Msg;

public class CopyEventMsg implements Msg {

	private static final long serialVersionUID = -5791089710920190332L;
	private String content;

	public CopyEventMsg(String content) {
		super();
		this.content = content;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

}
