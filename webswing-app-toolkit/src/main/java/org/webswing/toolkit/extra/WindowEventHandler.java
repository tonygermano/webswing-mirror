package org.webswing.toolkit.extra;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;

import org.webswing.common.WindowActionType;

public class WindowEventHandler {

    private boolean lockedOnEvent;
    private WindowActionType lockEventType;
    private Point referenceMouseLocation;
    private Window window;

    public void handle(WindowActionType wat, MouseEvent e) {
        if (!lockedOnEvent) {
            if (MouseEvent.MOUSE_PRESSED == e.getID()) {
                lockedOnEvent = true;
                lockEventType = wat;
                referenceMouseLocation = e.getPoint();
                window=(Window) e.getSource();
            }
        } else {
            switch (lockEventType) {
                case close:
                    if (wat.equals(WindowActionType.close) && MouseEvent.MOUSE_RELEASED == e.getID()) {
                        Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(new WindowEvent((Window) e.getSource(), WindowEvent.WINDOW_CLOSING));
                        lockedOnEvent = false;
                    }
                    break;
                case move:
                    if ( MouseEvent.MOUSE_RELEASED == e.getID()) {
                        lockedOnEvent = false;
                    }
                    if ( MouseEvent.MOUSE_DRAGGED == e.getID()) {
                        Window w = (Window) e.getSource();
                        Rectangle originalPos = new Rectangle(w.getLocation().x,w.getLocation().y,w.getWidth(),w.getHeight());
                        w.setLocation(e.getXOnScreen()-referenceMouseLocation.x,e.getYOnScreen()-referenceMouseLocation.y);
                        //TODO:optimize on client side and remove repaint call
                        WindowManager.getInstance().requestRepaintAfterMove(originalPos);
                        w.repaint();
                    }
                    break;
                case resizeUni:
                default:
                    lockedOnEvent = false;

            }
        }
    }

    public boolean isEventHandlingLocked() {
        return lockedOnEvent;
    }

    public Window getLockedToWindow() {
        return window;
    }
}
