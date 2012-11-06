package sk.viktor.ignored.containers;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;

import sk.viktor.ignored.GraphicsWrapper;
import sk.viktor.ignored.PaintManager;
import sk.viktor.ignored.WebWindow;
import sk.viktor.ignored.model.JsonWindowInfo;

public class WebJFrame extends JFrame implements WebWindow {

    /**
      * 
      */
    private static final long serialVersionUID = -2131755526938257553L;
    private BufferedImage virtualScreen;

    public WebJFrame(String title) {
        super(title);
    }

    @Override
    public Graphics getGraphics() {
        if (virtualScreen == null || virtualScreen.getWidth() != this.getWidth() || virtualScreen.getHeight() != this.getHeight()) {
            virtualScreen = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_ARGB_PRE);
        }
        return new GraphicsWrapper((Graphics2D) super.getGraphics(), this);
    }

    public BufferedImage getVirtualScreen() {
        if (this.getRootPane() != null) {
            return virtualScreen.getSubimage(this.getRootPane().getX(), this.getRootPane().getY(), this.getRootPane().getWidth(), this.getRootPane().getHeight());
        }
        return virtualScreen;
    }

    public Graphics2D getWebGraphics() {
        return (Graphics2D) virtualScreen.getGraphics();
    }

    public void setVirtualScreen(BufferedImage virtualScreen) {
        this.virtualScreen = virtualScreen;
    }

    public JsonWindowInfo getWindowInfo() {
        JsonWindowInfo result = new JsonWindowInfo();
        result.setHeight(this.getVirtualScreen().getHeight());
        result.setWidth(this.getVirtualScreen().getWidth());
        result.setHasFocus(this.isFocused());
        result.setTitle(this.getTitle());
        result.setId(PaintManager.getObjectIdentity(this));
        return result;
    }

    public Point getFrameTranslation() {
        return new Point(this.getRootPane().getX(), this.getRootPane().getY());
    }

    @Override
    public void setVisible(boolean b) {
        super.setVisible(b);
        PaintManager.registerWindow(this);
    }

    @Override
    public void dispose() {
        super.dispose();
        PaintManager.disposeWindow(this);
    }

}
