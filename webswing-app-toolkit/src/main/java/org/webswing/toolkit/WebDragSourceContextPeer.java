package org.webswing.toolkit;

import java.awt.Cursor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.InvalidDnDOperationException;
import java.util.Map;

@SuppressWarnings("restriction")
public class WebDragSourceContextPeer extends sun.awt.dnd.SunDragSourceContextPeer {

    private static final WebDragSourceContextPeer theInstance = new WebDragSourceContextPeer(null);

    public WebDragSourceContextPeer(DragGestureEvent paramDragGestureEvent) {
        super(paramDragGestureEvent);
    }

    static WebDragSourceContextPeer createDragSourceContextPeer(DragGestureEvent paramDragGestureEvent) throws InvalidDnDOperationException {
        theInstance.setTrigger(paramDragGestureEvent);
        return theInstance;
    }

    @SuppressWarnings("rawtypes")
    @Override
    protected void startDrag(Transferable paramTransferable, long[] paramArrayOfLong, Map paramMap) {

    }

    @Override
    protected void setNativeCursor(long paramLong, Cursor paramCursor, int paramInt) {

    }

}
