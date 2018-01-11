package org.webswing.toolkit;

import java.awt.Component;
import java.awt.KeyboardFocusManager;
import java.awt.Window;
import java.awt.peer.KeyboardFocusManagerPeer;
import java.lang.reflect.Method;

import org.webswing.toolkit.util.Util;
import org.webswing.toolkit.util.Logger;

import sun.awt.SunToolkit;

@SuppressWarnings("restriction")
public class WebKeyboardFocusManagerPeer implements KeyboardFocusManagerPeer {

	@Override
	public void clearGlobalFocusOwner(Window activeWindow) {
	}

	@Override
	public Component getCurrentFocusOwner() {
		return Util.getWebToolkit().getWindowManager().getActiveWindow().getFocusOwner();
	}

	@Override
	public Window getCurrentFocusedWindow() {
		return Util.getWebToolkit().getWindowManager().getActiveWindow();
	}

	@Override
	public void setCurrentFocusOwner(Component comp) {

	}

	@Override
	public void setCurrentFocusedWindow(Window win) {

	}

}
