package org.webswing.toolkit;

import org.webswing.toolkit.util.Util;
import sun.awt.CausedFocusEvent;

import java.awt.*;
import java.awt.peer.FileDialogPeer;

public class WebFileDialogPeer7 extends WebFileDialogPeer {
    public WebFileDialogPeer7(FileDialog paramFileDialog) {
        super(paramFileDialog);
    }


    @Override
    public boolean requestFocus(Component lightweightChild, boolean temporary, boolean focusedWindowChangeAllowed, long time, CausedFocusEvent.Cause cause) {
        return WebToolkit7.requestFocus(target,lightweightChild,temporary,focusedWindowChangeAllowed,time,cause);
    }
}
