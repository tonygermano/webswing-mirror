package org.webswing.model.appframe.out;

import org.webswing.model.MsgOut;

public class PlaybackInfoMsgOut implements MsgOut {
	private static final long serialVersionUID = -2332725867134258277L;
	private int current;
	private int total;

	public int getCurrent() {
		return current;
	}

	public void setCurrent(int current) {
		this.current = current;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

}
