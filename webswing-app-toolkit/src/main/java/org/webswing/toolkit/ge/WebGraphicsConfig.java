package org.webswing.toolkit.ge;

import java.awt.Component;
import java.awt.GraphicsDevice;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;

import sun.awt.image.BufferedImageGraphicsConfig;
import sun.awt.image.OffScreenImage;

@SuppressWarnings("restriction")
public class WebGraphicsConfig extends BufferedImageGraphicsConfig {

    WebScreenDevice device;

    public static WebGraphicsConfig getWebGraphicsConfig(int width, int height) {
        return new WebGraphicsConfig(width, height);
    }

    private WebGraphicsConfig(int width, int height) {
        super(new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB), null);
    }

    @Override
    public GraphicsDevice getDevice() {
        if (device == null) {
            device = new WebScreenDevice(super.getDevice(), this);
        }
        return device;
    }

    public Image createAcceleratedImage(Component target, int paramInt1, int paramInt2) {
        ColorModel localColorModel = getColorModel(1);
        WritableRaster localWritableRaster = localColorModel.createCompatibleWritableRaster(paramInt1, paramInt2);
        return new OffScreenImage(target, localColorModel, localWritableRaster, localColorModel.isAlphaPremultiplied());
    }

    //    @Override
    //    public VolatileImage createCompatibleVolatileImage(int paramInt1, int paramInt2, ImageCapabilities paramImageCapabilities, int paramInt3) throws AWTException {
    //    }

}
