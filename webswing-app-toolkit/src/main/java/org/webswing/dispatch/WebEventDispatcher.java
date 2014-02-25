package org.webswing.dispatch;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.WindowEvent;

import org.webswing.Constants;
import org.webswing.model.c2s.JsonConnectionHandshake;
import org.webswing.model.c2s.JsonEvent;
import org.webswing.model.c2s.JsonEventKeyboard;
import org.webswing.model.c2s.JsonEventMouse;
import org.webswing.model.c2s.JsonEventWindow;
import org.webswing.toolkit.extra.WindowManager;
import org.webswing.util.Util;

public class WebEventDispatcher {

    private MouseEvent lastMouseEvent;
    private Point lastMousePosition=new Point();

    public void dispatchEvent(JsonEvent event) {
        if (event instanceof JsonEventMouse) {
            dispatchMouseEvent((JsonEventMouse) event);
        }
        if (event instanceof JsonEventWindow) {
            dispatchWindowEvent((JsonEventWindow) event);
        }
        if (event instanceof JsonEventKeyboard) {
            dispatchKeyboardEvent((JsonEventKeyboard) event);
        }
        if (event instanceof JsonConnectionHandshake) {
            JsonConnectionHandshake handshake = (JsonConnectionHandshake) event;
            Util.getWebToolkit().initSize(handshake.desktopWidth, handshake.desktopHeight);
        }
    }

    public void dispatchMessage(String message){
        if (message.equals(Constants.SWING_KILL_SIGNAL)) {
            System.exit(0);
        }
    }
    
    
    private void dispatchWindowEvent(JsonEventWindow event) {
        Window w = (Window) Util.findWindowPeerById(event.windowId).getTarget();
        if (w != null) {
            switch (event.type) {
                case close:
                    dispatchEventInSwing(w, new WindowEvent(w, WindowEvent.WINDOW_CLOSING));
                    //TODO: WindowManager handle
                    break;
                case resize:
                    w.setSize(event.newWidth, event.newHeight);
                    //TODO:windowmanager active
                    break;
            }
        }
    }

    private void dispatchKeyboardEvent(JsonEventKeyboard event) {
        Window w = (Window) WindowManager.getInstance().getActiveWindow();
        if (w != null) {
            long when = System.currentTimeMillis();
            int modifiers = Util.getKeyModifiersAWTFlag(event);
            int type = Util.getKeyType(event.type);
            Component src = w.getFocusOwner() == null ? w : w.getFocusOwner();
            if (type == KeyEvent.KEY_TYPED) {
                AWTEvent e = new KeyEvent(src, KeyEvent.KEY_TYPED, when, modifiers, 0, (char) event.character);
                dispatchEventInSwing(w, e);
            } else {
                AWTEvent e = new KeyEvent(src, type, when, modifiers, event.keycode, (char) event.character);
                dispatchEventInSwing(w, e);
            }
        }
    }

    private void dispatchMouseEvent(JsonEventMouse event) {
        Window w = (Window) WindowManager.getInstance().getVisibleWindowOnPosition(event.x, event.y);
        if(w==null){
            return;
        }
        if (w != null) {
            MouseEvent e = null;
            int x = event.x-w.getX();
            int y = event.y-w.getY();
            lastMousePosition.x=x;
            lastMousePosition.y=y;
            long when = System.currentTimeMillis();
            int modifiers = Util.getMouseModifiersAWTFlag(event);
            int id = 0;
            int clickcount = 0;
            int buttons = Util.getMouseButtonsAWTFlag(event.button);
            if(buttons!=0){ 
                WindowManager.getInstance().activateWindow(w,x,y);
            }
            switch (event.type) {
                case mousemove:
                    id = event.button == 1 ? MouseEvent.MOUSE_DRAGGED : MouseEvent.MOUSE_MOVED;
                    e = new MouseEvent(w, id, when, modifiers, x, y, x, y, clickcount, false, buttons);
                    dispatchEventInSwing(w, e);
                    lastMouseEvent = e;
                    break;
                case mouseup:
                    id = MouseEvent.MOUSE_RELEASED;
                    boolean popupTrigger = (buttons == 3) ? true : false;
                    clickcount = 1;
                    e = new MouseEvent(w, id, when, modifiers, x, y, x, y, clickcount, popupTrigger, buttons);
                    dispatchEventInSwing(w, e);
                    if (lastMouseEvent != null && lastMouseEvent.getID() == MouseEvent.MOUSE_PRESSED && lastMouseEvent.getX() == x && lastMouseEvent.getY() == y) {
                        e = new MouseEvent(w, MouseEvent.MOUSE_CLICKED, when, modifiers, x, y, x, y, clickcount, popupTrigger, buttons);
                        dispatchEventInSwing(w, e);
                        lastMouseEvent = null;
                    }
                    break;
                case mousedown:
                    id = MouseEvent.MOUSE_PRESSED;
                    clickcount = 1;
                    e = new MouseEvent(w, id, when, modifiers, x, y, x, y, clickcount, false, buttons);
                    dispatchEventInSwing(w, e);
                    lastMouseEvent = e;
                    break;
                case mousewheel:
                    id = MouseEvent.MOUSE_WHEEL;
                    buttons = 0;
                    modifiers = 0;
                    e = new MouseWheelEvent(w, id, when, modifiers, x, y, clickcount, false, MouseWheelEvent.WHEEL_UNIT_SCROLL, 3, event.wheelDelta);
                    dispatchEventInSwing(w, e);
                    lastMouseEvent = e;
                    break;
                case dblclick:
                    e = new MouseEvent(w, MouseEvent.MOUSE_CLICKED, when, modifiers, x, y, x, y, 2, false, buttons);
                    dispatchEventInSwing(w, e);
                    break;
                default:
                    break;
            }
        }
    }

    public Point getLastMousePosition(){
        return lastMousePosition;
    }

    public static void dispatchEventInSwing(final Window w, final AWTEvent e) {
        Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(e);
    }
}
