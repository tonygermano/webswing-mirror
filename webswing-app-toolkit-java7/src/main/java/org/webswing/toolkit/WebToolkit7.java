package org.webswing.toolkit;

import java.awt.EventQueue;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.peer.KeyboardFocusManagerPeer;

import org.webswing.toolkit.ge.WebGraphicsEnvironment;

public class WebToolkit7 extends WebToolkit {

    private KeyboardFocusManagerPeer kfmp;

    @Override
    public void displayChanged() {
        EventQueue.invokeLater(new Runnable() {

            public void run() {
                ((WebGraphicsEnvironment) GraphicsEnvironment.getLocalGraphicsEnvironment()).displayChanged();
            }
        });
    }

    @Override
    public KeyboardFocusManagerPeer getKeyboardFocusManagerPeer() throws HeadlessException {
        if (kfmp == null) {
            kfmp = new WebKeyboardFocusManagerPeer();
        }
        return kfmp;
    }

    @Override
    protected boolean syncNativeQueue(long paramLong) {
        return false;
    }

}
