package org.webswing.containers;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;

import javax.swing.JWindow;

import org.webswing.ignored.common.GraphicsWrapper;
import org.webswing.ignored.common.PaintManager;
import org.webswing.ignored.common.WebWindow;
import org.webswing.ignored.model.s2c.JsonWindowInfo;
import org.webswing.util.Util;


public class WebJWindow extends JWindow implements WebWindow {

    /**
     * 
     */
    private static final long serialVersionUID = 2961442461315724672L;
    private BufferedImage virtualScreen;
    private BufferedImage diffScreen;
    private boolean webDirty=false;
    
    @Override
    public Graphics getGraphics() {
        if (virtualScreen == null || virtualScreen.getWidth() != this.getWidth() || virtualScreen.getHeight() != this.getHeight()) {
            virtualScreen = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_ARGB_PRE);
            diffScreen = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_ARGB_PRE);
        }
        return new GraphicsWrapper((Graphics2D) super.getGraphics(), this);
    }

    public BufferedImage getVirtualScreen() {
        return virtualScreen;
    }

    public BufferedImage getDiffWebData() {
        synchronized (this) {
            BufferedImage res;
            res = Util.deepCopy(diffScreen);
            resetScreen(diffScreen);
            webDirty=false;
            return res;
        }
    }

    public void resetScreen(BufferedImage img) {
        Graphics2D g = (Graphics2D) img.getGraphics();
        g.setBackground(new Color(0, 0, 0, 0));
        g.clearRect(0, 0, img.getWidth(), img.getHeight());
        g.dispose();
    }

    public void addChangesToDiff() {
        synchronized (this) {
            Graphics g = diffScreen.getGraphics();
            g.drawImage(virtualScreen, 0, 0, null);
            g.dispose();
            resetScreen(virtualScreen);
            webDirty=true;
        }
    }

    public Graphics2D getWebGraphics() {
        return (Graphics2D) virtualScreen.getGraphics();
    }

    public void setVirtualScreen(BufferedImage virtualScreen) {
        this.virtualScreen = virtualScreen;
    }

    public JsonWindowInfo getWindowInfo() {
        JsonWindowInfo result = new JsonWindowInfo();
        result.setHeight(this.getHeight());
        result.setWidth(this.getWidth());
        result.setHasFocus(true);
        result.setId(Util.getObjectIdentity(this));
        if (this.getParent() != null) {
            result.setParentId(Util.getObjectIdentity(this.getParent()));
        }
        return result;
    }

    public Point getFrameTranslation() {
        return new Point(0, 0);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void show() {
        PaintManager.getInstance().registerWindow(this);
        super.show();
    }

    @SuppressWarnings("deprecation")
    @Override
    public void hide() {
        PaintManager.getInstance().hideWindowInBrowser(this);
        super.hide();
    }

    @Override
    public void setVisible(boolean b) {
        PaintManager.getInstance().registerWindow(this);
        if (!b) {
            PaintManager.getInstance().hideWindowInBrowser(this);
        }
        super.setVisible(b);
    }

    @Override
    public void dispose() {
        PaintManager.getInstance().disposeWindow(this);
        super.dispose();
    }

    public boolean isWebDirty() {
        return webDirty;
    }

}
