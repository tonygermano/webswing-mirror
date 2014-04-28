package org.webswing.dispatch;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import javax.swing.SwingUtilities;
import javax.swing.text.JTextComponent;

import org.webswing.Constants;
import org.webswing.model.c2s.JsonConnectionHandshake;
import org.webswing.model.c2s.JsonEvent;
import org.webswing.model.c2s.JsonEventKeyboard;
import org.webswing.model.c2s.JsonEventMouse;
import org.webswing.model.c2s.JsonEventPaste;
import org.webswing.model.c2s.JsonEventMouse.Type;
import org.webswing.toolkit.WebClipboard;
import org.webswing.toolkit.WebDragSourceContextPeer;
import org.webswing.toolkit.extra.DndEventHandler;
import org.webswing.toolkit.extra.WindowManager;
import org.webswing.util.Logger;
import org.webswing.util.Util;

public class WebEventDispatcher {

    private MouseEvent lastMouseEvent;
    private Point lastMousePosition = new Point();
    private static final DndEventHandler dndHandler = new DndEventHandler();

    private ClipboardOwner owner = new ClipboardOwner() {

        @Override
        public void lostOwnership(Clipboard clipboard, Transferable contents) {
        }
    };

    public void dispatchEvent(JsonEvent event) {
        Logger.debug("WebEventDispatcher.dispatchEvent:", event);

        if (event instanceof JsonEventMouse) {
            dispatchMouseEvent((JsonEventMouse) event);
        }
        if (event instanceof JsonEventKeyboard) {
            dispatchKeyboardEvent((JsonEventKeyboard) event);
        }
        if (event instanceof JsonConnectionHandshake) {
            JsonConnectionHandshake handshake = (JsonConnectionHandshake) event;
            Util.getWebToolkit().initSize(handshake.desktopWidth, handshake.desktopHeight);
        }
        if (event instanceof JsonEventPaste) {
            JsonEventPaste paste = (JsonEventPaste) event;
            handlePasteEvent(paste.content);
        }
    }

