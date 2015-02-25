package org.webswing.model.c2s;

import org.webswing.model.MsgIn;

public class MouseEventMsgIn implements MsgIn {
	private static final long serialVersionUID = 8411036255812103478L;

	public enum Type {
		mousemove, mousedown, mouseup, mousewheel, dblclick;
	}

	public String clientId;
	public int x;
	public int y;
	public Type type;
	public int wheelDelta;
	public int button;
	public boolean ctrl;
	public boolean alt;
	public boolean shift;
	public boolean meta;

}
