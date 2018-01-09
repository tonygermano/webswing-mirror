package org.webswing.dispatch;

import java.awt.*;
import java.awt.event.*;

import org.webswing.model.c2s.*;
import org.webswing.toolkit.extra.WindowManager9;
import org.webswing.toolkit.util.*;

import javax.swing.SwingUtilities;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

public class WebEventDispatcher9 extends WebEventDispatcher {
    
    protected void dispatchMouseEvent(MouseEventMsgIn event)
    {
        Component c = null;
        if (Util.getWebToolkit().getWindowManager().isLockedToWindowDecorationHandler())
        {
            c = Util.getWebToolkit().getWindowManager().getLockedToWindow();
        }
        else
        {
            c = Util.getWebToolkit().getWindowManager().getVisibleComponentOnPosition(event.getX(), event.getY());
            if (lastMouseEvent != null && (lastMouseEvent.getID() == MouseEvent.MOUSE_DRAGGED || lastMouseEvent.getID() == MouseEvent.MOUSE_PRESSED) && ((event.getType() == MouseEventMsgIn.MouseEventType.mousemove && event.getButtons() != 0) || (event.getType() == MouseEventMsgIn.MouseEventType.mouseup)))
            {
                c = (Component) lastMouseEvent.getSource();
            }
        }
        if (c == null)
        {
            if (Util.getWebToolkit().getPaintDispatcher() != null)
            {
                Util.getWebToolkit().getPaintDispatcher().notifyCursorUpdate(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            }
            return;
        }
        if (c != null && c.isShowing())
        {
            MouseEvent e = null;
            int x = (int) (event.getX() - c.getLocationOnScreen().getX());
            int y = (int) (event.getY() - c.getLocationOnScreen().getY());
            lastMousePosition.x = event.getX();
            lastMousePosition.y = event.getY();
            long when = System.currentTimeMillis();
            int modifiers = Util.getMouseModifiersAWTFlag(event);
            int id = 0;
            int clickcount = 0;
            int buttons = Util.getMouseButtonsAWTFlag(event.getButton());
            if (buttons != 0 && event.getType() == MouseEventMsgIn.MouseEventType.mousedown)
            {
                Window w = (Window) (c instanceof Window ? c : SwingUtilities.windowForComponent(c));
                ((WindowManager9) Util.getWebToolkit().getWindowManager()).activateWindow(w, null, x, y, false, true, FocusEvent.Cause.MOUSE_EVENT);
            }
            switch (event.getType())
            {
                case mousemove:
                    id = event.getButtons() != 0 ? MouseEvent.MOUSE_DRAGGED : MouseEvent.MOUSE_MOVED;
                    buttons = lastMousePressEvent != null ? lastMouseEvent.getButton() : 1;
                    e = new MouseEvent(c, id, when, modifiers, x, y, event.getX(), event.getY(), clickcount, false, buttons);
                    lastMouseEvent = e;
                    dispatchEventInSwing(c, e);
                    break;
                case mouseup:
                    id = MouseEvent.MOUSE_RELEASED;
                    boolean popupTrigger = (buttons == 3) ? true : false;
                    clickcount = computeClickCount(x, y, buttons, false, event.getTimeMilis());
                    modifiers = modifiers & (((1 << 6) - 1) | (~((1 << 14) - 1)) | MouseEvent.CTRL_DOWN_MASK | MouseEvent.ALT_DOWN_MASK | MouseEvent.SHIFT_DOWN_MASK | MouseEvent.META_DOWN_MASK);
                    e = new MouseEvent(c, id, when, modifiers, x, y, event.getX(), event.getY(), clickcount, popupTrigger, buttons);
                    dispatchEventInSwing(c, e);
                    if (lastMousePressEvent != null && Math.abs(lastMousePressEvent.x - x) < CLICK_TOLERANCE && Math.abs(lastMousePressEvent.y - y) < CLICK_TOLERANCE)
                    {
                        e = new MouseEvent(c, MouseEvent.MOUSE_CLICKED, when, modifiers, x, y, event.getX(), event.getY(), clickcount, popupTrigger, buttons);
                        dispatchEventInSwing(c, e);
                        lastMouseEvent = e;
                        lastMousePressEvent = MouseEventInfo.get(e, event.getTimeMilis());
                    }
                    else
                    {
                        lastMouseEvent = e;
                        lastMousePressEvent = MouseEventInfo.get(e, event.getTimeMilis());
                    }
                    break;
                case mousedown:
                    id = MouseEvent.MOUSE_PRESSED;
                    clickcount = computeClickCount(x, y, buttons, true, event.getTimeMilis());
                    e = new MouseEvent(c, id, when, modifiers, x, y, event.getX(), event.getY(), clickcount, false, buttons);
                    dispatchEventInSwing(c, e);
                    lastMousePressEvent = MouseEventInfo.get(e, event.getTimeMilis());
                    lastMouseEvent = e;
                    break;
                case mousewheel:
                    id = MouseEvent.MOUSE_WHEEL;
                    buttons = 0;
                    modifiers = 0;
                    e = new MouseWheelEvent(c, id, when, modifiers, x, y, clickcount, false, MouseWheelEvent.WHEEL_UNIT_SCROLL, 3, event.getWheelDelta());
                    dispatchEventInSwing(c, e);
                    break;
                case dblclick:
                    // e = new MouseEvent(w, MouseEvent.MOUSE_CLICKED, when,
                    // modifiers, x, y, event.x, event.y, 2, false, buttons);
                    // dispatchEventInSwing(w, e);
                    // break;
                default:
                    break;
            }
    
        }
    }
}
