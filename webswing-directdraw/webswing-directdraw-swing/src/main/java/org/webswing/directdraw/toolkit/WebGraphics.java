package org.webswing.directdraw.toolkit;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.ImageObserver;
import java.awt.image.RenderedImage;
import java.io.IOException;

public class WebGraphics extends AbstractVectorGraphics {

    WebImage thisImage;
    private DrawInstructionFactory dif;
    private boolean disposed = false;
    private int id;

    public WebGraphics(WebImage webImage) {
        super(new Dimension(webImage.getWidth(null), webImage.getHeight(null)));
        this.thisImage = webImage;
        this.dif= thisImage.getContext().getInstructionFactory();
        this.id = this.thisImage.getNextGraphicsId();
        this.thisImage.addInstruction(null, dif.createGraphics(this, null));
    }

    public WebGraphics(WebGraphics g) {
        super(g);
        this.thisImage = g.thisImage;
        this.dif= thisImage.getContext().getInstructionFactory();
        this.id = this.thisImage.getNextGraphicsId();
        this.thisImage.addInstruction(null, dif.createGraphics(this, g));
    }

    @Override
    public void draw(Shape s) {
        if (getStroke() instanceof BasicStroke) {
            thisImage.addInstruction(this, dif.draw(s, getClip()));
        } else {
            thisImage.addInstruction(this, dif.fill(getStroke().createStrokedShape(s), getClip()));
        }
    }

    @Override
    public void fill(Shape s) {
        thisImage.addInstruction(this, dif.fill(s, getClip()));
    }

    @Override
    protected void writeImage(RenderedImage image, AffineTransform xform) throws IOException {
    	thisImage.addImage(this, image, xform);
    }

    @Override
    protected void writeImage(Image image, ImageObserver observer, AffineTransform xform, Rectangle2D.Float crop, Color bkg) throws IOException {
        if (image instanceof WebImage) {
            crop = crop != null ? crop : new Rectangle2D.Float(0, 0, image.getWidth(observer), image.getHeight(observer));
            thisImage.addInstruction(this, dif.drawImage((WebImage) image, xform, crop, bkg, getClip()));
        } else {
            thisImage.addImage(this, image, observer, xform, crop, bkg);
        }
    }

    @Override
    protected void writeString(String string, double x, double y) throws IOException {
        thisImage.addInstruction(this, dif.drawString(string, x, y, getFont(), getClip()));
    }

    @Override
    protected void writeTransform(AffineTransform transform) throws IOException {
        thisImage.addInstruction(this, dif.transform(transform));

    }

    @Override
    protected void writePaint(Paint paint) throws IOException {
        thisImage.addInstruction(this, dif.setPaint(paint));

    }

    @Override
    public void writeStroke(Stroke stroke) throws IOException {
        if (stroke instanceof BasicStroke) {
            thisImage.addInstruction(this, dif.setStroke((BasicStroke) stroke));
        }
    }

    @Override
    public void copyArea(int x, int y, int width, int height, int dx, int dy) {
        thisImage.addInstruction(this, dif.copyArea(x, y, width, height, dx, dy, getClip()));
    }

    @Override
    public Graphics create() {
        return new WebGraphics(this);
    }

    @Override
    public void setPaintMode() {
        // TODO Auto-generated method stub

    }

    @Override
    public void setXORMode(Color c1) {
        // TODO Auto-generated method stub

    }

    @Override
    public GraphicsConfiguration getDeviceConfiguration() {
        // TODO Auto-generated method stub
        return null;
    }

    public boolean isDisposed() {
        return disposed;
    }

    @Override
    public void dispose() {
        thisImage.addInstruction(this, dif.disposeGraphics(this));
        disposed = true;
    }

    public int getId() {
        return id;
    }

}
