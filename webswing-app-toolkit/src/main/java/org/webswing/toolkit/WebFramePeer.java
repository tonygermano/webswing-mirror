package org.webswing.toolkit;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.MenuBar;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.peer.FramePeer;

import javax.swing.JFrame;

import org.webswing.toolkit.extra.WindowManager;
import org.webswing.toolkit.util.Util;

public class WebFramePeer extends WebWindowPeer implements FramePeer {

	private int state;

	public WebFramePeer(Frame t) {
		super(t);
	}

	public void setMenuBar(MenuBar paramMenuBar) {
	}

	public void setState(int paramInt) {
		state = paramInt;
		if (state == Frame.MAXIMIZED_BOTH) {
			WindowManager.getInstance().maximizeFrame((JFrame) target);
		}
	}

	public int getState() {
		return state;
	}

	public void setMaximizedBounds(Rectangle paramRectangle) {
	}

	public void setBoundsPrivate(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
	}

	public Rectangle getBoundsPrivate() {
		return null;
	}

}
