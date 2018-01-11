package org.webswing.toolkit;

import sun.awt.CausedFocusEvent;

import java.awt.*;

public class WebDialogPeer8 extends WebDialogPeer {
    public WebDialogPeer8(Dialog paramDialog) {
        super(paramDialog);
    }

    @Override
    public boolean requestFocus(Component lightweightChild, boolean temporary, boolean focusedWindowChangeAllowed, long time, CausedFocusEvent.Cause cause) {
        return WebToolkit8.requestFocus(target,lightweightChild,temporary,focusedWindowChangeAllowed,time,cause);
    }
}
