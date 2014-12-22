package org.webswing.directdraw.util;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.font.TextAttribute;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ImageObserver;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.util.Hashtable;
import java.util.Properties;

import org.webswing.directdraw.model.DrawConstant;

public class DirectDrawUtils {

    public static final Properties windowsFonts = new Properties();
    static {
        // logical fonts
        windowsFonts.setProperty("Dialog", "Arial");
        windowsFonts.setProperty("DialogInput", "Courier New");
        windowsFonts.setProperty("Serif", "Times New Roman");
        windowsFonts.setProperty("SansSerif", "Arial");
        windowsFonts.setProperty("Monospaced", "Courier New");
    }

    /**
     * there is a bug in the jdk 1.6 which makes
     * Font.getAttributes() not work correctly. The
     * method does not return all values. What we dow here
     * is using the old JDK 1.5 method.
     *
     * @param font font
     * @return Attributes of font
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static Hashtable getAttributes(Font font) {
        Hashtable result = new Hashtable(7, (float) 0.9);
        result.put(TextAttribute.TRANSFORM, font.getTransform());
        result.put(TextAttribute.FAMILY, font.getName());
        result.put(TextAttribute.SIZE, new Float(font.getSize2D()));
        result.put(TextAttribute.WEIGHT, (font.getStyle() & Font.BOLD) != 0 ? TextAttribute.WEIGHT_BOLD : TextAttribute.WEIGHT_REGULAR);
        result.put(TextAttribute.POSTURE, (font.getStyle() & Font.ITALIC) != 0 ? TextAttribute.POSTURE_OBLIQUE : TextAttribute.POSTURE_REGULAR);
        result.put(TextAttribute.SUPERSCRIPT, new Integer(0 /* no getter! */));
        result.put(TextAttribute.WIDTH, new Float(1 /* no getter */));
        return result;
    }

    public static BufferedImage createBufferedImage(Image image, ImageObserver observer, Color bkg) {
        if ((bkg == null) && ((image instanceof BufferedImage))) {
            return (BufferedImage) image;
        }
        BufferedImage bufferedImage = new BufferedImage(image.getWidth(observer), image.getHeight(observer), bkg == null ? BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_INT_RGB);

        Graphics g = bufferedImage.getGraphics();
        if (bkg == null) {
            g.drawImage(image, 0, 0, observer);
        } else {
            g.drawImage(image, 0, 0, bkg, observer);
        }
        return bufferedImage;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static BufferedImage createBufferedImage(RenderedImage img) {
        if (img instanceof BufferedImage) {
            return (BufferedImage) img;
        }
        ColorModel cm = img.getColorModel();
        int width = img.getWidth();
        int height = img.getHeight();
        WritableRaster raster = cm.createCompatibleWritableRaster(width, height);
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        Hashtable properties = new Hashtable();
        String[] keys = img.getPropertyNames();
        if (keys != null) {
            for (int i = 0; i < keys.length; i++) {
                properties.put(keys[i], img.getProperty(keys[i]));
            }
        }
        BufferedImage result = new BufferedImage(cm, raster, isAlphaPremultiplied, properties);
        img.copyData(raster);
        return result;
    }
    
    public static DrawConstant[] concat(DrawConstant[] a, DrawConstant[] b) {
    	   int aLen = a.length;
    	   int bLen = b.length;
    	   DrawConstant[] c= new DrawConstant[aLen+bLen];
    	   System.arraycopy(a, 0, c, 0, aLen);
    	   System.arraycopy(b, 0, c, aLen, bLen);
    	   return c;
    	}

}
