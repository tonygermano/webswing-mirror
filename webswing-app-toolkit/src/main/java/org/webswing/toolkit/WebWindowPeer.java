package org.webswing.toolkit;

import java.awt.Dialog;
import java.awt.Window;
import java.awt.peer.WindowPeer;

public class WebWindowPeer extends WebContainerPeer implements WindowPeer {


    //////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////// WebWindowPeer Implementation//////////////////////////////////////////////////
    public WebWindowPeer(Window t) {
        super(t);
    }

    public void toFront() {
        System.out.println("toFront()");
    }

    public void toBack() {
        // TODO Auto-generated method stub

    }

    public void setAlwaysOnTop(boolean paramBoolean) {
        // TODO Auto-generated method stub

    }

    public void updateFocusableWindowState() {
        // TODO Auto-generated method stub

    }

    public boolean requestWindowFocus() {
        // TODO Auto-generated method stub
        return true;
    }

    public void setModalBlocked(Dialog paramDialog, boolean paramBoolean) {
        // TODO Auto-generated method stub

    }

    public void updateMinimumSize() {
        // TODO Auto-generated method stub

    }

    public void updateIconImages() {
        // TODO Auto-generated method stub

    }

    public void setOpacity(float paramFloat) {
        // TODO Auto-generated method stub

    }

    public void setOpaque(boolean paramBoolean) {
        // TODO Auto-generated method stub

    }

    public void repositionSecurityWarning() {
        // TODO Auto-generated method stub

    }

    public void updateWindow() {
        System.out.println("updateWindow");

    }

}
