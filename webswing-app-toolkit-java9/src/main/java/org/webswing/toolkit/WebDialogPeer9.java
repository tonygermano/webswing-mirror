package org.webswing.toolkit;

import java.awt.*;
import java.awt.event.FocusEvent;

public class WebDialogPeer9 extends WebDialogPeer {

    public WebDialogPeer9(Dialog t) {
        super(t);
    }

    @Override
    public boolean requestFocus(Component lightweightChild, boolean temporary, boolean focusedWindowChangeAllowed, long time, FocusEvent.Cause cause) {
        return WebToolkit9.requestFocus(target,lightweightChild,temporary,focusedWindowChangeAllowed,time,cause);
    }
}
