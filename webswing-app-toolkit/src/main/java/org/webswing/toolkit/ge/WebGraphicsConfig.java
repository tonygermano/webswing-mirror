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
import java.util.HashMap;
import java.util.Map;

import sun.awt.image.BufferedImageGraphicsConfig;
import sun.awt.image.OffScreenImage;

@SuppressWarnings("restriction")
public class WebGraphicsConfig extends GraphicsConfiguration {

    private static Map<Integer, WebGraphicsConfig> configs = new HashMap<Integer, WebGraphicsConfig>();
    private static Map<Integer, WebScreenDevice> devices = new HashMap<Integer, WebScreenDevice>();

    BufferedImageGraphicsConfig imageConfig;

    public static WebGraphicsConfig getWebGraphicsConfig(BufferedImage image) {
        if (!configs.containsKey(System.identityHashCode(image))) {
            configs.put(System.identityHashCode(image), new WebGraphicsConfig(image));
        }
        return configs.get(System.identityHashCode(image));

    }

    private WebGraphicsConfig(BufferedImage image) {
        imageConfig = BufferedImageGraphicsConfig.getConfig(image);
    }

    @Override
    public GraphicsDevice getDevice() {
        if (!devices.containsKey(System.identityHashCode(imageConfig.getDevice()))) {
            devices.put(System.identityHashCode(imageConfig.getDevice()), new WebScreenDevice(imageConfig.getDevice(), this));
        }
        return devices.get(System.identityHashCode(imageConfig.getDevice()));
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
        return imageConfig.getBounds();
    }

    public Image createAcceleratedImage(Component target, int paramInt1, int paramInt2) {
        ColorModel localColorModel = getColorModel(1);
        WritableRaster localWritableRaster = localColorModel.createCompatibleWritableRaster(paramInt1, paramInt2);
        return new OffScreenImage(target, localColorModel, localWritableRaster, localColorModel.isAlphaPremultiplied());
    }

}
