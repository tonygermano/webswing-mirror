package org.webswing.util;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Window;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;
import javax.swing.UIManager;

import org.webswing.ignored.common.WebWindow;
import org.webswing.ignored.model.c2s.JsonEventKeyboard;
import org.webswing.ignored.model.c2s.JsonEventKeyboard.Type;
import org.webswing.ignored.model.c2s.JsonEventMouse;
import org.webswing.ignored.model.s2c.JsonWindowInfo;

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

    public static int getMouseModifiersAWTFlag(JsonEventMouse evt) {
        int result = 0;
        switch (evt.button) {
            case 1:
                result = MouseEvent.BUTTON1_DOWN_MASK;
                break;
            case 2:
                result = MouseEvent.BUTTON2_DOWN_MASK;
                break;
            case 3:
                result = MouseEvent.BUTTON3_DOWN_MASK | MouseEvent.META_DOWN_MASK;
                break;
        }
        if (evt.ctrl) {
            result = result | MouseEvent.CTRL_DOWN_MASK;
        }
        if (evt.alt) {
            result = result | MouseEvent.ALT_DOWN_MASK;
        }
        if (evt.shift) {
            result = result | MouseEvent.SHIFT_DOWN_MASK;
        }
        if (evt.meta) {
            result = result | MouseEvent.META_DOWN_MASK;
        }
        return result;
    }

    public static void savePngImage(Image imageContent, String name) {
        try {
            BufferedImage bi= new BufferedImage(imageContent.getWidth(null), imageContent.getHeight(null), BufferedImage.TYPE_4BYTE_ABGR_PRE);
            Graphics g = bi.getGraphics();
            g.drawImage(imageContent, 0, 0, null);
            OutputStream os = new FileOutputStream(new File(name));
            ImageOutputStream ios = ImageIO.createImageOutputStream(os);
            ImageIO.write(bi, "png", ios);
            ios.close();
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String resolveComboboxUIClassId(String newUIid) {
        String className = (String) UIManager.get(newUIid);
        for (StackTraceElement e : Thread.currentThread().getStackTrace()) {
            if (e.getClassName().equals(UIManager.class.getCanonicalName()) && e.getMethodName().equals("getUI")) {
                return newUIid;
            }
            if (e.getClassName().equals(className) && e.getMethodName().equals("<init>")) {
                break;
            }
        }
        return "ComboBoxUI";

    }

    public static int getKeyModifiersAWTFlag(JsonEventKeyboard event) {
        int modifiers = 0;
        if (event.alt) {
            modifiers = modifiers & KeyEvent.ALT_MASK;
        }
        if (event.ctrl) {
            modifiers = modifiers & KeyEvent.CTRL_MASK;
        }
        if (event.shift) {
            modifiers = modifiers & KeyEvent.SHIFT_MASK;
        }
        if (event.altgr) {
            modifiers = modifiers & KeyEvent.ALT_GRAPH_MASK;
        }
        if (event.meta) {
            modifiers = modifiers & KeyEvent.META_MASK;
        }
        return modifiers;
    }

    public static int getKeyType(Type type) {
        switch (type) {
            case keydown:
                return KeyEvent.KEY_PRESSED;
            case keypress:
                return KeyEvent.KEY_TYPED;
            case keyup:
                return KeyEvent.KEY_RELEASED;
        }
        return 0;
    }

    public static Map<String, JsonWindowInfo> createJsonWindowInfoMap(Set<String> keys, Map<String, Window> windows) {
        Map<String, JsonWindowInfo> result = new HashMap<String, JsonWindowInfo>();
        for (String windowKey : keys) {
            WebWindow ww = (WebWindow) windows.get(windowKey);
            result.put(windowKey, ww.getWindowInfo());
        }
        return result;
    }

    public static boolean needPainting(Map<String, Window> windows) {
        Map<String, Window> copy = new HashMap<String, Window>(windows);
        for (String windowKey : copy.keySet()) {
            WebWindow ww = (WebWindow) copy.get(windowKey);
            if (ww.isWebDirty()) {
                return true;
            }
        }
        return false;
    }

    public static BufferedImage deepCopy(BufferedImage bi) {
        ColorModel cm = bi.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = bi.copyData(null);
        return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
    }

    
    public static Map<String, BufferedImage> getImagesToPaint(Map<String, Window> windows){
        Map<String, BufferedImage> result = new HashMap<String, BufferedImage>();
        for (String windowKey : windows.keySet()) {
            WebWindow ww = (WebWindow) windows.get(windowKey);
            if (ww.isWebDirty()) {
                result.put(windowKey, ww.getDiffWebData());
            }
        }
        return result;
    }
}
