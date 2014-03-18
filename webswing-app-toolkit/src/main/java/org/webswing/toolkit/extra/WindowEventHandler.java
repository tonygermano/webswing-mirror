package org.webswing.toolkit.extra;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.Map;

import org.webswing.common.WindowActionType;

public class WindowEventHandler {

    private Map<Window, Rectangle> previousSize = new HashMap<Window, Rectangle>();
    private boolean lockedOnEvent;
    private WindowActionType lockEventType;
    private Point referenceMouseLocation;
    private Window window;
    private static final Dimension DEFAULT_MINIMUM_WINDOW_SIZE = new Dimension(100, 25);

    public void handle(WindowActionType wat, MouseEvent e) {
        if (!lockedOnEvent) {
            if (MouseEvent.MOUSE_PRESSED == e.getID()) {
                lockedOnEvent = true;
                lockEventType = wat;
                referenceMouseLocation = e.getPoint();
                window = (Window) e.getSource();
            }
        } else {
            switch (lockEventType) {
                case minimize:
                    if (MouseEvent.MOUSE_RELEASED == e.getID()) {
                        Window w = (Window) e.getSource();
                        Rectangle o = w.getBounds();
                        resizeWindow(w, 0, 0);
                        if (o.getSize().equals(w.getSize()) && previousSize.containsKey(w)) {
                            //restore previous size
                            o = previousSize.get(w);
                            moveWindow(w, o.x, o.y);
                            resizeWindow(w, o.width, o.height);
                        } else {
                            Rectangle r = w.getBounds();
                            //move to the middle of original size
                            moveWindow(w, (o.x + (o.width / 2)) - (r.width / 2), (o.y + (o.height / 2)) - (r.height / 2));
                        }
                        previousSize.put(w, o);
                        lockedOnEvent = false;
                    }
                    break;
                case maximize:
                    if (MouseEvent.MOUSE_RELEASED == e.getID()) {
                        if (wat.equals(WindowActionType.maximize)) {
                            Window w = (Window) e.getSource();
                            Rectangle o = w.getBounds();
                            Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
                            if (o.x == 0 && o.y == 0 && o.width == size.width && o.height == size.height && previousSize.containsKey(w)) {
                                //restore previous size
                                o=previousSize.get(w);
                                moveWindow(w, o.x, o.y);
                                resizeWindow(w, o.width, o.height);
                            } else {
                                moveWindow(w, 0, 0);
                                resizeWindow(w, size.width, size.height);
                            }
                            previousSize.put(w, o);
                        }
                        lockedOnEvent = false;
                    }
                    break;
                case close:
                    if (MouseEvent.MOUSE_RELEASED == e.getID()) {
                        if (wat.equals(WindowActionType.close)) {
                            Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(new WindowEvent((Window) e.getSource(), WindowEvent.WINDOW_CLOSING));
                        }
                        lockedOnEvent = false;
                    }
                    break;
                case move:
                    if (MouseEvent.MOUSE_RELEASED == e.getID()) {
                        Window w = (Window) e.getSource();
                        previousSize.remove(w);
                        lockedOnEvent = false;
                    }
                    if (MouseEvent.MOUSE_DRAGGED == e.getID()) {
                        Window w = (Window) e.getSource();
                        moveWindow(w, e.getXOnScreen() - referenceMouseLocation.x, e.getYOnScreen() - referenceMouseLocation.y);
                    }
                    break;
                case resizeUni:
                    if (MouseEvent.MOUSE_RELEASED == e.getID()) {
                        Window w = (Window) e.getSource();
                        previousSize.remove(w);
                        lockedOnEvent = false;
                    }
                    if (MouseEvent.MOUSE_DRAGGED == e.getID()) {
                        Window w = (Window) e.getSource();
                        resizeWindow(w, e.getXOnScreen() - w.getX(), e.getYOnScreen() - w.getY());
                    }
                    break;
                case resizeRight:
                    if (MouseEvent.MOUSE_RELEASED == e.getID()) {
                        Window w = (Window) e.getSource();
                        previousSize.remove(w);
                        lockedOnEvent = false;
                    }
                    if (MouseEvent.MOUSE_DRAGGED == e.getID()) {
                        Window w = (Window) e.getSource();
                        resizeWindow(w, e.getXOnScreen() - w.getX(), w.getSize().height);
                    }
                    break;
                case resizeBottom:
                    if (MouseEvent.MOUSE_RELEASED == e.getID()) {
                        Window w = (Window) e.getSource();
                        previousSize.remove(w);
                        lockedOnEvent = false;
                    }
                    if (MouseEvent.MOUSE_DRAGGED == e.getID()) {
                        Window w = (Window) e.getSource();
                        resizeWindow(w, w.getSize().width, e.getYOnScreen() - w.getY());
                    }
                    break;
                default:
                    lockedOnEvent = false;

            }
        }
    }

    private void moveWindow(Window w, int x, int y) {
        Rectangle originalPos = new Rectangle(w.getLocation().x, w.getLocation().y, w.getWidth(), w.getHeight());
        w.setLocation(x, y);
        //TODO:optimize on client side and remove repaint call
        WindowManager.getInstance().requestRepaintAfterMove(originalPos);
        w.repaint();
    }

    private void resizeWindow(Window w, int width, int height) {
        Dimension originalSize = w.getSize();
        Dimension newSize = new Dimension(width, height);
        validateSize(w, newSize);
        if (!originalSize.equals(newSize)) {
            w.setSize(newSize);
            WindowManager.getInstance().requestRepaintAfterMove(new Rectangle(w.getX(), w.getY(), originalSize.width, originalSize.height));
            w.invalidate();
            w.validate();
            w.repaint();
        }
    }

    private void validateSize(Window w, Dimension newSize) {
        Dimension min = w.getMinimumSize();
        newSize.width = Math.max(min.width, newSize.width);
        newSize.width = Math.max(DEFAULT_MINIMUM_WINDOW_SIZE.width, newSize.width);
        newSize.height = Math.max(min.height, newSize.height);
        newSize.height = Math.max(DEFAULT_MINIMUM_WINDOW_SIZE.height, newSize.height);
    }

    public boolean isEventHandlingLocked() {
        return lockedOnEvent;
    }

    public Window getLockedToWindow() {
        return window;
    }
}
