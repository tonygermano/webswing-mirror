package org.webswing.util;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;

import org.webswing.ignored.model.c2s.JsonEventKeyboard;
import org.webswing.ignored.model.c2s.JsonEventKeyboard.Type;
import org.webswing.ignored.model.c2s.JsonEventMouse;

public class Util {


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

    public static BufferedImage deepCopy(BufferedImage bi) {
        ColorModel cm = bi.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = bi.copyData(null);
        return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
    }

   
}
