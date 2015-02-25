package org.webswing.model.s2c;

import org.webswing.model.Msg;

public class WindowMoveActionMsg implements Msg {

	private static final long serialVersionUID = -8935899924614304993L;
	public int sx;
	public int sy;
	public int dx;
	public int dy;
	public int width;
	public int height;

	public WindowMoveActionMsg(int sx, int sy, int dx, int dy, int width, int height) {
		super();
		this.sx = sx;
		this.sy = sy;
		this.dx = dx;
		this.dy = dy;
		this.width = width;
		this.height = height;
	}

}
