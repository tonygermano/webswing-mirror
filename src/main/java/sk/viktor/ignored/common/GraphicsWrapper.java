package sk.viktor.ignored.common;

import java.awt.Color;
import java.awt.Component;
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
import java.awt.Window;
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

import javax.swing.JComponent;
import javax.swing.RepaintManager;

import sk.viktor.ignored.model.s2c.JsonWindowInfo;
import sk.viktor.util.Util;

public class GraphicsWrapper extends Graphics2D {

    private WebWindow webWindow;

    private JComponent rootPaintComponent;

    private Graphics2D original;
    private Graphics2D web;

    public GraphicsWrapper(Graphics2D g, WebWindow root) {
        this.webWindow = root;
        this.original = g;
        initWebGraphics();
    }

    public GraphicsWrapper(Graphics2D original, Graphics2D web, WebWindow root) {
        //create copy
        this.webWindow = root;
        this.web = web;
        this.original = original;
    }

    public BufferedImage getImg() {
        return webWindow.getVirtualScreen();
    }

    public WebWindow getWebWindow() {
        return webWindow;
    }

    public JComponent getRootPaintComponent() {
        return rootPaintComponent;
    }

    public void setRootPaintComponent(JComponent rootPaintComponent) {
        this.rootPaintComponent = rootPaintComponent;
    }


    public Graphics getOriginal() {
        return original;
    }

    public void initWebGraphics() {
        Rectangle originalBounds = original.getClipBounds();
        web = ((Graphics2D) webWindow.getWebGraphics());
        web.setClip(originalBounds);
        //copy properties from original
        web.setBackground(original.getBackground());
        web.setClip(original.getClip());
        web.setColor(original.getColor());
        web.setComposite(original.getComposite());
        web.setFont(original.getFont());
        web.setPaint(original.getPaint());
        web.setStroke(original.getStroke());
        web.setTransform(original.getTransform());
        web.setRenderingHints(original.getRenderingHints());
    }

    @Override
    public Graphics create() {
        GraphicsWrapper copy = new GraphicsWrapper((Graphics2D) original.create(), (Graphics2D) web.create(), webWindow);
        copy.setRootPaintComponent(rootPaintComponent);
        return copy;
    }

    @Override
    public void translate(int x, int y) {
        original.translate(x, y);
        web.translate(x, y);
    }

    @Override
    public Color getColor() {
        return original.getColor();
    }

    @Override
    public void setColor(Color c) {
        original.setColor(c);
        web.setColor(c);
    }

    @Override
    public void setPaintMode() {
        original.setPaintMode();
        web.setPaintMode();
    }

    @Override
    public void setXORMode(Color c1) {
        original.setXORMode(c1);
        web.setXORMode(c1);
    }

    @Override
    public Font getFont() {
        return original.getFont();
    }

    @Override
    public void setFont(Font font) {
        original.setFont(font);
        web.setFont(font);
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
        web.clipRect(x, y, width, height);
    }

    @Override
    public void setClip(int x, int y, int width, int height) {
        original.setClip(x, y, width, height);
        web.setClip(x, y, width, height);
    }

    @Override
    public Shape getClip() {
        return original.getClip();
    }

    @Override
    public void setClip(Shape clip) {
        original.setClip(clip);
        web.setClip(clip);
    }

    @Override
    public void copyArea(int x, int y, int width, int height, int dx, int dy) {
        original.copyArea(x, y, width, height, dx, dy);
        //repaint area
        RepaintManager.currentManager((Component)webWindow).addDirtyRegion((Window) webWindow,  (int)getTransform().getTranslateX()+x+dx, (int)getTransform().getTranslateY()+y+dy, width, height);
    }

    @Override
    public void drawLine(int x1, int y1, int x2, int y2) {
        original.drawLine(x1, y1, x2, y2);
        web.drawLine(x1, y1, x2, y2);
    }

    @Override
    public void fillRect(int x, int y, int width, int height) {
        original.fillRect(x, y, width, height);
        web.fillRect(x, y, width, height);
    }

    @Override
    public void clearRect(int x, int y, int width, int height) {
        original.clearRect(x, y, width, height);
        web.clearRect(x, y, width, height);
    }

