package org.webswing.toolkit;

import java.awt.EventQueue;
import java.awt.GraphicsEnvironment;

import org.webswing.toolkit.ge.WebGraphicsEnvironment;

import sun.awt.image.SurfaceManager;
import sun.java2d.SurfaceData;

@SuppressWarnings("restriction")
public class WebToolkit6 extends WebToolkit {

    @Override
    public void displayChanged() {
        EventQueue.invokeLater(new Runnable() {

            public void run() {
                ((WebGraphicsEnvironment) GraphicsEnvironment.getLocalGraphicsEnvironment()).displayChanged();
            }
        });
    }

    @Override
    public boolean webConpoenentPeerUpdateGraphicsData() {
        return false;
    }

    @Override
    public SurfaceData webComponentPeerReplaceSurfaceData(SurfaceManager mgr) {
        return mgr.getDestSurfaceData();
    }

}
