package org.webswing.toolkit.extra;

import java.awt.AWTEvent;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.AWTEventListener;
import java.awt.event.WindowEvent;
import java.util.LinkedList;

import javax.swing.SwingUtilities;


public class WindowManager implements AWTEventListener {

    private static WindowManager singleton= null;
    
    LinkedList<Window> zorder= new LinkedList<Window>(); 
    Window activeWindow= null;
    
    private WindowManager(){
        Toolkit.getDefaultToolkit().addAWTEventListener(this, AWTEvent.WINDOW_EVENT_MASK);
    }
    public static WindowManager getInstance(){
        if(singleton==null){
            singleton= new  WindowManager();
        }
        return singleton;
    }
    
    public Window getVisibleWindowOnPosition(int x, int y){
        for(Window w:zorder){
            if(SwingUtilities.isRectangleContainingRectangle(w.getBounds(),new Rectangle(x,y,0,0))){
                return w;
            }
        }
        return null;
    }
    
    public void bringToFront(Window w){
        zorder.remove(w);
        if(w.isAlwaysOnTop()){
            zorder.addFirst(w);
        }else{
            Window firstNotAlwaysOnTopWindow=null;
            for(Window z: zorder){
                if(!z.isAlwaysOnTop()){
                    firstNotAlwaysOnTopWindow=z;
                }
            }
            if(firstNotAlwaysOnTopWindow!=null){
                zorder.add(zorder.indexOf(firstNotAlwaysOnTopWindow),w);
            }else{
                zorder.addFirst(w);
            }
        }
    }
    
    public void bringToBack(Window w){
        w.setAlwaysOnTop(false);
        zorder.remove(w);
        zorder.add(w);
    }
    
    
    public Window getActiveWindow(){
        return activeWindow;
    }
    
    public boolean isWindowActive(Window w){
        if(activeWindow==w){
            return true;
        }else{
            return false;
        }
    }
    
    public void activateWindow(Window w){
        bringToFront(w);
        this.activeWindow=w;
    }
    
    public void eventDispatched(AWTEvent event) {
        if(event instanceof WindowEvent){
            WindowEvent we= (WindowEvent) event;
            switch(we.getID()){
                case WindowEvent.WINDOW_CLOSED:
                    if(zorder.contains(we.getWindow())){
                        zorder.remove(we.getWindow());
                    }
                    break;
                case WindowEvent.WINDOW_OPENED:
                    bringToFront(we.getWindow());
                    break;
            }
        }
    }
    
}