    @Override
    public void drawRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
        original.drawRoundRect(x, y, width, height, arcWidth, arcHeight);
        web.drawRoundRect(x, y, width, height, arcWidth, arcHeight);
    }

    @Override
    public void fillRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
        original.fillRoundRect(x, y, width, height, arcWidth, arcHeight);
        web.fillRoundRect(x, y, width, height, arcWidth, arcHeight);
    }

    @Override
    public void drawOval(int x, int y, int width, int height) {
        original.drawOval(x, y, width, height);
        web.drawOval(x, y, width, height);
    }

    @Override
    public void fillOval(int x, int y, int width, int height) {
        original.fillOval(x, y, width, height);
        web.fillOval(x, y, width, height);
    }

    @Override
    public void drawArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
        original.drawArc(x, y, width, height, startAngle, arcAngle);
        web.drawArc(x, y, width, height, startAngle, arcAngle);
    }

    @Override
    public void fillArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
        original.fillArc(x, y, width, height, startAngle, arcAngle);
        web.fillArc(x, y, width, height, startAngle, arcAngle);
    }

    @Override
    public void drawPolyline(int[] xPoints, int[] yPoints, int nPoints) {
        original.drawPolyline(xPoints, yPoints, nPoints);
        web.drawPolyline(xPoints, yPoints, nPoints);
    }

    @Override
    public void drawPolygon(int[] xPoints, int[] yPoints, int nPoints) {
        original.drawPolygon(xPoints, yPoints, nPoints);
        web.drawPolygon(xPoints, yPoints, nPoints);
    }

    @Override
    public void fillPolygon(int[] xPoints, int[] yPoints, int nPoints) {
        original.fillPolygon(xPoints, yPoints, nPoints);
        web.fillPolygon(xPoints, yPoints, nPoints);
    }

    @Override
    public void drawString(String str, int x, int y) {
        original.drawString(str, x, y);
        web.drawString(str, x, y);
    }

    @Override
    public void drawString(AttributedCharacterIterator iterator, int x, int y) {
        original.drawString(iterator, x, y);
        web.drawString(iterator, x, y);
    }

    @Override
    public boolean drawImage(Image img, int x, int y, ImageObserver observer) {
        web.drawImage(img, x, y, observer);
        if (Util.isPaintDoubleBufferedPainting() || Util.isForceDoubleBufferedPainting()) {
            PaintManager.getInstance(webWindow.getClientId()).doSendPaintRequest(this, (JComponent) observer);
        }
        return original.drawImage(img, x, y, observer);
    }

    @Override
    public boolean drawImage(Image img, int x, int y, int width, int height, ImageObserver observer) {
        web.drawImage(img, x, y, width, height, observer);
        return original.drawImage(img, x, y, width, height, observer);
    }

    @Override
    public boolean drawImage(Image img, int x, int y, Color bgcolor, ImageObserver observer) {
        web.drawImage(img, x, y, bgcolor, observer);
        return original.drawImage(img, x, y, bgcolor, observer);
    }

    @Override
    public boolean drawImage(Image img, int x, int y, int width, int height, Color bgcolor, ImageObserver observer) {
        web.drawImage(img, x, y, width, height, bgcolor, observer);
        return original.drawImage(img, x, y, width, height, bgcolor, observer);
    }

    @Override
    public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2, ImageObserver observer) {
        web.drawImage(img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, observer);
        return original.drawImage(img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, observer);
    }

    @Override
    public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2, Color bgcolor, ImageObserver observer) {
        web.drawImage(img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, bgcolor, observer);
        return original.drawImage(img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, bgcolor, observer);
    }

    @Override
    public void dispose() {
        web.dispose();
        original.dispose();
    }

    @Override
    public void draw(Shape s) {
        original.draw(s);
        web.draw(s);
    }

    @Override
    public boolean drawImage(Image img, AffineTransform xform, ImageObserver obs) {
        web.drawImage(img, xform, obs);
        return original.drawImage(img, xform, obs);
    }

    @Override
    public void drawImage(BufferedImage img, BufferedImageOp op, int x, int y) {
        web.drawImage(img, op, x, y);
        original.drawImage(img, op, x, y);
    }

    @Override
    public void drawRenderedImage(RenderedImage img, AffineTransform xform) {
        web.drawRenderedImage(img, xform);
        original.drawRenderedImage(img, xform);
    }

    @Override
    public void drawRenderableImage(RenderableImage img, AffineTransform xform) {
        web.drawRenderableImage(img, xform);
        original.drawRenderableImage(img, xform);
    }

    @Override
    public void drawString(String str, float x, float y) {
        web.drawString(str, x, y);
        original.drawString(str, x, y);
    }

    @Override
    public void drawString(AttributedCharacterIterator iterator, float x, float y) {
        web.drawString(iterator, x, y);
        original.drawString(iterator, x, y);
    }

    @Override
    public void drawGlyphVector(GlyphVector g, float x, float y) {
        this.web.drawGlyphVector(g, x, y);
        this.original.drawGlyphVector(g, x, y);
    }

    @Override
    public void fill(Shape s) {
        web.fill(s);
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
        web.setComposite(comp);
        original.setComposite(comp);
    }

    @Override
    public void setPaint(Paint paint) {
        web.setPaint(paint);
        original.setPaint(paint);
    }

    @Override
    public void setStroke(Stroke s) {
        web.setStroke(s);
        original.setStroke(s);
    }

    @Override
    public void setRenderingHint(Key hintKey, Object hintValue) {
        web.setRenderingHint(hintKey, hintValue);
        original.setRenderingHint(hintKey, hintValue);
    }

    @Override
    public Object getRenderingHint(Key hintKey) {
        return original.getRenderingHint(hintKey);
    }

    @Override
    public void setRenderingHints(Map<?, ?> hints) {
        web.setRenderingHints(hints);
        original.setRenderingHints(hints);
    }

    @Override
    public void addRenderingHints(Map<?, ?> hints) {
        web.addRenderingHints(hints);
        original.addRenderingHints(hints);
    }

    @Override
    public RenderingHints getRenderingHints() {
        return original.getRenderingHints();
    }

    @Override
    public void translate(double tx, double ty) {
        web.translate(tx, ty);
        original.translate(tx, ty);
    }

    @Override
    public void rotate(double theta) {
        web.rotate(theta);
        original.rotate(theta);
    }

    @Override
    public void rotate(double theta, double x, double y) {
        web.rotate(theta, x, y);
        original.rotate(theta, x, y);
    }

    @Override
    public void scale(double sx, double sy) {
        web.scale(sx, sy);
        original.scale(sx, sy);
    }

    @Override
    public void shear(double shx, double shy) {
        web.shear(shx, shy);
        original.shear(shx, shy);
    }

    @Override
    public void transform(AffineTransform Tx) {
        web.transform(Tx);
        original.transform(Tx);
    }

    @Override
    public void setTransform(AffineTransform Tx) {
        web.setTransform(Tx);
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
        web.setBackground(color);
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
        web.clip(s);
        original.clip(s);
    }

    @Override
    public FontRenderContext getFontRenderContext() {
        return original.getFontRenderContext();
    }

    public JsonWindowInfo getWindowInfo() {
        return webWindow.getWindowInfo();
    }

}
