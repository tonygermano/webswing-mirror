package org.webswing.ignored.common;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JComponent;

import org.webswing.ignored.model.c2s.JsonEvent;
import org.webswing.ignored.model.c2s.JsonEventKeyboard;
import org.webswing.ignored.model.c2s.JsonEventMouse;
import org.webswing.ignored.model.c2s.JsonEventWindow;
import org.webswing.ignored.model.s2c.JsonPaintRequest;
import org.webswing.ignored.model.s2c.JsonWindowInfo;
import org.webswing.ignored.model.s2c.JsonWindowRequest;
import org.webswing.util.Util;


public class PaintManager {

    private static PaintManager paintManager = new PaintManager();
    private ServerJvmConnection jmsService;
    Map<String, Window> windows = new HashMap<String, Window>();
    private MouseEvent lastMouseEvent;

    public PaintManager() {
    
    }

    public static Graphics beforePaintInterceptor(Graphics g, JComponent c) {
        GraphicsWrapper result;
        if (g instanceof GraphicsWrapper) {
            result = (GraphicsWrapper) g;
            if (result.getRootPaintComponent() == null) {
                result.setRootPaintComponent(c);
            }
        } else {
            return g;
        }
        return result;
    }

    public static void afterPaintInterceptor(Graphics g, JComponent c) {
        if (g instanceof GraphicsWrapper) {
            GraphicsWrapper gw = (GraphicsWrapper) g;
            if (Util.getObjectIdentity(gw.getRootPaintComponent()).equals(Util.getObjectIdentity(c))) {
                paintManager.paintToWeb(gw, c);
            }
        }
    }

    public void paintToWeb(GraphicsWrapper gw, JComponent c) {
        BufferedImage img = gw.getImg();
        if (img != null) {
            gw.getWebWindow().addChangesToDiff();
            if (jmsService.isReadyToReceive()) {
                doSendPaintRequest();
            }
        }
    }

    public void doSendPaintRequest() {
        Map<String, Window> copy = new HashMap<String, Window>(windows);
        Map<String, String> paintMap = jmsService.createEncodedPaintMap(copy);
        Map<String, JsonWindowInfo> windowInfoMap = Util.createJsonWindowInfoMap(paintMap.keySet(), copy);
        jmsService.sendJsonObject(new JsonPaintRequest(paintMap, windowInfoMap));
    }

    public void disposeWindow(Window webWindow) {
        windows.remove(Util.getObjectIdentity(webWindow));
    }

    public void registerWindow(Window webWindow) {
        windows.put(Util.getObjectIdentity(webWindow), webWindow);
    }

    public void hideWindowInBrowser(Window webWindow) {
        jmsService.sendJsonObject(new JsonWindowRequest(Util.getObjectIdentity(webWindow)));
    }

    public WebWindow getWebWindow(String guid) {
        return (WebWindow) windows.get(guid);
    }

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
    }

    private void dispatchWindowEvent(JsonEventWindow event) {
        Window w = windows.get(event.windowId);
        if (w != null) {
            dispatchEventInSwing(w, new WindowEvent(w, WindowEvent.WINDOW_CLOSING));
        }
    }

    private void dispatchKeyboardEvent(JsonEventKeyboard event) {
        Window w = windows.get(event.windowId);
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
        Window w = windows.get(event.windowId);
        if (w != null) {
            MouseEvent e = null;
            WebWindow ww = (WebWindow) w;
            int x = event.x + ww.getFrameTranslation().x;
            int y = event.y + ww.getFrameTranslation().y;
            long when = System.currentTimeMillis();
            int modifiers = Util.getMouseModifiersAWTFlag(event);
            int id = 0;
            int clickcount = 0;
            int buttons = Util.getMouseButtonsAWTFlag(event.button);
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

    private void dispatchEventInSwing(final Window w, final AWTEvent e) {
        Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(e);
    }

    public static PaintManager getInstance() {
        return paintManager;
    }
    
    public void notifyShutDown(){
        jmsService.sendShutdownNotification();
    }

    public Map<String, Window> getWindows() {
        return windows;
    }

    
    public ServerJvmConnection getJmsService() {
        return jmsService;
    }

    
    public void setJmsService(ServerJvmConnection jmsService) {
        this.jmsService = jmsService;
    }
    
    
    
}
