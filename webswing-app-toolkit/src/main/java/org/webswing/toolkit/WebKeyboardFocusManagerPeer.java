package org.webswing.toolkit;

import java.awt.Component;
import java.awt.Window;
import java.awt.peer.KeyboardFocusManagerPeer;

import org.webswing.toolkit.extra.WindowManager;

public class WebKeyboardFocusManagerPeer implements KeyboardFocusManagerPeer {

    @Override
    public void clearGlobalFocusOwner(Window activeWindow) {
    }

    @Override
    public Component getCurrentFocusOwner() {
        if (WindowManager.getInstance().getActiveWindow() != null) {
            return WindowManager.getInstance().getActiveWindow().getFocusOwner();
        } else {
            return null;
        }
    }

    @Override
    public Window getCurrentFocusedWindow() {
        return WindowManager.getInstance().getActiveWindow();
    }

    @Override
    public void setCurrentFocusOwner(Component comp) {

    }

    @Override
    public void setCurrentFocusedWindow(Window win) {

    }

}
