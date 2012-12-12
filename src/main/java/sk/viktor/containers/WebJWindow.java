package sk.viktor.containers;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;

import javax.swing.JWindow;

import sk.viktor.ignored.common.GraphicsWrapper;
import sk.viktor.ignored.common.PaintManager;
import sk.viktor.ignored.common.WebWindow;
import sk.viktor.ignored.model.s2c.JsonWindowInfo;
import sk.viktor.util.Util;

public class WebJWindow extends JWindow implements WebWindow {

    /**
     * 
     */
    private static final long serialVersionUID = 2961442461315724672L;
    private BufferedImage virtualScreen;
    private BufferedImage diffScreen;

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

    public byte[] getDiffWebData() {
        synchronized (this) {
            byte[] res;
            res = Util.getPngImage(diffScreen);
            resetScreen(diffScreen);
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
        result.setHasFocus(this.isFocused());
        result.setId(Util.getObjectIdentity(this));
        if (this.getParent() != null) {
            result.setParentId(Util.getObjectIdentity(this.getParent()));
        }
        return result;
    }

    public Point getFrameTranslation() {
        return new Point(0, 0);
    }

    public String getClientId() {
        return Util.resolveClientId(this.getClass());
    }

    @Override
    public void setVisible(boolean b) {
        super.setVisible(b);
        PaintManager.getInstance(getClientId()).registerWindow(this);
    }

    @Override
    public void dispose() {
        super.dispose();
        PaintManager.getInstance(getClientId()).disposeWindow(this);
    }
    
}
