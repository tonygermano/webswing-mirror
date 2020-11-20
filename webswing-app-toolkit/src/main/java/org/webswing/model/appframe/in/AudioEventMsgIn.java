package org.webswing.model.appframe.in;

import org.webswing.model.MsgIn;

public class AudioEventMsgIn implements MsgIn {

	private static final long serialVersionUID = -4269267759304268713L;

	private String id;
	private boolean stop;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public boolean isStop() {
		return stop;
	}

	public void setStop(boolean stop) {
		this.stop = stop;
	}

}
