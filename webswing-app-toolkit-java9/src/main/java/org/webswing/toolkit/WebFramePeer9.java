package org.webswing.toolkit;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.MenuBar;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.peer.FramePeer;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.webswing.toolkit.util.*;

public class WebFramePeer9 extends WebWindowPeer9 implements FramePeer {
    
    private int state;
    
    public WebFramePeer9(Frame t) {
        super(t);
        state=t.getExtendedState();
    }
    
    public void setMenuBar(MenuBar paramMenuBar) {
    }
    
    public void setState(int paramInt) {
        state = paramInt;
        if (state == Frame.MAXIMIZED_BOTH) {
            final JFrame f = (JFrame) target;
            f.setLocation(0, 0);
            final Dimension originalSize = f.getSize();
            final Dimension newSize = Toolkit.getDefaultToolkit().getScreenSize();
            if (!originalSize.equals(newSize)) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        f.setSize(newSize);
                        Util.getWebToolkit().getWindowManager().requestRepaintAfterMove(f, new Rectangle(f.getX(), f.getY(), originalSize.width, originalSize.height));
                    }
                });
            }
        }
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
    
    @Override
    public void show() {
        super.show();
        setState(state);//maximize if necessary
    }
    
    @Override
    public void emulateActivation(boolean activate) {
    }
}
