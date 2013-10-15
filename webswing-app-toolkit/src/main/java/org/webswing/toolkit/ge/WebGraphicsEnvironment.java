package org.webswing.toolkit.ge;

import java.awt.GraphicsDevice;
import java.awt.image.BufferedImage;

import org.webswing.toolkit.WebToolkit;

import sun.awt.FontConfiguration;

@SuppressWarnings("restriction")
public class WebGraphicsEnvironment extends sun.java2d.SunGraphicsEnvironment{

    @Override
    protected int getNumScreens() {
        return 1;
    }

    @Override
    protected GraphicsDevice makeScreenDevice(int paramInt) {
        
        return WebGraphicsConfig.getWebGraphicsConfig(new BufferedImage(WebToolkit.screenWidth, WebToolkit.screenHeight, BufferedImage.TYPE_INT_ARGB)).getDevice();
    }

    @Override
    protected FontConfiguration createFontConfiguration() {
        return createFontConfiguration(false, false);
    }

    @Override
    public FontConfiguration createFontConfiguration(boolean paramBoolean1, boolean paramBoolean2) {
        return new WebFontConfiguration(this);
    }

    @Override
    public void displayChanged() {
        screens[0]=makeScreenDevice(1);
        super.displayChanged();
    }
}
