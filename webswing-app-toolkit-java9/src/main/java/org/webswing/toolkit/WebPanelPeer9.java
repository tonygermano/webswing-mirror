package org.webswing.toolkit;

import java.applet.*;
import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Panel;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.*;
import java.awt.peer.PanelPeer;

import javax.swing.SwingUtilities;

import org.webswing.applet.*;
import org.webswing.common.GraphicsWrapper;
import org.webswing.toolkit.extra.*;
import org.webswing.toolkit.util.*;

public class WebPanelPeer9 extends WebContainerPeer implements PanelPeer {
    
    private Insets insets;
    
    public WebPanelPeer9(Container t) {
        super(t);
        insets = new Insets(0, 0, 0, 0);
    }
    
    @Override
    public String getGuid() {
        return getParentWindowPeer().getGuid();
    }
    
    @Override
    public Insets getInsets() {
        return insets;
    }
    
    @SuppressWarnings("deprecation")
    WebWindowPeer getParentWindowPeer() {
        Panel target = (Panel) getTarget();
        Window w = SwingUtilities.windowForComponent(target);
        if (w != null && w.isDisplayable()) {
            return (WebWindowPeer) WebWindowPeer.getPeerForTarget(w);
        }
        return null;
    }
    
    @Override
    public void show() {
        getParentWindowPeer().addHwLayer(this);
    }
    
    @Override
    public void hide() {
        getParentWindowPeer().removeHwLayer(this);
    }
    
    @Override
    public Graphics getGraphics() {
        GraphicsWrapper g = (GraphicsWrapper) super.getGraphics();
        g.setOffset(SwingUtilities.convertPoint((Component) target, new Point(0, 0), (Component) getParentWindowPeer().getTarget()));
        return g;
    }
    
    @Override
    protected void notifyWindowClosed() {
        //do nothing here
    }
    
    @Override
    protected void notifyWindowBoundsChanged(Rectangle rectangle) {
        //do nothing here
    }
    
    @Override
    public boolean requestFocus(Component paramComponent, boolean temporary, boolean focusedWindowChangeAllowed, long time, FocusEvent.Cause paramCause) {
        if (target instanceof Window) {
            return ((WindowManager9) Util.getWebToolkit().getWindowManager()).activateWindow((Window) target, paramComponent, 0, 0, temporary, focusedWindowChangeAllowed, paramCause);
        } else if (target instanceof Applet) {
            Applet applet = (Applet) target;
            Window window = ((WebAppletContext) applet.getAppletContext()).getContainer();
            return ((WindowManager9) Util.getWebToolkit().getWindowManager()).activateWindow(window, paramComponent, 0, 0, temporary, focusedWindowChangeAllowed, paramCause);
        } else {
            return false;
        }
    }
}
