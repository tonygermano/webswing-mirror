package org.webswing.toolkit;

import java.awt.AWTEvent;
import java.awt.AWTException;
import java.awt.BufferCapabilities;
import java.awt.BufferCapabilities.FlipContents;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.Insets;
import java.awt.MenuBar;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.PaintEvent;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.awt.image.VolatileImage;
import java.awt.peer.ContainerPeer;
import java.awt.peer.FramePeer;

import sun.awt.CausedFocusEvent.Cause;
import sun.java2d.pipe.Region;

public class WebFramePeer extends WebWindowPeer implements FramePeer {

    private int state;

    public WebFramePeer(Frame t) {
        super(t);
    }

    public void setTitle(String paramString) {
        // TODO Auto-generated method stub

    }

    public void setMenuBar(MenuBar paramMenuBar) {
        // TODO Auto-generated method stub

    }

    public void setResizable(boolean paramBoolean) {
        // TODO Auto-generated method stub

    }

    public void setState(int paramInt) {
        state = paramInt;
    }

    public int getState() {
        return state;
    }

    public void setMaximizedBounds(Rectangle paramRectangle) {
        // TODO Auto-generated method stub

    }

    public void setBoundsPrivate(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
        // TODO Auto-generated method stub

    }

    public Rectangle getBoundsPrivate() {
        // TODO Auto-generated method stub
        return null;
    }

}
