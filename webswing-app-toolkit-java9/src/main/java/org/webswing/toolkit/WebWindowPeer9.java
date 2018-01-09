package org.webswing.toolkit;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;

import org.webswing.applet.*;
import org.webswing.toolkit.extra.*;
import org.webswing.toolkit.util.*;

public class WebWindowPeer9 extends WebWindowPeer {
    
    public WebWindowPeer9(Window t) {
        super(t);
    }
    
    @Override
    public boolean requestFocus(Component paramComponent, boolean temporary, boolean focusedWindowChangeAllowed, long time, FocusEvent.Cause paramCause) {
        if (target instanceof Window) {
            return ((WindowManager9) Util.getWebToolkit().getWindowManager()).activateWindow((Window) target, paramComponent, 0, 0, temporary, focusedWindowChangeAllowed, paramCause);
        } else if (target instanceof Applet) {
            Applet applet = (Applet) target;
            Window window = ((WebAppletContext) applet.getAppletContext()).getContainer();
            return ((WindowManager9) Util.getWebToolkit().getWindowManager()).activateWindow(window, paramComponent, 0, 0, temporary, focusedWindowChangeAllowed, paramCause);
        } else {
            return false;
        }
    }
}
