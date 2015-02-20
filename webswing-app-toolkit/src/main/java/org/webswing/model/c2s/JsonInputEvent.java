package org.webswing.model.c2s;

public class JsonInputEvent implements JsonEvent{

	private static final long serialVersionUID = -6578454357790442182L;
	public JsonConnectionHandshake handshake;
	public JsonEventKeyboard key;
	public JsonEventMouse	mouse;
}
