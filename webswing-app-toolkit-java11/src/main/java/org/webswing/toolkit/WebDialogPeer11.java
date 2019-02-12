package org.webswing.toolkit;

import java.awt.*;
import java.awt.event.FocusEvent;

public class WebDialogPeer11 extends WebDialogPeer {

    public WebDialogPeer11(Dialog t) {
        super(t);
    }

    @Override
    public boolean requestFocus(Component lightweightChild, boolean temporary, boolean focusedWindowChangeAllowed, long time, FocusEvent.Cause cause) {
        return WebToolkit11.requestFocus(target,lightweightChild,temporary,focusedWindowChangeAllowed,time,cause);
    }
}
