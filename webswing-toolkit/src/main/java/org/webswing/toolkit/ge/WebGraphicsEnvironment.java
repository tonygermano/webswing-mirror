package org.webswing.toolkit.ge;

import java.awt.GraphicsDevice;
import java.awt.image.BufferedImage;

import sun.awt.FontConfiguration;
import  sun.awt.image.BufferedImageDevice;
import  sun.awt.image.BufferedImageGraphicsConfig;

public class WebGraphicsEnvironment extends sun.java2d.SunGraphicsEnvironment{

    @Override
    protected int getNumScreens() {
        return 1;
    }

    @Override
    protected GraphicsDevice makeScreenDevice(int paramInt) {
        return WebGraphicsConfig.getWebGraphicsConfig(new BufferedImage(1000, 1000, BufferedImage.TYPE_INT_ARGB)).getDevice();
    }

    @Override
    protected FontConfiguration createFontConfiguration() {
        return createFontConfiguration(false, false);
    }

    @Override
    public FontConfiguration createFontConfiguration(boolean paramBoolean1, boolean paramBoolean2) {
        return new WebFontConfiguration(this);
    }

}
