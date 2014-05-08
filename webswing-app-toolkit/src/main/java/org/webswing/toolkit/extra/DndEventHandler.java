package org.webswing.toolkit.extra;

import java.awt.AWTEvent;
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
    private WebDragSourceContextPeer dragSource;
    private int sourceActions;
    private int lastDropTargetAction = 0;
    private boolean dropped;
    private boolean finished;
    private static String cursorName = JsonCursorChange.MOVE_CURSOR;

    public void processMouseEvent(Window w, AWTEvent e) {
        if (e instanceof MouseEvent) {
            MouseEvent me = (MouseEvent) e;
            if (e.getID() == MouseEvent.MOUSE_RELEASED) {
                int modifiers = (me.getModifiersEx() & (MouseEvent.CTRL_DOWN_MASK | MouseEvent.SHIFT_DOWN_MASK)) | MouseEvent.BUTTON1_DOWN_MASK;
                int currentDropAction = WebDragSourceContextPeer.convertModifiersToDropAction(modifiers, sourceActions);
                //dragSource.dragMouseMoved(currentDropAction, modifiers, me.getXOnScreen(), me.getYOnScreen());
                //dragSource.dragEnter(currentDropAction, modifiers, me.getXOnScreen(), me.getYOnScreen());
                //dragSource.dragMotion(currentDropAction, modifiers, me.getXOnScreen(), me.getYOnScreen());
                lastDropTargetAction = dropTarget.handleEnterMessage(w, me.getX(), me.getY(), currentDropAction, sourceActions, formats, 123123123);
                //lastDropTargetAction = dropTarget.handleMotionMessage(w, me.getX(), me.getY(), currentDropAction, sourceActions, formats, 123123123);
                dragEnd(w, e, lastDropTargetAction != 0, lastDropTargetAction);
            }
        } else if (e instanceof KeyEvent) {
            if (e.getID() == KeyEvent.KEY_PRESSED && ((KeyEvent) e).getKeyCode() == KeyEvent.VK_ESCAPE) {
                dragEnd(w, e, false, 0);
            }
        }
    }

    private void updateCursor() {
        if (lastDropTargetAction == 0) {
            cursorName = JsonCursorChange.NOT_ALLOWED_CURSOR;
        } else {
            cursorName = JsonCursorChange.MOVE_CURSOR;
        }
    }

    public void dragStart(WebDragSourceContextPeer dragSource, Transferable transferable, int actions, long[] formats) {
        this.dragSource = dragSource;
        this.formats = formats;
        this.sourceActions = actions;
        dropTarget = WebDropTargetContextPeer.getWebDropTargetContextPeer();
        this.dropped = false;
        this.finished = false;
    }

    private void dragEnd(Window w, AWTEvent e, boolean supported, int dropAction) {
        if (!finished) {
            MouseEvent me = ((MouseEvent) e);
            if (w != null) {
                if (supported && !dropped) {
                    dropTarget.setDragSource(dragSource);
                    dropTarget.handleDropMessage(w, me.getX(), me.getY(), lastDropTargetAction, this.sourceActions, formats, 123123123);
                    dropped = true;
                } else {
                    dropTarget.handleExitMessage(w, 123123123);
                    dragSource.dragFinished(false, dropAction, me.getX(), me.getY());
                }
            }
            finished = true;
        }
        sourceActions = 0;
        lastDropTargetAction = 0;
        cursorName = JsonCursorChange.MOVE_CURSOR;
    }

    public static String getCurrentDropTargetCursorName() {
        return cursorName;
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
