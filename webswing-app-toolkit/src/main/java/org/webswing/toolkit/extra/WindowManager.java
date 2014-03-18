package org.webswing.toolkit.extra;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.FocusEvent;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.JWindow;
import javax.swing.SwingUtilities;

import org.webswing.common.WindowActionType;
import org.webswing.dispatch.WebEventDispatcher;
import org.webswing.dispatch.WebPaintDispatcher;
import org.webswing.toolkit.WebToolkit;
import org.webswing.toolkit.WebWindowPeer;
import org.webswing.util.Util;

public class WindowManager {

    private static WindowManager singleton = null;
    LinkedList<Window> zorder = new LinkedList<Window>();
    Window activeWindow = null;
    WindowEventHandler eventhandler = new WindowEventHandler();

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
            zorder.remove(w);
            if (w.isAlwaysOnTop()) {
                zorder.addFirst(w);
            } else {
                Window lastAlwaysOnTopWindow = null;
                for (Window z : zorder) {
                    if (z.isAlwaysOnTop()) {
                        lastAlwaysOnTopWindow = z;
                    }
                }
                if (lastAlwaysOnTopWindow != null) {
                    zorder.add(zorder.indexOf(lastAlwaysOnTopWindow) + 1, w);
                } else {
                    zorder.addFirst(w);
                }
            }
            if (!(w instanceof JWindow)) {
                activeWindow = w;
            }
            WindowEvent gainedFocusWindowEvent = new WindowEvent(w, WindowEvent.WINDOW_GAINED_FOCUS, activeWindow, 0, 0);
            WebEventDispatcher.dispatchEventInSwing(w, gainedFocusWindowEvent);
            w.repaint();
            Util.getWebToolkit().getPaintDispatcher().notifyWindowRepaint(w);
        }
    }

    public void removeWindow(Window target) {
        synchronized (WebPaintDispatcher.webPaintLock) {
            if (zorder.contains(target)) {
                int index = zorder.indexOf(target);
                if (target.isAlwaysOnTop()) {
                    index = 0;
                }
                Rectangle bounds = target.getBounds();
                zorder.remove(target);
                requestRepaintUnderlying(index, bounds);
                if (index == 0 && zorder.size() > 0) {
                    activateWindow(zorder.get(0));
                }
            }
        }
    }

    public void bringToBack(Window w) {
        synchronized (WebPaintDispatcher.webPaintLock) {
            w.setAlwaysOnTop(false);
            removeWindow(w);
            zorder.add(w);
            w.repaint();
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

    public void activateWindow(Window w, int x, int y) {
        if (activeWindow != null && activeWindow instanceof java.awt.Dialog && ((java.awt.Dialog) activeWindow).isModal() && activeWindow.isShowing()) {
            if (!(w instanceof java.awt.Dialog) || !((java.awt.Dialog) w).isModal()) {
                w = activeWindow;
            }
        }

        Component newFocusOwner = SwingUtilities.getDeepestComponentAt(w, x, y);
        if (w.getFocusOwner() != newFocusOwner && newFocusOwner != null && newFocusOwner.isFocusable()) {
            FocusEvent gainedFocusEvent = new FocusEvent(newFocusOwner, FocusEvent.FOCUS_GAINED, false);
            WebEventDispatcher.dispatchEventInSwing(w, gainedFocusEvent);
        }
        if (this.activeWindow != w) {
            bringToFront(w);
        }

    }

    public Window getVisibleWindowOnPosition(int x, int y) {
        for (Window w : zorder) {
            if (SwingUtilities.isRectangleContainingRectangle(w.getBounds(), new Rectangle(x, y, 0, 0))) {
                return w;
            }
        }
        return null;
    }

    @SuppressWarnings("restriction")
    public void extractNonVisibleAreas(Map<String, List<Rectangle>> map) {
        if (zorder.size() > 0) {
            for (int i = 1; i < zorder.size() + 1; i++) {
                String id = i == zorder.size() ? WebToolkit.BACKGROUND_WINDOW_ID : ((WebWindowPeer) WebToolkit.targetToPeer(zorder.get(i))).getGuid();
                Rectangle current = i == zorder.size() ? new Rectangle(Util.getWebToolkit().getScreenSize()) : zorder.get(i).getBounds();
                List<Rectangle> currentDifferences = new ArrayList<Rectangle>();
                for (int j = i - 1; j >= 0; j--) {
                    Rectangle previousBounds = zorder.get(j).getBounds();
                    Rectangle intersect = SwingUtilities.computeIntersection(current.x, current.y, current.width, current.height, (Rectangle) previousBounds.clone());
                    if (!intersect.isEmpty()) {
                        currentDifferences.add(intersect);
                    }
                }
                if (currentDifferences.size() != 0) {
                    map.put(id, currentDifferences);
                }
            }
        }
    }

    public void requestRepaintAfterMove(Rectangle originalPosition) {
        requestRepaintUnderlying(0, originalPosition);
    }

    private void requestRepaintUnderlying(int index, Rectangle bounds) {
        for (int i = index; i < zorder.size(); i++) {
            Window underlying = zorder.get(i);
            Rectangle uBounds = underlying.getBounds();
            Rectangle boundsCopy = new Rectangle(bounds);
            SwingUtilities.computeIntersection(uBounds.x, uBounds.y, uBounds.width, uBounds.height, boundsCopy);
            WebWindowPeer peer = (WebWindowPeer) WebToolkit.targetToPeer(underlying);
            Util.getWebToolkit().getPaintDispatcher().notifyWindowAreaRepainted(peer.getGuid(), new Rectangle(boundsCopy.x - uBounds.x, boundsCopy.y - uBounds.y, boundsCopy.width, boundsCopy.height));
        }
        Rectangle boundsCopy = new Rectangle(bounds);
        Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
        SwingUtilities.computeIntersection(0, 0, screensize.width, screensize.height, boundsCopy);
        Util.getWebToolkit().getPaintDispatcher().notifyBackgroundRepainted(boundsCopy);
    }

    public void handleWindowDecorationEvent(Window w, MouseEvent e) {
        WindowActionType wat = Util.getWebToolkit().getImageService().getWindowDecorationTheme().getAction(w, e);
        eventhandler.handle(wat, e);
    }

    public boolean isLockedToWindowDecorationHandler() {
        return eventhandler.isEventHandlingLocked();
    }

    public Window getLockedToWindow() {
        return eventhandler.getLockedToWindow();
    }

}
