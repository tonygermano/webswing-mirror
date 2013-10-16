package org.webswing.toolkit;

import java.awt.AWTEvent;
import java.awt.AWTException;
import java.awt.BufferCapabilities;
import java.awt.BufferCapabilities.FlipContents;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.SystemColor;
import java.awt.Toolkit;
import java.awt.event.PaintEvent;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.awt.image.VolatileImage;
import java.awt.peer.ComponentPeer;
import java.awt.peer.ContainerPeer;
import java.util.UUID;

import org.webswing.common.GraphicsWrapper;
import org.webswing.dispatch.WebPaintDispatcher;
import org.webswing.toolkit.ge.WebGraphicsConfig;
import org.webswing.util.Util;

import sun.awt.AWTAccessor;
import sun.awt.CausedFocusEvent.Cause;
import sun.awt.PaintEventDispatcher;
import sun.awt.RepaintArea;
import sun.awt.event.IgnorePaintEvent;
import sun.awt.image.OffScreenImage;
import sun.awt.image.SunVolatileImage;
import sun.awt.image.SurfaceManager;
import sun.awt.image.ToolkitImage;
import sun.java2d.InvalidPipeException;
import sun.java2d.ScreenUpdateManager;
import sun.java2d.SunGraphics2D;
import sun.java2d.SurfaceData;
import sun.java2d.pipe.Region;

@SuppressWarnings("restriction")
public class WebComponentPeer implements ComponentPeer {

    private String guid = UUID.randomUUID().toString();
    
    public String getGuid() {
        return guid;
    }

    public BufferedImage extractSafeImage(Rectangle sub) {
        BufferedImage safeImage = new BufferedImage(sub.width, sub.height, BufferedImage.TYPE_4BYTE_ABGR);
        Graphics g = safeImage.getGraphics();
        g.drawImage(image, 0, 0,sub.width, sub.height,sub.x, sub.y,sub.x+sub.width, sub.y+sub.height, null);
        g.dispose();
        return safeImage;
    }

    //////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////// WebComponentPeer Implementation//////////////////////////////////////////////////

    private static final Font defaultFont = new Font("Dialog", 0, 12);
    protected long pData; //???
    protected boolean destroyed;
    protected Object target;
    private RepaintArea paintArea;
    private int oldWidth;
    private int oldHeight;
    private SurfaceData surfaceData;
    private OffScreenImage image;
    private Object background;
    private Object foreground;
    private Font font;

    public static WebComponentPeer getPeerForTarget(Object paramObject) {
        WebComponentPeer localWObjectPeer = (WebComponentPeer) WebToolkit.targetToPeer(paramObject);
        return localWObjectPeer;
    }

    public WebComponentPeer(Component t) {
        this.target = t;
        this.paintArea = new RepaintArea();
        if (((Component) this.target).isVisible()) {
            show();
        }
        Color localColor = ((Component) this.target).getForeground();
        if (localColor != null) {
            setForeground(localColor);
        }

        Font localFont = ((Component) this.target).getFont();
        if (localFont != null) {
            setFont(localFont);
        }
        if (!(((Component) this.target).isEnabled())) {
            disable();
        }
        Rectangle localRectangle = ((Component) this.target).getBounds();
        setBounds(localRectangle.x, localRectangle.y, localRectangle.width, localRectangle.height, 3);
    }

    public boolean isObscured() {
        return false;
    }

    public boolean canDetermineObscurity() {
        return false;
    }

    public void setVisible(boolean paramBoolean) {
        if (paramBoolean) {
            show();
        } else {
            hide();
        }
    }

    public void setEnabled(boolean paramBoolean) {
        if (paramBoolean)
            enable();
        else
            disable();
    }

    public void paint(Graphics paramGraphics) {
       ((Component) this.target).paint(paramGraphics);
    }

