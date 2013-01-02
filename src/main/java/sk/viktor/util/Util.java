package sk.viktor.util;

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;
import javax.swing.JComponent;
import javax.swing.UIManager;

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
        switch (button) {
            case 1:
                return MouseEvent.BUTTON1;
            case 2:
                return MouseEvent.BUTTON2;
            case 3:
                return MouseEvent.BUTTON3;
            case 0:
                return MouseEvent.NOBUTTON;
        }
        return 0;
    }

    public static int getMouseModifiersAWTFlag(int button) {
        switch (button) {
            case 1:
                return MouseEvent.BUTTON1_DOWN_MASK;
            case 2:
                return MouseEvent.BUTTON2_DOWN_MASK;
            case 3:
                return MouseEvent.BUTTON3_DOWN_MASK | MouseEvent.META_DOWN_MASK;
        }
        return 0;
    }

    public static String resolveClientId(JComponent c) {
        Component parent = c;
        while ((parent = parent.getParent()) instanceof WebWindow) {
            return ((WebWindow) parent).getClientId();
        }
        return null;
    }

    public static String resolveClientId(Class<?> clazz) {
        if (clazz.getClassLoader().getClass().getCanonicalName().equals("sk.viktor.SwingClassloader")) {
            try {
                Method m = clazz.getClassLoader().getClass().getMethod("getClientId");
                String result = (String) m.invoke(clazz.getClassLoader());
                return result;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static byte[] getPngImage(BufferedImage imageContent) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageOutputStream ios = ImageIO.createImageOutputStream(baos);
            ImageIO.write(imageContent, "png", ios);
            byte[] result = baos.toByteArray();
            baos.close();
            return result;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void savePngImage(BufferedImage imageContent, String name) {
        try {
            OutputStream os = new FileOutputStream(new File(name));
            ImageOutputStream ios = ImageIO.createImageOutputStream(os);
            ImageIO.write(imageContent, "png", ios);
            ios.close();
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String resolveComboboxUIClassId(String newUIid) {
        String className = (String) UIManager.get(newUIid);
        for (StackTraceElement e : Thread.currentThread().getStackTrace()) {
            if (e.getClassName().equals(UIManager.class.getCanonicalName()) && e.getMethodName().equals("getUI") ) {
                return newUIid;
            }
            if(e.getClassName().equals(className) && e.getMethodName().equals("<init>") ){
                break;
            }
        }
        return "ComboBoxUI";

    }
}