    public void dispatchMessage(String message) {
        Logger.debug("WebEventDispatcher.dispatchMessage", message);
        if (message.startsWith(Constants.SWING_KILL_SIGNAL)) {
            Logger.info("Received kill signal. Swing application shutting down.");
            System.exit(0);
        }
        if (message.startsWith(Constants.PAINT_ACK_PREFIX)) {
            Util.getWebToolkit().getPaintDispatcher().clientReadyToReceive();
        }
        if (message.startsWith(Constants.REPAINT_REQUEST_PREFIX)) {
            Util.getWebToolkit().getPaintDispatcher().notifyWindowRepaintAll();
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
                if (event.keycode == 13) {//enter keycode
                    event.keycode = 10;
                    event.character = 10;
                } else if (event.keycode == 46) {//delete keycode
                    event.keycode = 127;
                    event.character = 127;
                }
                AWTEvent e = new KeyEvent(src, type, when, modifiers, event.keycode, (char) event.character, KeyEvent.KEY_LOCATION_STANDARD);

                //filter out ctrl+c for copy
                if (event.type == JsonEventKeyboard.Type.keydown && event.character == 67 && event.ctrl == true && event.altgr == false && event.altgr == false && event.meta == false && event.shift == false) {
                    Transferable copied = Util.getWebToolkit().getSystemSelection().getContents(DataFlavor.stringFlavor);
                    Util.getWebToolkit().getSystemClipboard().setContents(copied, owner);
                }
                if (event.type == JsonEventKeyboard.Type.keydown && event.character == 86 && event.ctrl == true && event.altgr == false && event.altgr == false && event.meta == false && event.shift == false) {
                    //on paste event -do nothing
                } else {
                    dispatchEventInSwing(w, e);
                    if (event.keycode == 32 && event.type == JsonEventKeyboard.Type.keydown) {//space keycode handle press
                        event.type = JsonEventKeyboard.Type.keypress;
                        dispatchEvent(event);
                    }
                }
            }
        }
    }

    private void dispatchMouseEvent(JsonEventMouse event) {
        Window w = null;
        if (WindowManager.getInstance().isLockedToWindowDecorationHandler()) {
            w = WindowManager.getInstance().getLockedToWindow();
        } else {
            w = (Window) WindowManager.getInstance().getVisibleWindowOnPosition(event.x, event.y);
        }
        if (w == null) {
            Util.getWebToolkit().getPaintDispatcher().notifyCursorUpdate(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            return;
        }
        if (w != null) {
            MouseEvent e = null;
            int x = event.x - w.getX();
            int y = event.y - w.getY();
            lastMousePosition.x = event.x;
            lastMousePosition.y = event.y;
            long when = System.currentTimeMillis();
            int modifiers = Util.getMouseModifiersAWTFlag(event);
            int id = 0;
            int clickcount = 0;
            int buttons = Util.getMouseButtonsAWTFlag(event.button);
            if (buttons != 0 && event.type == Type.mousedown) {
                WindowManager.getInstance().activateWindow(w, x, y);
            }
            switch (event.type) {
                case mousemove:
                    id = event.button == 1 ? MouseEvent.MOUSE_DRAGGED : MouseEvent.MOUSE_MOVED;
                    e = new MouseEvent(w, id, when, modifiers, x, y, event.x, event.y, clickcount, false, buttons);
                    dispatchEventInSwing(w, e);
                    break;
                case mouseup:
                    id = MouseEvent.MOUSE_RELEASED;
                    boolean popupTrigger = (buttons == 3) ? true : false;
                    clickcount = computeClickCount(x, y, buttons, false);
                    e = new MouseEvent(w, id, when, modifiers, x, y, event.x, event.y, clickcount, popupTrigger, buttons);
                    dispatchEventInSwing(w, e);
                    if (lastMouseEvent != null && lastMouseEvent.getID() == MouseEvent.MOUSE_PRESSED && lastMouseEvent.getX() == x && lastMouseEvent.getY() == y) {
                        e = new MouseEvent(w, MouseEvent.MOUSE_CLICKED, when, modifiers, x, y, event.x, event.y, clickcount, popupTrigger, buttons);
                        dispatchEventInSwing(w, e);
                        lastMouseEvent = e;
                    } else {
                        lastMouseEvent = e;
                    }
                    break;
                case mousedown:
                    id = MouseEvent.MOUSE_PRESSED;
                    clickcount = computeClickCount(x, y, buttons, true);
                    e = new MouseEvent(w, id, when, modifiers, x, y, event.x, event.y, clickcount, false, buttons);
                    dispatchEventInSwing(w, e);
                    lastMouseEvent = e;
                    break;
                case mousewheel:
                    id = MouseEvent.MOUSE_WHEEL;
                    buttons = 0;
                    modifiers = 0;
                    e = new MouseWheelEvent(w, id, when, modifiers, x, y, clickcount, false, MouseWheelEvent.WHEEL_UNIT_SCROLL, 3, event.wheelDelta);
                    dispatchEventInSwing(w, e);
                    break;
                case dblclick:
                    //                    e = new MouseEvent(w, MouseEvent.MOUSE_CLICKED, when, modifiers, x, y, event.x, event.y, 2, false, buttons);
                    //                    dispatchEventInSwing(w, e);
                    //                    break;
                default:
                    break;
            }
        }
    }

    private int computeClickCount(int x, int y, int buttons, boolean isPressed) {
        if (isPressed) {
            if (lastMouseEvent != null && lastMouseEvent.getID() == MouseEvent.MOUSE_CLICKED && lastMouseEvent.getButton() == buttons && lastMouseEvent.getX() == x && lastMouseEvent.getY() == y) {
                return lastMouseEvent.getClickCount() + 1;
            }
        }else{
            if (lastMouseEvent != null && lastMouseEvent.getID() == MouseEvent.MOUSE_PRESSED && lastMouseEvent.getButton() == buttons ) {
                return lastMouseEvent.getClickCount();
            }
        }
        return 1;
    }

    private void handlePasteEvent(final String content) {
        Logger.debug("WebEventDispatcher.handlePasteEvent", content);
        final Component c = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
        if (c != null && c instanceof JTextComponent) {
            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    JTextComponent txtc = (JTextComponent) c;
                    WebClipboard wc = (WebClipboard) Util.getWebToolkit().getSystemClipboard();
                    wc.setContent(content);
                    txtc.paste();
                }
            });
        }
    }

    public Point getLastMousePosition() {
        return lastMousePosition;
    }

    public void dragStart(WebDragSourceContextPeer peer, Transferable transferable, int actions, long[] formats,boolean useDropTarget) {
        dndHandler.dragStart(peer, transferable, actions,formats,useDropTarget);
    }

    public static void dispatchEventInSwing(final Window w, final AWTEvent e) {
        if (e instanceof MouseEvent) {
            w.setCursor(w.getCursor());//force cursor update
        }
        if ((Util.isWindowDecorationEvent(w, e) || WindowManager.getInstance().isLockedToWindowDecorationHandler()) && e instanceof MouseEvent) {
            Logger.debug("WebEventDispatcher.dispatchEventInSwing:windowManagerHandle", e);
            WindowManager.getInstance().handleWindowDecorationEvent(w, (MouseEvent) e);
        } else if (DndEventHandler.isDndInProgress() && (e instanceof MouseEvent || e instanceof KeyEvent)) {
            dndHandler.processMouseEvent(w, e);
        } else {
            Logger.debug("WebEventDispatcher.dispatchEventInSwing:postSystemQueue", e);
            Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(e);
        }
    }
}
