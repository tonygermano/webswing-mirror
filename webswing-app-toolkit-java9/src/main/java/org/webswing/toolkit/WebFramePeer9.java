package org.webswing.toolkit;

import java.awt.*;
import java.awt.event.FocusEvent;

public class WebFramePeer9 extends WebFramePeer {

    public WebFramePeer9(Frame frame) {
        super(frame);
    }

    @Override
    public boolean requestFocus(Component lightweightChild, boolean temporary, boolean focusedWindowChangeAllowed, long time, FocusEvent.Cause cause) {
        return WebToolkit9.requestFocus(target,lightweightChild,temporary,focusedWindowChangeAllowed,time,cause);
    }
}
