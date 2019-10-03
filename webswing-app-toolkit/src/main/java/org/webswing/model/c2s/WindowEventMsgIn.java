package org.webswing.model.c2s;

import org.webswing.model.MsgIn;

public class WindowEventMsgIn implements MsgIn {

	private static final long serialVersionUID = -4269267759304268713L;

	private String id;
	private int x;
	private int y;
	private int width;
	private int height;
	private boolean close;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

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

	public int getWidth() {
		return width;
	}
	
	public void setWidth(int width) {
		this.width = width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public void setHeight(int height) {
		this.height = height;
	}

	public boolean isClose() {
		return close;
	}

	public void setClose(boolean close) {
		this.close = close;
	}

}
