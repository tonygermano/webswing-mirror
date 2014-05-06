package org.webswing.toolkit.extra;

import java.awt.AWTEvent;
import java.awt.Window;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
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
    private int sourceActions;
    private int lastMouseModifiers = -1;
    private int lastDropTargetAction = 0;
    private Window lastPotentialDropWindow;
    private boolean dropped;
    private boolean finished;
    private static String cursorName = JsonCursorChange.MOVE_CURSOR;

    public void processMouseEvent(Window w, AWTEvent e) {
        if (e instanceof MouseEvent) {
            MouseEvent me = (MouseEvent) e;
            int currentDropAction = WebDragSourceContextPeer.convertModifiersToDropAction(me.getModifiersEx(), sourceActions);
            if (e.getID() == MouseEvent.MOUSE_DRAGGED && w != null) {
                if (lastMouseModifiers == me.getModifiersEx() || lastMouseModifiers == -1) {
                    dragSource.dragMouseMoved(currentDropAction & lastDropTargetAction, me.getModifiersEx(), me.getXOnScreen(), me.getYOnScreen());
                } else {
                    dragSource.dragOperationChanged(currentDropAction & lastDropTargetAction, me.getModifiersEx(), me.getXOnScreen(), me.getYOnScreen());
                }
                lastMouseModifiers = me.getModifiersEx();
                if (currentDropAction != DnDConstants.ACTION_NONE) {
                    if (!entered) {
                        dragSource.dragEnter(currentDropAction & lastDropTargetAction, me.getModifiersEx(), me.getXOnScreen(), me.getYOnScreen());
                        entered = true;
                    } else {
                        dragSource.dragMotion(currentDropAction & lastDropTargetAction, me.getModifiersEx(), me.getXOnScreen(), me.getYOnScreen());
                    }
                } else {
                    if (entered) {
                        dragSource.dragExit2(me.getXOnScreen(), me.getYOnScreen());
                    }
                }
                if (lastPotentialDropWindow == null || lastPotentialDropWindow != w) {
                    if (lastPotentialDropWindow != w && lastPotentialDropWindow != null) {
                        dropTarget.handleExitMessage(lastPotentialDropWindow, 123123123);
                    }
                    lastDropTargetAction = dropTarget.handleEnterMessage(w, me.getX(), me.getY(), currentDropAction, sourceActions, formats, 123123123);
                    updateCursor();
                    lastPotentialDropWindow = w;
                }
                lastDropTargetAction = dropTarget.handleMotionMessage(w, me.getX(), me.getY(), currentDropAction, sourceActions, formats, 123123123);
                updateCursor();
            } else if (e.getID() == MouseEvent.MOUSE_RELEASED) {
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
        this.entered = false;
        this.dropped = false;
        this.finished = false;
    }

    private void dragEnd(Window w, AWTEvent e, boolean success, int dropAction) {
        if (!finished) {
            MouseEvent me = ((MouseEvent) e);
            if (w != null) {
                if (success && lastPotentialDropWindow != null && entered && !dropped) {
                    dropTarget.handleDropMessage(w, me.getX(), me.getY(), lastDropTargetAction, this.sourceActions, formats, 123123123);
                    dropped = true;
                } else {
                    if (lastPotentialDropWindow != null) {
                        dropTarget.handleExitMessage(w, 123123123);
                    }
                }
            }
            dragSource.dragFinished(success, dropAction, me.getX(), me.getY());
            if (success) {
                WindowManager.getInstance().activateWindow(w, me.getX(), me.getY());
            }
            finished = true;
        }
        entered = false;
        sourceActions = 0;
        lastMouseModifiers = -1;
        lastDropTargetAction = 0;
        lastPotentialDropWindow = null;
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
