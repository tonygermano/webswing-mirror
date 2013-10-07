package org.webswing.common;

import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.RenderingHints.Key;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ImageObserver;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderableImage;
import java.text.AttributedCharacterIterator;
import java.util.Map;

import org.webswing.toolkit.WebComponentPeer;

public class GraphicsWrapper extends Graphics2D {

    private WebComponentPeer rootPaintComponent;

    private boolean rootGraphics;
    
    private Graphics2D original;

    public GraphicsWrapper(Graphics2D g,WebComponentPeer wcp, boolean rootGraphics) {
        this.original = g;
        this.rootPaintComponent=wcp;
        this.rootGraphics= rootGraphics;
    }

    private void addDirtyClipArea() {
        Rectangle r = new Rectangle(getClipBounds());
        addDirtyRectangleArea(r);
    }
    private void addDirtyRectangleArea(Rectangle r) {
        r.translate((int)getTransform().getTranslateX(), (int)getTransform().getTranslateY());
        rootPaintComponent.addDirtyArea(r);
    }

    public WebComponentPeer getRootPaintComponent() {
        return rootPaintComponent;
    }

    public void setRootPaintComponent(WebComponentPeer rootPaintComponent) {
        this.rootPaintComponent = rootPaintComponent;
    }

    public Graphics getOriginal() {
        return original;
    }

    @Override
    public Graphics create() {
        GraphicsWrapper copy = new GraphicsWrapper((Graphics2D) original.create(),rootPaintComponent,false);
        return copy;
    }

    @Override
    public void translate(int x, int y) {
        original.translate(x, y);
    }

    @Override
    public Color getColor() {
        return original.getColor();
    }

    @Override
    public void setColor(Color c) {
        original.setColor(c);
    }

    @Override
    public void setPaintMode() {
        original.setPaintMode();
    }

    @Override
    public void setXORMode(Color c1) {
        original.setXORMode(c1);
    }

    @Override
    public Font getFont() {
        return original.getFont();
    }

    @Override
    public void setFont(Font font) {
        original.setFont(font);
    }

    @Override
    public FontMetrics getFontMetrics(Font f) {
        return original.getFontMetrics(f);
    }

    @Override
    public Rectangle getClipBounds() {
        return original.getClipBounds();
    }

    @Override
    public void clipRect(int x, int y, int width, int height) {
        original.clipRect(x, y, width, height);
    }

    @Override
    public void setClip(int x, int y, int width, int height) {
        original.setClip(x, y, width, height);
    }

    @Override
    public Shape getClip() {
        return original.getClip();
    }

    @Override
    public void setClip(Shape clip) {
        original.setClip(clip);
    }

    @Override
    public void copyArea(int x, int y, int width, int height, int dx, int dy) {
        original.copyArea(x, y, width, height, dx, dy);
        Rectangle r = new Rectangle(x+dx,y+dx,width+dx,height+dy);
        addDirtyRectangleArea(r);
    }


    @Override
    public void drawLine(int x1, int y1, int x2, int y2) {
        original.drawLine(x1, y1, x2, y2);
        Rectangle r = new Rectangle(x1, y1, x2, y2);
        addDirtyRectangleArea(r);
    }

    @Override
    public void fillRect(int x, int y, int width, int height) {
        original.fillRect(x, y, width, height);
        addDirtyRectangleArea(new Rectangle(x, y, width, height));
    }

    @Override
    public void clearRect(int x, int y, int width, int height) {
        original.clearRect(x, y, width, height);
        addDirtyRectangleArea(new Rectangle(x, y, width, height));
    }

