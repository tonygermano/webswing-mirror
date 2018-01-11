package org.webswing.toolkit;

import java.awt.*;
import java.awt.event.FocusEvent;

public class WebPanelPeer9 extends WebPanelPeer {

    public WebPanelPeer9(Panel panel) {
        super(panel);
    }

    @Override
    public boolean requestFocus(Component lightweightChild, boolean temporary, boolean focusedWindowChangeAllowed, long time, FocusEvent.Cause cause) {
        return WebToolkit9.requestFocus(target,lightweightChild,temporary,focusedWindowChangeAllowed,time,cause);
    }
}
