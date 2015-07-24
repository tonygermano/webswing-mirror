package org.webswing.model.s2c;

import org.webswing.model.Msg;

public class CopyEventMsg implements Msg {

	private static final long serialVersionUID = -5791089710920190332L;
	private String content;
	private String htmlContent;

	public CopyEventMsg(String content, String html) {
		super();
		this.content = content;
		this.htmlContent = html;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getHtmlContent() {
		return htmlContent;
	}

	public void setHtmlContent(String htmlContent) {
		this.htmlContent = htmlContent;
	}

}
