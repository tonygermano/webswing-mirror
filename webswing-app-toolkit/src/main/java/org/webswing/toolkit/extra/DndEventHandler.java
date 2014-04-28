package org.webswing.toolkit.extra;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Window;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.InvalidDnDOperationException;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import org.webswing.model.s2c.JsonCursorChange;
import org.webswing.toolkit.WebDragSourceContextPeer;
import org.webswing.toolkit.WebDropTargetContextPeer;

@SuppressWarnings("restriction")
public class DndEventHandler {

    private WebDropTargetContextPeer dropTarget;
    private long[] formats;
    private boolean entered;
    private WebDragSourceContextPeer dragSource;
    private int actions;
    private static boolean dropSupported;

    public void processMouseEvent(Window w, AWTEvent e) {
        if (e instanceof MouseEvent) {
            MouseEvent me = (MouseEvent) e;
            if (e.getID() == MouseEvent.MOUSE_RELEASED || me.getButton() != 1) {
                if (dropSupported) {
                    dropTarget.handleDropMessage(w, me.getX(), me.getY(), getDragAction(me), this.actions, formats, 1073741824);
                    dragEnd(e, true, getDragAction(me));
                    WindowManager.getInstance().activateWindow(w, me.getX(), me.getY());
                } else {
                    dragEnd(e, false, getDragAction(me));
                }
            } else {
                if (e.getID() == MouseEvent.MOUSE_DRAGGED && w != null) {
                    if (!entered) {
                        entered = true;
                        int enterdrag = dropTarget.handleEnterMessage(w, me.getX(), me.getY(), getDragAction(me), this.actions, this.formats, 1073741824);
                        if (enterdrag == 1) {
                            dragSource.dragEnter(getDragAction(me), me.getModifiers(), me.getX(), me.getY());
                            dropSupported = true;
                        } else {
                            dropSupported = false;
                        }
                    }
                    int motiondrag = dropTarget.handleMotionMessage(w, me.getX(), me.getY(), getDragAction(me), this.actions, this.formats, 1073741824);
                    if (motiondrag == 1) {
                        dragSource.dragMotion(getDragAction(me), me.getModifiers(), me.getX(), me.getY());
                        dropSupported = true;
                    } else {
                        dropSupported = false;
                    }
                }
            }

        } else if (e instanceof KeyEvent) {
            if (e.getID() == KeyEvent.KEY_PRESSED && ((KeyEvent) e).getKeyCode() == KeyEvent.VK_ESCAPE) {
                dragEnd(e, false, 0);
            }
        }

    }

    private int getDragAction(MouseEvent me) {
        int result = 2;
        if ((me.getModifiersEx() & MouseEvent.CTRL_DOWN_MASK) == MouseEvent.CTRL_DOWN_MASK) {
            result = 1;
        }
        return result;
    }

    public void dragStart(WebDragSourceContextPeer dragSource, Transferable transferable, int actions, long[] formats, boolean useDropTarget) {
        this.dragSource = dragSource;
        this.formats = formats;
        this.actions = actions;
        dropTarget = WebDropTargetContextPeer.getWebDropTargetContextPeer();
        this.entered = false;
    }

    private void dragEnd(AWTEvent e, boolean success, int dropAction) {
        if (isDndInProgress()) {
            dropTarget.handleExitMessage((Component) e.getSource(), 1073741824);
            int x = 0;
            int y = 0;
            if (e instanceof MouseEvent) {
                x = ((MouseEvent) e).getX();
                y = ((MouseEvent) e).getY();
            }
            dragSource.dragExit2(x, y);
            dragSource.dragFinished(success, dropAction, x, y);
        }
    }

    public void setDndInProgress(boolean value) {
        try {
            WebDragSourceContextPeer.setDragDropInProgress(value);
        } catch (InvalidDnDOperationException e) {
        }
    }

    public static String getCurrentDropTargetCursorName() {
        if (dropSupported) {
            return JsonCursorChange.MOVE_CURSOR;
        } else {
            return JsonCursorChange.NOT_ALLOWED_CURSOR;
        }

    }

    public static boolean isDndInProgress() {
        try {
            WebDragSourceContextPeer.checkDragDropInProgress();
            return false;
        } catch (InvalidDnDOperationException e) {
            return true;
        }
    }

}
