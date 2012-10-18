package sk.viktor.ignored;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.awt.image.IndexColorModel;
import java.awt.image.WritableRaster;
import java.util.Hashtable;

public class MyImage extends BufferedImage {

    public MyImage(int width, int height) {
        super(width, height, BufferedImage.TYPE_INT_ARGB_PRE);
    }
 

    public Graphics getGraphics() {
        return new MyWrapperdGraphics(this);
    }

}
