package org.webswing.toolkit;

import sun.awt.CausedFocusEvent;

import java.awt.*;

public class WebFramePeer7 extends WebFramePeer {
    public WebFramePeer7(Frame frame) {
        super(frame);
    }

    @Override
    public boolean requestFocus(Component lightweightChild, boolean temporary, boolean focusedWindowChangeAllowed, long time, CausedFocusEvent.Cause cause) {
        return WebToolkit7.requestFocus(target,lightweightChild,temporary,focusedWindowChangeAllowed,time,cause);
    }
}
