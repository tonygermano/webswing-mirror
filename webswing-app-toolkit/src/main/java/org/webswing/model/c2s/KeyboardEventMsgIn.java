package org.webswing.model.c2s;

import org.webswing.model.MsgIn;

public class KeyboardEventMsgIn implements MsgIn {

	private static final long serialVersionUID = -896095456169586882L;

	public enum Type {
		keypress, keydown, keyup;
	}

	public String clientId;
	public Type type;
	public int character;
	public int keycode;
	public boolean alt;
	public boolean ctrl;
	public boolean shift;
	public boolean meta;
	public boolean altgr;

}
