package org.webswing.toolkit;

import java.awt.AWTEvent;
import java.awt.AWTException;
import java.awt.BufferCapabilities;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.BufferCapabilities.FlipContents;
import java.awt.event.PaintEvent;
import java.awt.image.ColorModel;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.awt.image.VolatileImage;
import java.awt.peer.ContainerPeer;

import sun.awt.CausedFocusEvent.Cause;
import sun.java2d.pipe.Region;


public class WebContainerPeer extends WebComponentPeer implements ContainerPeer {


    public WebContainerPeer(Container t){
        super(t);
    }
    
    
    public Insets getInsets() {
       return new Insets(0, 0, 0, 0);
    }

    
    public void beginValidate() {
        System.out.println("begin Validate");        
    }

    
    public void endValidate() {
        System.out.println("end Validate");        
        
    }

    
    public void beginLayout() {
        // TODO Auto-generated method stub
        
    }

    
    public void endLayout() {
        // TODO Auto-generated method stub
        
    }

    
    public boolean isPaintPending() {
        // TODO Auto-generated method stub
        return false;
    }

    
    public void restack() {
        throw new UnsupportedOperationException();        
    }

    
    public boolean isRestackSupported() {
        return false;
    }

    
    public Insets insets() {
        return getInsets();
    }

  
}
