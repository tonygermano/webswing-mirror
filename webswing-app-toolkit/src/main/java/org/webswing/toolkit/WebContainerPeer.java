package org.webswing.toolkit;

import java.awt.Container;
import java.awt.Insets;
import java.awt.peer.ContainerPeer;

import javax.swing.JWindow;

import org.webswing.util.Util;

public class WebContainerPeer extends WebComponentPeer implements ContainerPeer {


    public WebContainerPeer(Container t){
        super(t);
    }
    
    
    public Insets getInsets() {
       if(target!=null && target instanceof JWindow){
           return new Insets(0,0,0,0);
       }else{
           return Util.getWebToolkit().getImageService().getWindowDecorationTheme().getInsets();
       }
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
