package org.webswing.util;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Toolkit;
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
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;

import org.webswing.common.ImageServiceIfc;
import org.webswing.model.c2s.JsonEventKeyboard;
import org.webswing.model.c2s.JsonEventKeyboard.Type;
import org.webswing.model.c2s.JsonEventMouse;
import org.webswing.model.s2c.JsonAppFrame;
import org.webswing.model.s2c.JsonWindow;
import org.webswing.model.s2c.JsonWindowPartialContent;
import org.webswing.toolkit.WebToolkit;
import org.webswing.toolkit.WebWindowPeer;

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

    public static Rectangle[] concatRectangleArrays(Rectangle[] A, Rectangle[] B) {
        int aLen = A.length;
        int bLen = B.length;
        Rectangle[] C = new Rectangle[aLen + bLen];
        System.arraycopy(A, 0, C, 0, aLen);
        System.arraycopy(B, 0, C, aLen, bLen);
        return C;
    }

    public static WebToolkit getWebToolkit() {
        return ((WebToolkit) Toolkit.getDefaultToolkit());
    }

    public static WebWindowPeer findWindowPeerById(String id) {
        for (Window w : Window.getWindows()) {
            Object peer = WebToolkit.targetToPeer(w);
            if (peer instanceof WebWindowPeer) {
                if (((WebWindowPeer) peer).getGuid().equals(id)) {
                    return (WebWindowPeer) peer;
                }

            }
        }
        return null;
    }

    public static void extractWindowImages(Map<String, BufferedImage> windowImages, JsonAppFrame json) {
        for (JsonWindow window : json.getWindows()) {
            WebWindowPeer w = findWindowPeerById(window.getId());
            windowImages.put(window.getId(), w.extractSafeImage());
        }
    }

    public static void encodeWindowImages(Map<String, BufferedImage> windowImages, Map<String, List<Rectangle>> windowNonVisibleAreas, JsonAppFrame json, ImageServiceIfc imageService) {
        for (JsonWindow window : json.getWindows()) {
            BufferedImage result = windowImages.get(window.getId());
            if(windowNonVisibleAreas.containsKey(window.getId())){
                Graphics2D g=(Graphics2D) result.getGraphics();
                g.setBackground(new Color(0x000000,true));
                for(Rectangle n:windowNonVisibleAreas.get(window.getId())){
                    g.clearRect(n.x-window.getPosX(), n.y-window.getPosX(), n.width, n.height);
                }
                g.dispose();
            }
            if (result != null) {
                JsonWindowPartialContent c = window.getContent();
                if (c != null && c.getPositionX() != null && c.getPositionY() != null && c.getWidth() != null && c.getHeight() != null) {
                    BufferedImage cropped = result.getSubimage(c.getPositionX(), c.getPositionY(), c.getWidth(), c.getHeight());
                    String base64Content = imageService.encodeImage(cropped);
                    window.getContent().setBase64Content(base64Content);
                }
            }
        }
    }
}
