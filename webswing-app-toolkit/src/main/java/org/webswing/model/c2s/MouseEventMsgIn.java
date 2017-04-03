package org.webswing.model.c2s;

import org.webswing.model.MsgIn;

public class MouseEventMsgIn implements MsgIn {
	private static final long serialVersionUID = 8411036255812103478L;

	public enum MouseEventType {
		mousemove, mousedown, mouseup, mousewheel, dblclick;
	}

	private int x;
	private int y;
	private MouseEventType type;
	private int wheelDelta;
	private int button;
	private int buttons;
	private boolean ctrl;
	private boolean alt;
	private boolean shift;
	private boolean meta;

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public MouseEventType getType() {
		return type;
	}

	public void setType(MouseEventType type) {
		this.type = type;
	}

	public int getWheelDelta() {
		return wheelDelta;
	}

	public void setWheelDelta(int wheelDelta) {
		this.wheelDelta = wheelDelta;
	}

	public int getButton() {
		return button;
	}

	public void setButton(int button) {
		this.button = button;
	}

	public boolean isCtrl() {
		return ctrl;
	}

	public void setCtrl(boolean ctrl) {
		this.ctrl = ctrl;
	}

	public boolean isAlt() {
		return alt;
	}

	public void setAlt(boolean alt) {
		this.alt = alt;
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

	public int getButtons() {
		return buttons;
	}

	public void setButtons(int buttons) {
		this.buttons = buttons;
	}
}
