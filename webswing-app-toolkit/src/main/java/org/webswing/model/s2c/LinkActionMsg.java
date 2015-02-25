package org.webswing.model.s2c;

import org.webswing.model.Msg;

public class LinkActionMsg implements Msg {

	private static final long serialVersionUID = -1341111272614100725L;

	public enum LinkActionType {
		file, url, print;
	}

	public LinkActionType action;
	public String url;

	public LinkActionMsg(LinkActionType action, String url) {
		super();
		this.action = action;
		this.url = url;
	}

}
