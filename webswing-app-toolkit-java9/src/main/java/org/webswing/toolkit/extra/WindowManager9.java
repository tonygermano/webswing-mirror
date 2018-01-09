package org.webswing.toolkit.extra;

import java.awt.*;
import java.awt.event.*;
import java.lang.reflect.*;
import java.util.*;

import javax.swing.*;

import org.webswing.toolkit.util.*;
import sun.awt.*;

public class WindowManager9 extends WindowManager {
    private static WindowManager9 singleton = null;
    public static WindowManager9 getInstance() {
        if (singleton == null) {
            singleton = new WindowManager9();
        }
        return singleton;
    }
    
    @Override
    public void activateWindow(Window w, int x, int y) {
        Component newFocusOwner = w.getFocusTraversalPolicy().getInitialComponent(w);
        activateWindow(w, newFocusOwner, x, y, false, true, FocusEvent.Cause.UNEXPECTED);
    }
    
    public boolean activateWindow(Window w, Component newFocusOwner, int x, int y, boolean tmp, boolean focusedWindowChangeAllowed, FocusEvent.Cause cause) {
        boolean success = false;
        boolean newWindow = false;
        if (!zorder.contains(w)) {
            zorder.addWindow(w);
            newWindow = true;
        }
        
        // dont allow activation outside modal dialog ancestors
        if (!(isModal(w) && newWindow) && !zorder.isInSameModalBranch(activeWindow, w) && !(w instanceof sun.awt.ModalExclude)) {
            return success;
        }
        if (focusedWindowChangeAllowed || activeWindow == w) {
            
            if (newFocusOwner != null && newFocusOwner.isFocusable() && w.isFocusableWindow()) {
                int result = shouldNativelyFocusHeavyweight(w, newFocusOwner, tmp, true, new Date().getTime(), cause);
                switch (result) {
                    case 1:
                        success = true;
                        break;
                    case 2:
                        deliverFocus(w, newFocusOwner, tmp, true, new Date().getTime(), cause);
                        success = true;
                        break;
                    default:
                        break;
                }
            }
            
            if (SwingUtilities.isRectangleContainingRectangle(new Rectangle(0, 0, w.getWidth(), w.getHeight()), new Rectangle(x, y, 0, 0))) {
                bringToFront(w);
            } else {
                bringToFront(null);
            }
        }
        return success;
        
    }
    
    public static int shouldNativelyFocusHeavyweight(Window heavyweight, Component descendant, boolean temporary, boolean focusedWindowChangeAllowed, long time, FocusEvent.Cause cause) {
        try {
            Method m2 = KeyboardFocusManager.class.getDeclaredMethod("shouldNativelyFocusHeavyweight", Component.class, Component.class, Boolean.TYPE, Boolean.TYPE, Long.TYPE, FocusEvent.Cause.class);
            m2.setAccessible(true);
            Integer result2 = (Integer) m2.invoke(null, heavyweight, descendant, temporary, focusedWindowChangeAllowed, time, cause);
            return result2;
        } catch (Exception e) {
            Logger.debug("Failed to invoke processSynchronousLightweightTransfer on KeyboardFocusManager. Check your java version.", e);
            return 0;
        }
    }
    
    @SuppressWarnings("deprecation")
    public static boolean deliverFocus(Component heavyweight, Component descendant, boolean temporary, boolean focusedWindowChangeAllowed, long time, FocusEvent.Cause cause) {
        if (heavyweight == null) {
            heavyweight = descendant;
        }
        
        Component c = Util.getWebToolkit().getWindowManager().getActiveWindow().getFocusOwner();
        FocusEvent focusEvent;
        if ((c != null) && !c.isDisplayable()) {
            c = null;
        }
        if (c != null) {
            focusEvent = new FocusEvent(c, FocusEvent.FOCUS_LOST, false, heavyweight, cause);
            
            SunToolkit.postEvent(SunToolkit.targetToAppContext(c), focusEvent);
        }
        
        focusEvent = new FocusEvent(heavyweight, FocusEvent.FOCUS_GAINED, false, c, cause);
        
        SunToolkit.postEvent(SunToolkit.targetToAppContext(heavyweight), focusEvent);
        return true;
    }
}
