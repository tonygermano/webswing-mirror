package org.webswing.toolkit.api.component;

import javax.swing.JPanel;

import org.webswing.toolkit.api.action.WebWindow;
import org.webswing.toolkit.api.action.WebWindowActionListener;

public abstract class HtmlPanel extends JPanel implements WebWindow {

	private static final long serialVersionUID = 1601744731001086393L;

	public abstract void addWebWindowActionListener(WebWindowActionListener listener);
	public abstract void removeWebWindowActionListener(WebWindowActionListener listener);
	
}
