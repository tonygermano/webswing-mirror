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

    public void setTitle(String paramString) {
        // TODO Auto-generated method stub

    }

    public void setMenuBar(MenuBar paramMenuBar) {
        // TODO Auto-generated method stub

    }

    public void setResizable(boolean paramBoolean) {
        // TODO Auto-generated method stub

    }

    public void setState(int paramInt) {
        state = paramInt;
    }

    public int getState() {
        return state;
    }

    public void setMaximizedBounds(Rectangle paramRectangle) {
        // TODO Auto-generated method stub

    }

    public void setBoundsPrivate(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
        // TODO Auto-generated method stub

    }

    public Rectangle getBoundsPrivate() {
        // TODO Auto-generated method stub
        return null;
    }

}
