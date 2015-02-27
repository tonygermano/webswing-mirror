package org.webswing.model.c2s;

import org.webswing.model.MsgIn;

public class KeyboardEventMsgIn implements MsgIn {

	private static final long serialVersionUID = -896095456169586882L;

	public enum KeyEventType {
		keypress, keydown, keyup;
	}

	private String clientId;
	private KeyEventType type;
	private int character;
	private int keycode;
	private boolean alt;
	private boolean ctrl;
	private boolean shift;
	private boolean meta;
	private boolean altgr;

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public KeyEventType getType() {
		return type;
	}

	public void setType(KeyEventType type) {
		this.type = type;
	}

	public int getCharacter() {
		return character;
	}

	public void setCharacter(int character) {
		this.character = character;
	}

	public int getKeycode() {
		return keycode;
	}

	public void setKeycode(int keycode) {
		this.keycode = keycode;
	}

	public boolean isAlt() {
		return alt;
	}

	public void setAlt(boolean alt) {
		this.alt = alt;
	}

	public boolean isCtrl() {
		return ctrl;
	}

	public void setCtrl(boolean ctrl) {
		this.ctrl = ctrl;
	}

	public boolean isShift() {
		return shift;
	}

	public void setShift(boolean shift) {
		this.shift = shift;
	}

	public boolean isMeta() {
		return meta;
	}

	public void setMeta(boolean meta) {
		this.meta = meta;
	}

	public boolean isAltgr() {
		return altgr;
	}

	public void setAltgr(boolean altgr) {
		this.altgr = altgr;
	}

}
