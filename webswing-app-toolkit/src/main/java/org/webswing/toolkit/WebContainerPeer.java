package org.webswing.toolkit;

import java.awt.Container;
import java.awt.Insets;
import java.awt.peer.ContainerPeer;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JWindow;

import org.webswing.toolkit.util.Services;

public class WebContainerPeer extends WebComponentPeer implements ContainerPeer {

	public WebContainerPeer(Container t) {
		super(t);
	}

	public Insets getInsets() {
		if (target != null && target instanceof JWindow) {
			return new Insets(0, 0, 0, 0);
		} else if (target != null && ((target instanceof JFrame && ((JFrame) target).isUndecorated()) || (target instanceof JDialog && ((JDialog) target).isUndecorated()))) {
			return new Insets(0, 0, 0, 0);
		} else {
			return Services.getImageService().getWindowDecorationTheme().getInsets();
		}
	}

	@Override
	public void setBounds(int x, int y, int w, int h, int op) {
		if (op == SET_CLIENT_SIZE) {
			Insets insets = getInsets();
			w = w + insets.left + insets.right;
			h = h + insets.top + insets.bottom;
		}
		super.setBounds(x, y, w, h, op);
	}

	public void beginValidate() {
	}

	public void endValidate() {

	}

	public void beginLayout() {
	}

	public void endLayout() {
	}

	public boolean isPaintPending() {
		return false;
	}

	public void restack() {
		throw new UnsupportedOperationException();
	}

	public boolean isRestackSupported() {
		return false;
	}

	public Insets insets() {
		return getInsets();
	}

}