    public void repaint(long paramLong, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
        ((Component) this.target).repaint(paramLong,paramInt1,paramInt2,paramInt3,paramInt4);
    }

    public void print(Graphics paramGraphics) {
        // not supported now
    }

    public void setBounds(int x, int y, int w, int h, int paramInt5) {
        synchronized (WebPaintDispatcher.webPaintLock) {
            if (((Component) target).isShowing()) {
                Point location = ((Component) target).getLocationOnScreen();
                Util.getWebToolkit().getPaintDispatcher().notifyWindowBoundsChanged(getGuid(), new Rectangle(location.x, location.y, w, h));
            }
            if ((w != this.oldWidth) || (h != this.oldHeight)) {
                try {
                    replaceSurfaceData(x, y, w, h);
                } catch (InvalidPipeException e) {
                    e.printStackTrace();
                }
                this.oldWidth = w;
                this.oldHeight = h;
            }
        }
    }

    public void replaceSurfaceData(int x, int y, int w, int h) {
        SurfaceData localSurfaceData = null;
        synchronized (((Component) this.target).getTreeLock()) {
            synchronized (this) {
                WebGraphicsConfig localWebGraphicsConfig = (WebGraphicsConfig) getGraphicsConfiguration();
                this.image = (OffScreenImage) localWebGraphicsConfig.createAcceleratedImage((Component) this.target, w, h);
                localSurfaceData = this.surfaceData;
                this.surfaceData = SurfaceManager.getManager(this.image).getDestSurfaceData();
                repaintPeerTarget();
                if (localSurfaceData != null) {
                    localSurfaceData.invalidate();
                }
            }
        }
        if (localSurfaceData != null) {
            localSurfaceData.flush();
            localSurfaceData = null;
        }
    }

    public void handleEvent(AWTEvent paramAWTEvent) {
        //System.out.println(paramAWTEvent);
    }

    public void coalescePaintEvent(PaintEvent paramPaintEvent) {
        Rectangle localRectangle = paramPaintEvent.getUpdateRect();
        if (!(paramPaintEvent instanceof IgnorePaintEvent))
            this.paintArea.add(localRectangle, paramPaintEvent.getID());
    }

    private void repaintPeerTarget() {
        Component localComponent = (Component) this.target;
        Rectangle localRectangle = AWTAccessor.getComponentAccessor().getBounds(localComponent);
        if (!(((Component) this.target).getIgnoreRepaint())) {
            PaintEvent localPaintEvent = PaintEventDispatcher.getPaintEventDispatcher().createPaintEvent((Component) this.target, 0, 0, localRectangle.width, localRectangle.height);
            if (localPaintEvent != null)
                postEvent(localPaintEvent);
        }
    }

    public Point getLocationOnScreen() {
        return getBounds().getLocation();
    }

    public Dimension getPreferredSize() {
        return getMinimumSize();
    }

    public Dimension getMinimumSize() {
        return ((Component) this.target).getSize();
    }

    public ColorModel getColorModel() {
        GraphicsConfiguration localGraphicsConfiguration = getGraphicsConfiguration();
        if (localGraphicsConfiguration != null) {
            return localGraphicsConfiguration.getColorModel();
        }
        return null;
    }

    public Toolkit getToolkit() {
        return Toolkit.getDefaultToolkit();
    }

    public Graphics getGraphics() {
        //System.out.println("getGraphics");
        //try{throw new Exception();}catch(Exception e){e.printStackTrace();if(e!=null){return null;}}

        SurfaceData localSurfaceData = this.surfaceData;
        if (localSurfaceData != null) {
            Object localObject1 = this.background;
            if (localObject1 == null) {
                localObject1 = SystemColor.window;
            }
            Object localObject2 = this.foreground;
            if (localObject2 == null) {
                localObject2 = SystemColor.windowText;
            }
            Font localFont = this.font;
            if (localFont == null) {
                localFont = defaultFont;
            }
            return new GraphicsWrapper(new SunGraphics2D(localSurfaceData, (Color) localObject2, (Color) localObject1, localFont), this, true);
        }
        return null;
    }

