package org.webswing.toolkit;

import java.awt.*;
import java.awt.peer.*;
import java.util.List;

public class WebDialogPeer9 extends WebWindowPeer9 implements DialogPeer {
    
    public WebDialogPeer9(Dialog t) {
        super(t);
    }
    
    public void blockWindows(List<Window> windows) {
    }
}
