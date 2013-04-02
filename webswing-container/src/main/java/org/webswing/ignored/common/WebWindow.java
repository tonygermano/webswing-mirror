package org.webswing.ignored.common;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;

import org.webswing.ignored.model.s2c.JsonWindowInfo;


public interface WebWindow {

    public BufferedImage getVirtualScreen();

    public void setVirtualScreen(BufferedImage virtualScreen);

    public JsonWindowInfo getWindowInfo();

    public Graphics2D getWebGraphics();

    public Point getFrameTranslation();

    public byte[] getDiffWebData();

    public void addChangesToDiff();
    
    public boolean isWebDirty();
}
