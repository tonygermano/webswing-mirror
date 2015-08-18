package org.webswing.model.c2s;

import org.webswing.model.MsgIn;

public class PasteEventMsgIn implements MsgIn {

	private static final long serialVersionUID = -2857821597134135941L;
	private String text;
	private String html;
	private String img;

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getHtml() {
		return html;
	}

	public void setHtml(String html) {
		this.html = html;
	}

	public String getImg() {
		return img;
	}

	public void setImg(String img) {
		this.img = img;
	}

}
