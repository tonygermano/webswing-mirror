package org.webswing.toolkit;

import sun.awt.CausedFocusEvent;

import java.awt.*;

public class WebPanelPeer8 extends WebPanelPeer {
    public WebPanelPeer8(Panel panel) {
        super(panel);
    }

    @Override
    public boolean requestFocus(Component lightweightChild, boolean temporary, boolean focusedWindowChangeAllowed, long time, CausedFocusEvent.Cause cause) {
        return WebToolkit8.requestFocus(target,lightweightChild,temporary,focusedWindowChangeAllowed,time,cause);
    }
}
