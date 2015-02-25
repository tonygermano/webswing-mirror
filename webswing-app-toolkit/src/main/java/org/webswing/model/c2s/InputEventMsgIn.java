package org.webswing.model.c2s;

import org.webswing.model.MsgIn;

public class InputEventMsgIn implements MsgIn {

	private static final long serialVersionUID = -6578454357790442182L;
	public ConnectionHandshakeMsgIn handshake;
	public KeyboardEventMsgIn key;
	public MouseEventMsgIn mouse;
	public SimpleEventMsgIn event;
}