    @Override
    public void drawRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
        original.drawRoundRect(x, y, width, height, arcWidth, arcHeight);
        addDirtyRectangleArea(new Rectangle(x, y, width, height));
    }

    @Override
    public void fillRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
        original.fillRoundRect(x, y, width, height, arcWidth, arcHeight);
        addDirtyRectangleArea(new Rectangle(x, y, width, height));
    }

    @Override
    public void drawOval(int x, int y, int width, int height) {
        original.drawOval(x, y, width, height);
        addDirtyRectangleArea(new Rectangle(x, y, width, height));
    }

    @Override
    public void fillOval(int x, int y, int width, int height) {
        original.fillOval(x, y, width, height);
        addDirtyRectangleArea(new Rectangle(x, y, width, height));
    }

    @Override
    public void drawArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
        original.drawArc(x, y, width, height, startAngle, arcAngle);
        addDirtyRectangleArea(new Rectangle(x, y, width, height));
    }

    @Override
    public void fillArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
        original.fillArc(x, y, width, height, startAngle, arcAngle);
        addDirtyRectangleArea(new Rectangle(x, y, width, height));
    }

    @Override
    public void drawPolyline(int[] xPoints, int[] yPoints, int nPoints) {
        original.drawPolyline(xPoints, yPoints, nPoints);
        addDirtyClipArea();
    }

    @Override
    public void drawPolygon(int[] xPoints, int[] yPoints, int nPoints) {
        original.drawPolygon(xPoints, yPoints, nPoints);
        addDirtyClipArea();
    }

    @Override
    public void fillPolygon(int[] xPoints, int[] yPoints, int nPoints) {
        original.fillPolygon(xPoints, yPoints, nPoints);
        addDirtyClipArea();
    }

    @Override
    public void drawString(String str, int x, int y) {
        original.drawString(str, x, y);
        addDirtyClipArea();
    }

    @Override
    public void drawString(AttributedCharacterIterator iterator, int x, int y) {
        original.drawString(iterator, x, y);
        addDirtyClipArea();
    }

    @Override
    public boolean drawImage(Image img, int x, int y, ImageObserver observer) {
        addDirtyClipArea();
        return original.drawImage(img, x, y, observer);
    }



    @Override
    public boolean drawImage(Image img, int x, int y, int width, int height, ImageObserver observer) {
        addDirtyClipArea();
        return original.drawImage(img, x, y, width, height, observer);
    }

    @Override
    public boolean drawImage(Image img, int x, int y, Color bgcolor, ImageObserver observer) {
        addDirtyClipArea();
        return original.drawImage(img, x, y, bgcolor, observer);
    }

    @Override
    public boolean drawImage(Image img, int x, int y, int width, int height, Color bgcolor, ImageObserver observer) {
        addDirtyClipArea();
        return original.drawImage(img, x, y, width, height, bgcolor, observer);
    }

    @Override
    public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2, ImageObserver observer) {
        addDirtyClipArea();
        return original.drawImage(img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, observer);
    }

    @Override
    public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2, Color bgcolor, ImageObserver observer) {
        addDirtyClipArea();
        return original.drawImage(img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, bgcolor, observer);
    }

    @Override
    public void dispose() {
        original.dispose();
        if(rootGraphics){
            rootPaintComponent.sendPaint();
        }
    }

    @Override
    public void draw(Shape s) {
        addDirtyClipArea();
        original.draw(s);
    }

    @Override
    public boolean drawImage(Image img, AffineTransform xform, ImageObserver obs) {
        addDirtyClipArea();
        return original.drawImage(img, xform, obs);
    }

    @Override
    public void drawImage(BufferedImage img, BufferedImageOp op, int x, int y) {
        addDirtyClipArea();
        original.drawImage(img, op, x, y);
    }

    @Override
    public void drawRenderedImage(RenderedImage img, AffineTransform xform) {
        addDirtyClipArea();
        original.drawRenderedImage(img, xform);
    }

    @Override
    public void drawRenderableImage(RenderableImage img, AffineTransform xform) {
        addDirtyClipArea();
        original.drawRenderableImage(img, xform);
    }

    @Override
    public void drawString(String str, float x, float y) {
        addDirtyClipArea();
        original.drawString(str, x, y);
    }

    @Override
    public void drawString(AttributedCharacterIterator iterator, float x, float y) {
        addDirtyClipArea();
        original.drawString(iterator, x, y);
    }

    @Override
    public void drawGlyphVector(GlyphVector g, float x, float y) {
        addDirtyClipArea();
        this.original.drawGlyphVector(g, x, y);
    }

    @Override
    public void fill(Shape s) {
        addDirtyClipArea();
        original.fill(s);
    }

    @Override
    public boolean hit(Rectangle rect, Shape s, boolean onStroke) {
        return original.hit(rect, s, onStroke);
    }

    @Override
    public GraphicsConfiguration getDeviceConfiguration() {
        return original.getDeviceConfiguration();
    }

    @Override
    public void setComposite(Composite comp) {
        original.setComposite(comp);
    }

    @Override
    public void setPaint(Paint paint) {
        original.setPaint(paint);
    }

    @Override
    public void setStroke(Stroke s) {
        original.setStroke(s);
    }

    @Override
    public void setRenderingHint(Key hintKey, Object hintValue) {
        original.setRenderingHint(hintKey, hintValue);
    }

    @Override
    public Object getRenderingHint(Key hintKey) {
        return original.getRenderingHint(hintKey);
    }

    @Override
    public void setRenderingHints(Map<?, ?> hints) {
        original.setRenderingHints(hints);
    }

    @Override
    public void addRenderingHints(Map<?, ?> hints) {
        original.addRenderingHints(hints);
    }

    @Override
    public RenderingHints getRenderingHints() {
        return original.getRenderingHints();
    }

    @Override
    public void translate(double tx, double ty) {
        original.translate(tx, ty);
    }

    @Override
    public void rotate(double theta) {
        addDirtyClipArea();
        original.rotate(theta);
    }

    @Override
    public void rotate(double theta, double x, double y) {
        original.rotate(theta, x, y);
    }

    @Override
    public void scale(double sx, double sy) {
        original.scale(sx, sy);
    }

    @Override
    public void shear(double shx, double shy) {
        original.shear(shx, shy);
    }

    @Override
    public void transform(AffineTransform Tx) {
        original.transform(Tx);
    }

    @Override
    public void setTransform(AffineTransform Tx) {
        original.setTransform(Tx);
    }

    @Override
    public AffineTransform getTransform() {
        return original.getTransform();
    }

    @Override
    public Paint getPaint() {
        return original.getPaint();
    }

    @Override
    public Composite getComposite() {
        return original.getComposite();
    }

    @Override
    public void setBackground(Color color) {
        original.setBackground(color);
    }

    @Override
    public Color getBackground() {
        return original.getBackground();
    }

    @Override
    public Stroke getStroke() {
        return original.getStroke();
    }

    @Override
    public void clip(Shape s) {
        original.clip(s);
    }

    @Override
    public FontRenderContext getFontRenderContext() {
        return original.getFontRenderContext();
    }

}
