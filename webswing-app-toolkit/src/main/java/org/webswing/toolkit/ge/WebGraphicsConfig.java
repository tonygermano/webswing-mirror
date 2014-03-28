package org.webswing.toolkit.ge;

import java.awt.Component;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DirectColorModel;
import java.awt.image.WritableRaster;

import sun.awt.image.BufferedImageGraphicsConfig;
import sun.awt.image.OffScreenImage;

@SuppressWarnings("restriction")
public class WebGraphicsConfig extends GraphicsConfiguration {

    private static BufferedImage template = new BufferedImage(1,1,BufferedImage.TYPE_INT_ARGB);
    
    BufferedImageGraphicsConfig imageConfig;
    WebScreenDevice device;
    int width;
    int height;

    public static WebGraphicsConfig getWebGraphicsConfig(int width,int height) {
        return new WebGraphicsConfig(width,height);
    }

    private WebGraphicsConfig(int width,int height) {
        this.width=width;
        this.height=height;
        imageConfig = BufferedImageGraphicsConfig.getConfig(template);
    }

    @Override
    public GraphicsDevice getDevice() {
        if (device==null) {
            device =new WebScreenDevice(imageConfig.getDevice(), this);
        }
        return device;
    }

    @Override
    public BufferedImage createCompatibleImage(int paramInt1, int paramInt2) {
        return imageConfig.createCompatibleImage(paramInt1, paramInt2);
    }

    @Override
    public ColorModel getColorModel() {

        return imageConfig.getColorModel();
    }

    @Override
    public ColorModel getColorModel(int paramInt) {
        switch (paramInt) {
            case 1:
                return getColorModel();
            case 2:
                return new DirectColorModel(25, 16711680, 65280, 255, 16777216);
            case 3:
                return ColorModel.getRGBdefault();
        }
        return null;
    }

    @Override
    public AffineTransform getDefaultTransform() {
        return imageConfig.getDefaultTransform();
    }

    @Override
    public AffineTransform getNormalizingTransform() {
        return imageConfig.getNormalizingTransform();
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle(0,0,width,height);
    }

    public Image createAcceleratedImage(Component target, int paramInt1, int paramInt2) {
        ColorModel localColorModel = getColorModel(1);
        WritableRaster localWritableRaster = localColorModel.createCompatibleWritableRaster(paramInt1, paramInt2);
        return new OffScreenImage(target, localColorModel, localWritableRaster, localColorModel.isAlphaPremultiplied());
    }

}