    public FontMetrics getFontMetrics(Font paramFont) {
        // TODO Auto-generated method stub
        return null;
    }

    public void dispose() {
        synchronized (WebPaintDispatcher.webPaintLock) {
            Util.getWebToolkit().getPaintDispatcher().notifyWindowClosed(getGuid());
            SurfaceData localSurfaceData = this.surfaceData;
            this.surfaceData = null;
            ScreenUpdateManager.getInstance().dropScreenSurface(localSurfaceData);
            localSurfaceData.invalidate();
            WebToolkit.targetDisposedPeer(this.target, this);
        }
    }

    public void setForeground(Color paramColor) {
        this.foreground = paramColor;
    }

    public void setBackground(Color paramColor) {
        this.background = paramColor;

    }

    public void setFont(Font paramFont) {
        this.font = paramFont;
    }

    public void updateCursorImmediately() {
        // TODO Auto-generated method stub

    }

    public boolean requestFocus(Component paramComponent, boolean paramBoolean1, boolean paramBoolean2, long paramLong, Cause paramCause) {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean isFocusable() {
        // TODO Auto-generated method stub
        return false;
    }

    public Image createImage(ImageProducer paramImageProducer) {
        return new ToolkitImage(paramImageProducer);
    }

    public Image createImage(int paramInt1, int paramInt2) {
        WebGraphicsConfig localwebGraphicsConfig = (WebGraphicsConfig) getGraphicsConfiguration();
        return localwebGraphicsConfig.createAcceleratedImage((Component) this.target, paramInt1, paramInt2);
    }

    public VolatileImage createVolatileImage(int paramInt1, int paramInt2) {
        return new SunVolatileImage((Component) this.target, paramInt1, paramInt2);
    }

    public boolean prepareImage(Image paramImage, int paramInt1, int paramInt2, ImageObserver paramImageObserver) {
        return getToolkit().prepareImage(paramImage, paramInt1, paramInt2, paramImageObserver);
    }

    public int checkImage(Image paramImage, int paramInt1, int paramInt2, ImageObserver paramImageObserver) {
        return getToolkit().checkImage(paramImage, paramInt1, paramInt2, paramImageObserver);
    }

    public GraphicsConfiguration getGraphicsConfiguration() {
        return ((WebToolkit) getToolkit()).getGraphicsConfig();
    }

    public boolean handlesWheelScrolling() {
        // TODO Auto-generated method stub
        return false;
    }

    public void createBuffers(int paramInt, BufferCapabilities paramBufferCapabilities) throws AWTException {
        throw new AWTException("The operation requested is not supported");
    }

    public Image getBackBuffer() {
        return null;
    }

    public void flip(int paramInt1, int paramInt2, int paramInt3, int paramInt4, FlipContents paramFlipContents) {
        // TODO Auto-generated method stub

    }

    public void destroyBuffers() {
        // TODO Auto-generated method stub

    }

    public void reparent(ContainerPeer paramContainerPeer) {
        // TODO Auto-generated method stub

    }

    public boolean isReparentSupported() {
        return false;
    }

    public void layout() {
    }

    public Rectangle getBounds() {
        return ((Component) this.target).getBounds();
    }

    public void applyShape(Region paramRegion) {
    }

    public Dimension preferredSize() {
        return getPreferredSize();
    }

    public Dimension minimumSize() {
        return getMinimumSize();
    }

    public void show() {
    }

    public void hide() {
    }

    public void enable() {

    }

    public void disable() {
    }

    public void reshape(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    }

    void postEvent(AWTEvent paramAWTEvent) {
        WebToolkit.postEvent(WebToolkit.targetToAppContext(this.target), paramAWTEvent);
    }

    public Object getTarget() {
        return target;
    }

}
