package org.webswing.toolkit;

import sun.awt.CausedFocusEvent;

import java.awt.*;

public class WebFileDialogPeer8 extends WebFileDialogPeer {
    public WebFileDialogPeer8(FileDialog paramFileDialog) {
        super(paramFileDialog);
    }


    @Override
    public boolean requestFocus(Component lightweightChild, boolean temporary, boolean focusedWindowChangeAllowed, long time, CausedFocusEvent.Cause cause) {
        return WebToolkit8.requestFocus(target,lightweightChild,temporary,focusedWindowChangeAllowed,time,cause);
    }
}
