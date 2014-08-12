package org.webswing.toolkit;

import java.awt.EventQueue;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.peer.KeyboardFocusManagerPeer;

import org.webswing.toolkit.ge.WebGraphicsEnvironment;

import sun.awt.image.SurfaceManager;
import sun.java2d.SurfaceData;

@SuppressWarnings("restriction")
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

    @Override
    public boolean webConpoenentPeerUpdateGraphicsData() {
        ((WebGraphicsEnvironment) GraphicsEnvironment.getLocalGraphicsEnvironment()).displayChanged();
        return true;
    }

    @Override
    public SurfaceData webComponentPeerReplaceSurfaceData(SurfaceManager mgr) {
        return mgr.getPrimarySurfaceData();
    }

}
