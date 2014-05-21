package org.webswing.toolkit;

import java.awt.Frame;
import java.awt.MenuBar;
import java.awt.Rectangle;
import java.awt.peer.FramePeer;

public class WebFramePeer extends WebWindowPeer implements FramePeer {

    private int state;

    public WebFramePeer(Frame t) {
        super(t);
    }

    public void setMenuBar(MenuBar paramMenuBar) {
    }

    public void setState(int paramInt) {
        state = paramInt;
    }

    public int getState() {
        return state;
    }

    public void setMaximizedBounds(Rectangle paramRectangle) {
    }

    public void setBoundsPrivate(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    }

    public Rectangle getBoundsPrivate() {
        return null;
    }

}
