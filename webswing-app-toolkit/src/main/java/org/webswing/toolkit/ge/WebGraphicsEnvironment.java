package org.webswing.toolkit.ge;

import java.awt.GraphicsDevice;

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
        return WebGraphicsConfig.getWebGraphicsConfig(WebToolkit.screenWidth, WebToolkit.screenHeight).getDevice();
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
        screens=null;
        super.displayChanged();
    }
}
