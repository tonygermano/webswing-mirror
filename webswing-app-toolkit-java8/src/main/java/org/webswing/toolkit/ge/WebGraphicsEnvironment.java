package org.webswing.toolkit.ge;

import java.awt.GraphicsDevice;

import org.webswing.toolkit.WebToolkit;

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
    public void displayChanged() {
        screens=null;
        super.displayChanged();
    }

    @Override
    public boolean isDisplayLocal() {
        return true;
    }
}
