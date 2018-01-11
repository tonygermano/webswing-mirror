package org.webswing.toolkit;

import org.webswing.applet.WebAppletContext;
import org.webswing.toolkit.util.Util;
import sun.awt.CausedFocusEvent;

import java.applet.Applet;
import java.awt.*;

public class WebDialogPeer7 extends WebDialogPeer {
    public WebDialogPeer7(Dialog paramDialog) {
        super(paramDialog);
    }

    @Override
    public boolean requestFocus(Component lightweightChild, boolean temporary, boolean focusedWindowChangeAllowed, long time, CausedFocusEvent.Cause cause) {
        return WebToolkit7.requestFocus(target,lightweightChild,temporary,focusedWindowChangeAllowed,time,cause);
    }
}
