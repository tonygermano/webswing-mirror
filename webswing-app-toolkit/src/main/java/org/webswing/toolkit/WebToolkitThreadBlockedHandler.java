package org.webswing.toolkit;

import sun.awt.datatransfer.ToolkitThreadBlockedHandler;

@SuppressWarnings("restriction")
public class WebToolkitThreadBlockedHandler extends sun.awt.Mutex implements ToolkitThreadBlockedHandler {

    @Override
    public void enter() {
        if (!isOwned()) {
            throw new IllegalMonitorStateException();
        }
        try {
            unlock();
        } catch (Exception e) {
        }
        lock();
    }

    @Override
    public void exit() {
        if (!isOwned()) {
            throw new IllegalMonitorStateException();
        }
        try {
            unlock();
        } catch (Exception e) {
        }
    }

    @Override
    public synchronized void unlock() {
        try {
            super.unlock();
        } catch (Exception e) {
        }
    }
}
