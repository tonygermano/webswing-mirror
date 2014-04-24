package org.webswing.toolkit;

import java.awt.Component;
import java.io.IOException;

import sun.awt.dnd.SunDropTargetContextPeer;

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
}
