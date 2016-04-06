package org.webswing.toolkit;

import java.awt.Component;
import java.awt.Container;
import java.awt.Insets;
import java.awt.Panel;
import java.awt.Point;
import java.awt.Window;
import java.awt.peer.PanelPeer;

import javax.swing.SwingUtilities;

public class WebPanelPeer extends WebContainerPeer implements PanelPeer {

	private Insets insets;

	public WebPanelPeer(Container t) {
		super(t);
		insets = new Insets(0, 0, 0, 0);
	}

	@Override
	public String getGuid() {
		return getParentWindowPeer().getGuid();
	}

	@Override
	public Insets getInsets() {
		return insets;
	}

	@SuppressWarnings("deprecation")
	WebWindowPeer getParentWindowPeer() {
		Panel target = (Panel) getTarget();
		Window w = SwingUtilities.windowForComponent(target);
		if (w != null && w.getPeer() != null) {
			return (WebWindowPeer) w.getPeer();
		}
		return null;
	}

	@Override
	public void show() {
		getParentWindowPeer().addHwLayer(this);
	}

	@Override
	public void hide() {
		getParentWindowPeer().removeHwLayer(this);
	}

	public Point getOffset() {
		Point p = new Point(0,0);
		return SwingUtilities.convertPoint((Component) target, p, (Component) getParentWindowPeer().getTarget());
	}
	
}
