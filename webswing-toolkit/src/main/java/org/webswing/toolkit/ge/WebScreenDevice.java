package org.webswing.toolkit.ge;

import sun.awt.image.BufferedImageDevice;
import sun.awt.image.BufferedImageGraphicsConfig;

import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

public class WebScreenDevice extends GraphicsDevice {

    BufferedImageDevice imageDevice;
    GraphicsConfiguration c;

    public WebScreenDevice(GraphicsDevice imageDevice,GraphicsConfiguration c) {
        this.imageDevice = (BufferedImageDevice) imageDevice;
        this.c=c;
    }

    @Override
    public int getType() {
        return 0;
    }

    @Override
    public String getIDstring() {
        return imageDevice.getIDstring();
    }

    @Override
    public GraphicsConfiguration[] getConfigurations() {
        return new GraphicsConfiguration[]{c};
    }

    @Override
    public GraphicsConfiguration getDefaultConfiguration() {
        return c;
    }

}
