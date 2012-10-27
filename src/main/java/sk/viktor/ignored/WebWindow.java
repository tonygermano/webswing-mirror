package sk.viktor.ignored;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;

import sk.viktor.ignored.model.JsonWindowInfo;


public interface WebWindow {
    
    public BufferedImage getVirtualScreen();

    
    public void setVirtualScreen(BufferedImage virtualScreen) ;
    
    public JsonWindowInfo getWindowInfo();
    
    public Graphics2D getWebGraphics();
    
    public Point getFrameTranslation();
}
