package sk.viktor.containers;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;

import javax.swing.JDialog;

import sk.viktor.SwingClassloader;
import sk.viktor.ignored.common.GraphicsWrapper;
import sk.viktor.ignored.common.PaintManager;
import sk.viktor.ignored.common.WebWindow;
import sk.viktor.ignored.model.s2c.JsonWindowInfo;
import sk.viktor.util.Util;

public class WebJDialog extends JDialog implements WebWindow {

    /**
     * 
     */
    private static final long serialVersionUID = 7233902228541154553L;
    private BufferedImage virtualScreen;

    @Override
    public Graphics getGraphics() {
        if (virtualScreen == null || virtualScreen.getWidth() != this.getWidth() || virtualScreen.getHeight() != this.getHeight()) {
            virtualScreen = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_ARGB_PRE);
        }
        return new GraphicsWrapper((Graphics2D) super.getGraphics(), this);
    }

    public BufferedImage getVirtualScreen() {
        if(this.getRootPane()!=null){
            return virtualScreen.getSubimage(this.getRootPane().getX(), this.getRootPane().getY(),this.getRootPane().getWidth(), this.getRootPane().getHeight());
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
        result.setId(Util.getObjectIdentity(this));
        if (this.getParent() != null) {
            result.setParentId(Util.getObjectIdentity(this.getParent()));
        }
        result.setModal(this.isModal());
        return result;
    }

    public Point getFrameTranslation() {
        return new Point(this.getRootPane().getX(), this.getRootPane().getY());
    }

    public String getClientId(){
        if(this.getClass().getClassLoader() instanceof SwingClassloader){
            return ((SwingClassloader)Util.class.getClassLoader()).getClientId();
        }
        return null;
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
