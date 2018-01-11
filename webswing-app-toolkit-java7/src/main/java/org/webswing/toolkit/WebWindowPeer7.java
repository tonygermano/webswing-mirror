package org.webswing.toolkit;

import sun.awt.CausedFocusEvent;

import java.awt.*;

public class WebWindowPeer7 extends WebWindowPeer {
    public WebWindowPeer7(Window paramWindow) {
        super(paramWindow);
    }

    @Override
    public boolean requestFocus(Component lightweightChild, boolean temporary, boolean focusedWindowChangeAllowed, long time, CausedFocusEvent.Cause cause) {
        return WebToolkit7.requestFocus(target,lightweightChild,temporary,focusedWindowChangeAllowed,time,cause);
    }
}
