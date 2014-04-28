package org.webswing.toolkit;

import java.awt.Component;
import java.io.IOException;

import sun.awt.PeerEvent;
import sun.awt.SunToolkit;
import sun.awt.dnd.SunDropTargetContextPeer;
import sun.awt.dnd.SunDropTargetEvent;

@SuppressWarnings("restriction")
public class WebDropTargetContextPeer extends SunDropTargetContextPeer {

    public static WebDropTargetContextPeer getWebDropTargetContextPeer() {
        return new WebDropTargetContextPeer();
    }

    @Override
    protected Object getNativeData(long paramLong) throws IOException {
        return null;
    }

    @Override
    protected void doDropDone(boolean paramBoolean1, int paramInt, boolean paramBoolean2) {
    }

    public void handleDropMessage(Component window, int x, int y, int dropAction, int actions, long[] formats, long nativeCtxt) {
        postDropTargetEvent(window, x, y, dropAction, actions, formats, nativeCtxt, 502, false);
    }

    public int handleEnterMessage(Component window, int x, int y, int dropAction, int actions, long[] formats, long nativeCtxt) {
        return postDropTargetEvent(window, x, y, dropAction, actions, formats, nativeCtxt, 504, true);
    }

    public void handleExitMessage(Component window, long nativeCtxt) {
        postDropTargetEvent(window, 0, 0, 0, 0, null, nativeCtxt, 505, true);
    }

    public int handleMotionMessage(Component window, int x, int y, int dropAction, int actions, long[] formats, long nativeCtxt) {
        return postDropTargetEvent(window, x, y, dropAction, actions, formats, nativeCtxt, 506, true);
    }

    @Override
    protected void eventPosted(final SunDropTargetEvent paramSunDropTargetEvent) {
        if (paramSunDropTargetEvent.getID() != 502) {
            Runnable local1 = new Runnable() {

                public void run() {
                    paramSunDropTargetEvent.getDispatcher().unregisterAllEvents();
                }

            };
            PeerEvent localPeerEvent = new PeerEvent(paramSunDropTargetEvent.getSource(), local1, 0L);
            SunToolkit.executeOnEventHandlerThread(localPeerEvent);
        }
    }
}
