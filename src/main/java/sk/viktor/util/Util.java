package sk.viktor.util;

import java.awt.Component;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;

import sk.viktor.ignored.common.WebWindow;

public class Util {

    public static boolean isPaintImmediately() {
        StackTraceElement[] trace = Thread.currentThread().getStackTrace();
        for (StackTraceElement e : trace) {
            if (e.getClassName().equals("javax.swing.JComponent") && e.getMethodName().equals("paintImmediately")) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean isForceDoubleBufferedPainting() {
        StackTraceElement[] trace = Thread.currentThread().getStackTrace();
        for (StackTraceElement e : trace) {
            if (e.getClassName().equals("javax.swing.JComponent") && e.getMethodName().equals("paintForceDoubleBuffered")) {
                return true;
            }
        }
        return false;
    }

    public static boolean isPaintDoubleBufferedPainting() {
        StackTraceElement[] trace = Thread.currentThread().getStackTrace();
        for (StackTraceElement e : trace) {
            if (e.getClassName().equals("javax.swing.RepaintManager$PaintManager") && e.getMethodName().equals("paintDoubleBuffered")) {
                return true;
            }
        }
        return false;
    }

    public static String getObjectIdentity(Object c) {
        return "c" + System.identityHashCode(c);
    }

    public static int getMouseButtonsAWTFlag(int button) {
        switch(button){
            case 1:return MouseEvent.BUTTON1;
            case 2:return MouseEvent.BUTTON2;
            case 3:return MouseEvent.BUTTON3;
            case 0:return MouseEvent.NOBUTTON;
        }
        return 0;
    }
    public static int getMouseModifiersAWTFlag(int button) {
        switch(button){
            case 1:return MouseEvent.BUTTON1_DOWN_MASK;
            case 2:return MouseEvent.BUTTON2_DOWN_MASK;
            case 3:return MouseEvent.BUTTON3_DOWN_MASK;
        }
        return 0;
    }

    public static String resolveClientId(JComponent c) {
        Component parent=c;
        while((parent=parent.getParent()) instanceof WebWindow){
            return ((WebWindow)parent).getClientId();
        }
        return null;
    }
}
