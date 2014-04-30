package org.webswing.toolkit.extra;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Window;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.InvalidDnDOperationException;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import org.webswing.model.s2c.JsonCursorChange;
import org.webswing.toolkit.WebDragSourceContextPeer;
import org.webswing.toolkit.WebDropTargetContextPeer;

import sun.awt.dnd.SunDragSourceContextPeer;

@SuppressWarnings("restriction")
public class DndEventHandler {

    private WebDropTargetContextPeer dropTarget;
    private long[] formats;
    private boolean entered;
    private WebDragSourceContextPeer dragSource;
    private int sourceActions;
    private int lastMouseModifiers = -1;
    private int lastDropTargetAction = 3;

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
                    } else {
                        dragSource.dragMotion(currentDropAction & lastDropTargetAction, me.getModifiersEx(), me.getXOnScreen(), me.getYOnScreen());
                    }
                } else {
                    if (entered) {
                        dragSource.dragExit2(me.getXOnScreen(), me.getYOnScreen());
                    }
                }
                
                //dropTargetLogic
            } else if (e.getID() == MouseEvent.MOUSE_RELEASED || me.getButton() != 1) {
                dropTarget.handleDropMessage(w, me.getX(), me.getY(), currentDropAction, this.sourceActions, formats, 1073741824);
                dragEnd(e, currentDropAction != 0, currentDropAction);
                WindowManager.getInstance().activateWindow(w, me.getX(), me.getY());
            }
        } else if (e instanceof KeyEvent) {
            if (e.getID() == KeyEvent.KEY_PRESSED && ((KeyEvent) e).getKeyCode() == KeyEvent.VK_ESCAPE) {
                dragEnd(e, false, 0);
            }
        }

    }

    public void dragStart(WebDragSourceContextPeer dragSource, Transferable transferable, int actions, long[] formats) {
        this.dragSource = dragSource;
        this.formats = formats;
        this.sourceActions = actions;
        dropTarget = WebDropTargetContextPeer.getWebDropTargetContextPeer();
        this.entered = false;
    }

    private void dragEnd(AWTEvent e, boolean success, int dropAction) {
        if (isDndInProgress()) {
            dropTarget.handleExitMessage((Component) e.getSource(), 1073741824);
            int x = 0;
            int y = 0;
            if (e instanceof MouseEvent) {
                x = ((MouseEvent) e).getXOnScreen();
                y = ((MouseEvent) e).getYOnScreen();
            }
            dragSource.dragExit2(x, y);
            dragSource.dragFinished(success, dropAction, x, y);
        }
    }

    public static String getCurrentDropTargetCursorName() {
        return JsonCursorChange.MOVE_CURSOR;
        //                else {
        //                    return JsonCursorChange.NOT_ALLOWED_CURSOR;
        //                }

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
