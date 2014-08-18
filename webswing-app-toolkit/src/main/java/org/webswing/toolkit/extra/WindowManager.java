package org.webswing.toolkit.extra;

import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.FocusEvent;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.util.List;
import java.util.Map;

import javax.swing.SwingUtilities;

import org.webswing.common.WindowActionType;
import org.webswing.dispatch.WebEventDispatcher;
import org.webswing.dispatch.WebPaintDispatcher;
import org.webswing.model.s2c.JsonCursorChange;
import org.webswing.toolkit.WebComponentPeer;
import org.webswing.toolkit.WebToolkit;
import org.webswing.util.Util;

public class WindowManager {

    private static WindowManager singleton = null;
    private WindowHierarchyTree zorder = new WindowHierarchyTree();
    private Window activeWindow = null;
    private WindowEventHandler eventhandler = new WindowEventHandler();
    private String currentCursor = JsonCursorChange.DEFAULT_CURSOR;

    private WindowManager() {
    }

    public static WindowManager getInstance() {
        if (singleton == null) {
            singleton = new WindowManager();
        }
        return singleton;
    }

    public void bringToFront(Window w) {
        synchronized (WebPaintDispatcher.webPaintLock) {
            if ((w == null || w.isFocusableWindow()) && activeWindow != w) {
                Window oldActiveWindow = activeWindow;
                activeWindow = w;
                if (activeWindow != null) {
                    WindowEvent gainedFocusWindowEvent = new WindowEvent(activeWindow, WindowEvent.WINDOW_GAINED_FOCUS, activeWindow, 0, 0);
                    WebEventDispatcher.dispatchEventInSwing(activeWindow, gainedFocusWindowEvent);
                    WebComponentPeer activeWindowPeer = (WebComponentPeer) WebToolkit.targetToPeer(activeWindow);
                    activeWindowPeer.updateWindowDecorationImage();
                    Util.getWebToolkit().getPaintDispatcher().notifyWindowRepaint(activeWindow);
                }
                if (oldActiveWindow != null) {
                    WebComponentPeer oldActiveWindowPeer = (WebComponentPeer) WebToolkit.targetToPeer(oldActiveWindow);
                    oldActiveWindowPeer.updateWindowDecorationImage();
                    Util.getWebToolkit().getPaintDispatcher().notifyWindowRepaint(oldActiveWindow);
                }
            }
            if (w != null) {
                zorder.bringToFront(w);
            }
        }
    }

    public void removeWindow(Window target) {
        synchronized (WebPaintDispatcher.webPaintLock) {
            if(target==activeWindow){
                activeWindow=null;
            }
            zorder.removeWindow(target);
        }
    }

    public void bringToBack(Window w) {
        synchronized (WebPaintDispatcher.webPaintLock) {
            //            w.setAlwaysOnTop(false);
            //            removeWindow(w);
            //            zorder.add(w);
            //            w.repaint();
        }
    }

    public Window getActiveWindow() {
        return activeWindow;
    }

    public boolean isWindowActive(Window w) {
        if (activeWindow == w) {
            return true;
        } else {
            return false;
        }
    }

    public void activateWindow(Window w) {
        activateWindow(w, 0, 0);
    }

    @SuppressWarnings("restriction")
    public void activateWindow(Window w, int x, int y) {
        if (!zorder.contains(w)) {
            zorder.addWindow(w);
        }

        //dont allow activation outside modal dialog ancestors
        if (!zorder.isInSameModalBranch(activeWindow, w) && !(w instanceof sun.awt.ModalExclude)) {
            return;
        }
        Component newFocusOwner = SwingUtilities.getDeepestComponentAt(w, x, y);
        if (newFocusOwner != null && newFocusOwner.isFocusable() && w.isFocusableWindow()) {
            FocusEvent gainedFocusEvent = new FocusEvent(newFocusOwner, FocusEvent.FOCUS_GAINED, false);
            WebEventDispatcher.dispatchEventInSwing(w, gainedFocusEvent);
        }
        if (SwingUtilities.isRectangleContainingRectangle(new Rectangle(0,0,w.getWidth(),w.getHeight()), new Rectangle(x, y, 0, 0))) {
            bringToFront(w);
        } else {
            bringToFront(null);
        }
    }

    public Window getVisibleWindowOnPosition(int x, int y) {
        Window positionWin = zorder.getVisibleWindowOnPosition(x, y);
        if (positionWin == null) {
            positionWin = activeWindow;
        }
        return positionWin;
    }

    public Map<String, List<Rectangle>> extractNonVisibleAreas() {
        return zorder.extractNonVisibleAreas();
    }

    public void requestRepaintAfterMove(Window w, Rectangle originalPosition) {
        zorder.requestRepaintAfterMove(w, originalPosition);
    }

    public void handleWindowDecorationEvent(Window w, MouseEvent e) {
        WindowActionType wat = Util.getWebToolkit().getImageService().getWindowDecorationTheme().getAction(w, new Point(e.getX(), e.getY()));
        eventhandler.handle(wat, e);
    }

    public boolean isLockedToWindowDecorationHandler() {
        return eventhandler.isEventHandlingLocked();
    }

    public Window getLockedToWindow() {
        return eventhandler.getLockedToWindow();
    }

    public String getCurrentCursor() {
        return currentCursor;
    }

    public void setCurrentCursor(String currentCursor) {
        this.currentCursor = currentCursor;
    }

}
