// Copyright 2000-2007, FreeHEP
// Modification to original: merged with AbstractVectorGraphicsIO 
package org.webswing.toolkit.directdraw;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.Toolkit;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ImageObserver;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderContext;
import java.awt.image.renderable.RenderableImage;
import java.io.IOException;
import java.text.AttributedCharacterIterator;
import java.util.Map;

import org.webswing.directdraw.util.DirectDrawUtils;

/**
 * This class implements all conversions from integer to double as well as a few
 * other convenience functions. It also handles the different drawSymbol and
 * fillSymbol methods and print colors. The drawing of framed strings is
 * broken down to lower level methods.
 * 
 * @author Simon Fischer
 * @author Mark Donszelmann
 * @author Steffen Greiffenberg
 * @author Charles Loomis
 * @version $Id: AbstractVectorGraphics.java 10516 2007-02-06 21:11:19Z duns $
 */
public abstract class AbstractVectorGraphics extends Graphics2D {

    public static final boolean TEXT_AS_SHAPES = false;
    /**
     * Constant indicating that a string should be aligned vertically with the
     * baseline of the text. This is the default in drawString calls which do
     * not specify an alignment.
     */
    public static final int TEXT_BASELINE = 0;

    /**
     * Constant indicating that a string should be aligned vertically with the
     * top of the text.
     */
    public static final int TEXT_TOP = 1;

    /**
     * Constant indicating that a string should be aligned vertically with the
     * bottom of the text.
     */
    public static final int TEXT_BOTTOM = 3;

    /**
     * Constant indicating that a string should be aligned by the center. This
     * is used for both horizontal and vertical alignment.
     */
    public static final int TEXT_CENTER = 2;

    /**
     * Constant indicating that a string should be aligned horizontally with the
     * left side of the text. This is the default for drawString calls which do
     * not specify an alignment.
     */
    public static final int TEXT_LEFT = 1;

    /**
     * Constant indicating that the string should be aligned horizontally with
     * the right side of the text.
     */
    public static final int TEXT_RIGHT = 3;

    private Dimension size;

    private int colorMode;

    private Color backgroundColor;

    private Stroke currentStroke;

    private Color currentColor;

    private Paint currentPaint;

    private Font currentFont;

    private RenderingHints hints;

    private Composite currentComposite;

    private Rectangle deviceClip;

    private Area userClip; //Untransformed clipping Area defined by the user

    private AffineTransform currentTransform;

    private AffineTransform oldTransform = new AffineTransform(); // only for use in writeSetTransform to calculate the difference.

    /*
     * ================================================================================
     * 1. Constructors & Factory Methods
     * ================================================================================
     */

    /**
     * Constructs a Graphics context with the following graphics state:
     * <UL>
     * <LI>Paint: black
     * <LI>Font: Dailog, Plain, 12pt
     * <LI>Stroke: Linewidth 1.0; No Dashing; Miter Join Style; Miter Limit 10;
     * Square Endcaps;
     * <LI>Transform: Identity
     * <LI>Composite: AlphaComposite.SRC_OVER
     * <LI>Clip: Rectangle(0, 0, size.width, size.height)
     * </UL>
     *
     * @param size rectangle specifying the bounds of the image
     * @param doRestoreOnDispose true if writeGraphicsRestore() should be called
     *        when this graphics context is disposed of.
     */
    protected AbstractVectorGraphics(Dimension size) {
        this.size = size;

        deviceClip = (size != null ? new Rectangle(0, 0, size.width, size.height) : null);
        userClip = null;
        currentTransform = new AffineTransform();
        currentComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER);
        currentStroke = new BasicStroke(1.0f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 10.0f, null, 0.0f);

        currentColor = Color.BLACK;
        currentPaint = Color.BLACK;
        backgroundColor = Color.BLACK;
        currentFont = new Font("Dialog", Font.PLAIN, 12);

        // Initialize the rendering hints.
        hints = new RenderingHints(null);
    }

    /**
     * Constructs a Graphics context with the following graphics state:
     * <UL>
     * <LI>Paint: The color of the component.
     * <LI>Font: The font of the component.
     * <LI>Stroke: Linewidth 1.0; No Dashing; Miter Join Style; Miter Limit 10;
     * Square Endcaps;
     * <LI>Transform: The getDefaultTransform for the GraphicsConfiguration of
     * the component.
     * <LI>Composite: AlphaComposite.SRC_OVER
     * <LI>Clip: The size of the component, Rectangle(0, 0, size.width,
     * size.height)
     * </UL>
     *
     * @param component to be used to initialize the values of the graphics
     *        state
     * @param doRestoreOnDispose true if writeGraphicsRestore() should be called
     *        when this graphics context is disposed of.
     */
    protected AbstractVectorGraphics(Component component) {
        this.size = component.getSize();

        deviceClip = (size != null ? new Rectangle(0, 0, size.width, size.height) : null);
        userClip = null;
        GraphicsConfiguration gc = component.getGraphicsConfiguration();
        currentTransform = (gc != null) ? gc.getDefaultTransform() : new AffineTransform();
        currentComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER);
        currentStroke = new BasicStroke(1.0f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 10.0f, null, 0.0f);

        currentColor = Color.BLACK;
        currentPaint = Color.BLACK;
        backgroundColor = Color.BLACK;
        currentFont = new Font("Dialog", Font.PLAIN, 12);

        // Initialize the rendering hints.
        hints = new RenderingHints(null);
    }

    /**
     * Constructs a subgraphics context.
     *
     * @param graphics context to clone from
     * @param doRestoreOnDispose true if writeGraphicsRestore() should be called
     *        when this graphics context is disposed of.
     */
    protected AbstractVectorGraphics(AbstractVectorGraphics graphics) {
        super();
        backgroundColor = graphics.backgroundColor;
        currentColor = graphics.currentColor;
        currentPaint = graphics.currentPaint;
        colorMode = graphics.colorMode;
        currentFont = graphics.currentFont;

        size = new Dimension(graphics.size);

        deviceClip = new Rectangle(graphics.deviceClip);
        userClip = (graphics.userClip != null) ? new Area(graphics.userClip) : null;
        currentTransform = new AffineTransform(graphics.currentTransform);
        currentComposite = graphics.currentComposite;
        currentStroke = graphics.currentStroke;
        hints = graphics.hints;
    }

    /**
     * Gets the current font.
     * 
     * @return current font
     */
    public Font getFont() {
        return currentFont;
    }

    public void clearRect(int x, int y, int width, int height) {
        Paint temp = getPaint();
        setPaint(getBackground());
        fillRect(x, y, width, height);
        setPaint(temp);
    }

    public void drawLine(int x1, int y1, int x2, int y2) {
        draw(new Line2D.Double(x1, y1, x2, y2));
    }

    public void drawRect(int x, int y, int width, int height) {
        draw(new Rectangle2D.Double(x, y, width, height));
    }

    public void fillRect(int x, int y, int width, int height) {
        fill(new Rectangle2D.Double(x, y, width, height));
    }

    public void drawArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
        draw(new Arc2D.Double(x, y, width, height, startAngle, arcAngle, Arc2D.OPEN));
    }

    public void fillArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
        fill(new Arc2D.Double(x, y, width, height, startAngle, arcAngle, Arc2D.PIE));
    }

    public void drawOval(int x, int y, int width, int height) {
        draw(new Ellipse2D.Double(x, y, width, height));
    }

    public void fillOval(int x, int y, int width, int height) {
        fill(new Ellipse2D.Double(x, y, width, height));
    }

    public void drawRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
        draw(new RoundRectangle2D.Double(x, y, width, height, arcWidth, arcHeight));
    }

    public void fillRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
        fill(new RoundRectangle2D.Double(x, y, width, height, arcWidth, arcHeight));
    }

    public void translate(int x, int y) {
        translate((double) x, (double) y);
    }

    /*--------------------------------------------------------------------------------
     | 8.1. stroke/linewidth
     *--------------------------------------------------------------------------------*/
    public void setLineWidth(int width) {
        setLineWidth((double) width);
    }

    public void setLineWidth(double width) {
        Stroke stroke = getStroke();
        if (stroke instanceof BasicStroke) {
            BasicStroke cs = (BasicStroke) stroke;
            if (cs.getLineWidth() != width) {
                stroke = new BasicStroke((float) width, cs.getEndCap(), cs.getLineJoin(), cs.getMiterLimit(), cs.getDashArray(), cs.getDashPhase());
                setStroke(stroke);
            }
        } else {
            stroke = new BasicStroke((float) width);
            setStroke(stroke);
        }
    }

    public void drawString(String str, int x, int y) {
        drawString(str, (double) x, (double) y);
    }

    public void drawString(String s, float x, float y) {
        drawString(s, (double) x, (double) y);
    }

    public void drawString(AttributedCharacterIterator iterator, int x, int y) {
        drawString(iterator, (float) x, (float) y);
    }

    /**
     * Draws frame and banner for a TextLayout, which is used for
     * calculation auf ajustment
     *
     * @param tl TextLayout for frame calculation
     * @param x coordinate to draw string
     * @param y coordinate to draw string
     * @param horizontal alignment of the text
     * @param vertical alignment of the text
     * @param framed true if text is surrounded by a frame
     * @param frameColor color of the frame
     * @param frameWidth witdh of the frame
     * @param banner true if the frame is filled by a banner
     * @param bannerColor color of the banner
     * @return Offset for the string inside the frame
     */
    private Point2D drawFrameAndBanner(TextLayout tl, double x, double y, int horizontal, int vertical, boolean framed, Color frameColor, double frameWidth, boolean banner, Color bannerColor) {

        // calculate string bounds for alignment
        Rectangle2D bounds = tl.getBounds();

        // calculate real bounds
        bounds.setRect(bounds.getX(), bounds.getY(),
        // care for Italic fonts too
                Math.max(tl.getAdvance(), bounds.getWidth()), bounds.getHeight());

        // add x and y
        AffineTransform at = AffineTransform.getTranslateInstance(x, y);

        // horizontal alignment
        if (horizontal == TEXT_RIGHT) {
            at.translate(-bounds.getWidth(), 0);
        } else if (horizontal == TEXT_CENTER) {
            at.translate(-bounds.getWidth() / 2, 0);
        }

        // vertical alignment
        if (vertical == TEXT_BASELINE) {
            // no translation needed
        } else if (vertical == TEXT_TOP) {
            at.translate(0, -bounds.getY());
        } else if (vertical == TEXT_CENTER) {
            // the following adds supersript ascent too,
            // so it does not work
            // at.translate(0, tl.getAscent() / 2);
            // this is nearly the same
            at.translate(0, tl.getDescent());
        } else if (vertical == TEXT_BOTTOM) {
            at.translate(0, -bounds.getHeight() - bounds.getY());
        }

        // transform the bounds
        bounds = at.createTransformedShape(bounds).getBounds2D();
        // create the result with the same transformation
        Point2D result = at.transform(new Point2D.Double(0, 0), new Point2D.Double());

        // space between string and border
        double adjustment = (getFont().getSize2D() * 2) / 10;

        // add the adjustment
        bounds.setRect(bounds.getX() - adjustment, bounds.getY() - adjustment, bounds.getWidth() + 2 * adjustment, bounds.getHeight() + 2 * adjustment);

        if (banner) {
            Paint paint = getPaint();
            setColor(bannerColor);
            fill(bounds);
            setPaint(paint);
        }
        if (framed) {
            Paint paint = getPaint();
            Stroke stroke = getStroke();
            setColor(frameColor);
            setLineWidth(frameWidth);
            draw(bounds);
            setPaint(paint);
            setStroke(stroke);
        }

        return result;
    }

    /**
     * Draws frame, banner and aligned text inside
     *
     * @param str text to be drawn
     * @param x coordinate to draw string
     * @param y coordinate to draw string
     * @param horizontal alignment of the text
     * @param vertical alignment of the text
     * @param framed true if text is surrounded by a frame
     * @param frameColor color of the frame
     * @param frameWidth witdh of the frame
     * @param banner true if the frame is filled by a banner
     * @param bannerColor color of the banner
     */
    @SuppressWarnings("unchecked")
    public void drawString(String str, double x, double y, int horizontal, int vertical, boolean framed, Color frameColor, double frameWidth, boolean banner, Color bannerColor) {

        // change the x offset for the next drawing
        // change y offset for vertical text
        TextLayout tl = new TextLayout(str, DirectDrawUtils.getAttributes(getFont()), getFontRenderContext());

        // draw the frame
        Point2D offset = drawFrameAndBanner(tl, x, y, horizontal, vertical, framed, frameColor, frameWidth, banner, bannerColor);

        // draw the string
        drawString(str, offset.getX(), offset.getY());
    }

    // ------------------ other wrapper methods ----------------

    public void drawString(String str, double x, double y, int horizontal, int vertical) {
        drawString(str, x, y, horizontal, vertical, false, null, 0, false, null);
    }

    /* 8.2. paint/color */
    public int getColorMode() {
        return colorMode;
    }

    public void setColorMode(int colorMode) {
        this.colorMode = colorMode;
    }

    /**
     * Gets the background color.
     * 
     * @return background color
     */
    public Color getBackground() {
        return backgroundColor;
    }

    /**
     * Sets the background color.
     * 
     * @param color background color to be set
     */
    public void setBackground(Color color) {
        backgroundColor = color;
    }

    /**
     * Gets the current color.
     * 
     * @return the current color
     */
    public Color getColor() {
        return currentColor;
    }

    /**
     * Gets the current paint.
     * 
     * @return paint current paint
     */
    public Paint getPaint() {
        return currentPaint;
    }

    public void rotate(double theta, double x, double y) {
        translate(x, y);
        rotate(theta);
        translate(-x, -y);
    }

    public void drawPolyline(int[] xPoints, int[] yPoints, int nPoints) {
        draw(createShape(xPoints, yPoints, nPoints, false, true));
    }

    public void drawPolygon(int[] xPoints, int[] yPoints, int nPoints) {
        draw(createShape(xPoints, yPoints, nPoints, true, true));
    }

    public void fillPolygon(int[] xPoints, int[] yPoints, int nPoints) {
        fill(createShape(xPoints, yPoints, nPoints, true, false));
    }

    /**
     * Checks whether or not the specified <code>Shape</code> intersects
     * the specified {@link Rectangle}, which is in device
     * space.
     *
     * @param rect the area in device space to check for a hit
     * @param s the <code>Shape</code> to check for a hit
     * @param onStroke flag used to choose between testing the stroked or the filled shape.
     * @see java.awt.Graphics2D#hit(Rectangle, Shape, boolean)
     */
    public boolean hit(Rectangle rect, Shape s, boolean onStroke) {
        if (onStroke && getStroke() != null) {
            s = getStroke().createStrokedShape(s);
        }

        if (getTransform() != null) {
            s = getTransform().createTransformedShape(s);
        }

        Area area = new Area(s);
        if (getClip() != null) {
            area.intersect(new Area(getClip()));
        }

        return area.intersects(rect);
    }

    /*
     * ================================================================================ |
     * XXX 5. Drawing Methods
     * ================================================================================
     */

    /* 5.3. Images */
    public boolean drawImage(Image image, int x, int y, ImageObserver observer) {
        int imageWidth = image.getWidth(observer);
        int imageHeight = image.getHeight(observer);
        return drawImage(image, x, y, x + imageWidth, y + imageHeight, 0, 0, imageWidth, imageHeight, null, observer);
    }

    public boolean drawImage(Image image, int x, int y, int width, int height, ImageObserver observer) {
        int imageWidth = image.getWidth(observer);
        int imageHeight = image.getHeight(observer);
        return drawImage(image, x, y, x + width, y + height, 0, 0, imageWidth, imageHeight, null, observer);
    }

    public boolean drawImage(Image image, int x, int y, int width, int height, Color bgColor, ImageObserver observer) {
        int imageWidth = image.getWidth(observer);
        int imageHeight = image.getHeight(observer);
        return drawImage(image, x, y, x + width, y + height, 0, 0, imageWidth, imageHeight, bgColor, observer);
    }

    public boolean drawImage(Image image, int x, int y, Color bgColor, ImageObserver observer) {
        int imageWidth = image.getWidth(observer);
        int imageHeight = image.getHeight(observer);
        return drawImage(image, x, y, x + imageWidth, y + imageHeight, 0, 0, imageWidth, imageHeight, bgColor, observer);
    }

    public boolean drawImage(Image image, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2, ImageObserver observer) {
        return drawImage(image, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, null, observer);
    }

    public void drawImage(BufferedImage img, BufferedImageOp op, int x, int y) {
        drawImage(op.filter(img, null), x, y, null);
    }

    /**
     * Draw and resizes (transparent) image. Calls writeImage(...).
     *
     * @param image image to be drawn
     * @param dx1 destination image bounds
     * @param dy1 destination image bounds
     * @param dx2 destination image bounds
     * @param dy2 destination image bounds
     * @param sx1 source image bounds
     * @param sy1 source image bounds
     * @param sx2 source image bounds
     * @param sy2 source image bounds
     * @param bgColor background color
     * @param observer for updates if image still incomplete
     * @return true if successful
     */
    public boolean drawImage(Image image, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2, Color bgColor, ImageObserver observer) {
        try {
            int srcX = Math.min(sx1, sx2);
            int srcY = Math.min(sy1, sy2);
            int srcWidth = Math.abs(sx2 - sx1);
            int srcHeight = Math.abs(sy2 - sy1);
            int width = Math.abs(dx2 - dx1);
            int height = Math.abs(dy2 - dy1);

            Rectangle2D.Float crop = null;
            if ((srcX != 0) || (srcY != 0) || (srcWidth != image.getWidth(observer)) || (srcHeight != image.getHeight(observer))) {
                crop = new Rectangle2D.Float(srcX, srcY, srcWidth, srcHeight);
            }

            boolean flipHorizontal = (dx2 < dx1) ^ (sx2 < sx1); // src flipped
                                                                // and not dest
                                                                // flipped or
                                                                // vice versa
            boolean flipVertical = (dy2 < dy1) ^ (sy2 < sy1); // <=> source
                                                              // flipped XOR
                                                              // dest flipped

            double tx = (flipHorizontal) ? (double) dx2 : (double) dx1;
            double ty = (flipVertical) ? (double) dy2 : (double) dy1;

            double sx = (double) width / srcWidth;
            sx = flipHorizontal ? -1 * sx : sx;
            double sy = (double) height / srcHeight;
            sy = flipVertical ? -1 * sy : sy;

            writeImage(image, observer, new AffineTransform(sx, 0, 0, sy, tx, ty), crop, bgColor);
            return true;
        } catch (IOException e) {
            handleException(e);
            return false;
        }
    }

    public boolean drawImage(Image image, AffineTransform xform, ImageObserver observer) {
        try {
            writeImage(image, observer, xform, null, null);
        } catch (Exception e) {
            handleException(e);
        }
        return true;
    }

    public void drawRenderableImage(RenderableImage image, AffineTransform xform) {
        drawRenderedImage(image.createRendering(new RenderContext(new AffineTransform(), getRenderingHints())), xform);
    }

    /**
     * Draws a rendered image using a transform.
     *
     * @param image to be drawn
     * @param xform transform to be used on the image
     */
    public void drawRenderedImage(RenderedImage image, AffineTransform xform) {
        try {
            writeImage(image, xform);
        } catch (Exception e) {
            handleException(e);
        }
    }

    protected abstract void writeImage(RenderedImage image, AffineTransform xform) throws IOException;

    protected abstract void writeImage(Image image, ImageObserver observer, AffineTransform xform, Rectangle2D.Float crop, Color bkg) throws IOException;

    /**
     * Draws the string at (x, y). If TEXT_AS_SHAPES is set
     * {@link #drawGlyphVector(java.awt.font.GlyphVector, float, float)} is used, otherwise
     * {@link #writeString(String, double, double)} for a more direct output of the string.
     *
     * @param string
     * @param x
     * @param y
     */
    public void drawString(String string, double x, double y) {
        // something to draw?
        if (string == null || string.trim().equals("")) {
            return;
        }
        try {
            writeString(string, x, y);
        } catch (IOException e) {
            handleException(e);
        }
    }

    protected abstract void writeString(String string, double x, double y) throws IOException;

    /**
     * Use the transformation of the glyphvector and draw it
     *
     * @param g
     * @param x
     * @param y
     */
    public void drawGlyphVector(GlyphVector g, float x, float y) {
        fill(g.getOutline(x, y));
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void drawString(AttributedCharacterIterator iterator, float x, float y) {

        // reset to that font at the end
        Font font = getFont();

        // initial attributes, we us TextAttribute.equals() rather
        // than Font.equals() because using Font.equals() we do
        // not get a 'false' if underline etc. is changed
        Map/*<TextAttribute, ?>*/attributes = DirectDrawUtils.getAttributes(font);

        // stores all characters which are written with the same font
        // if font is changed the buffer will be written and cleared
        // after it
        StringBuffer sb = new StringBuffer();

        for (char c = iterator.first(); c != AttributedCharacterIterator.DONE; c = iterator.next()) {

            // append c if font is not changed
            if (attributes.equals(iterator.getAttributes())) {
                sb.append(c);

            } else {
                // TextLayout does not like 0 length strings
                if (sb.length() > 0) {
                    // draw sb if font is changed
                    drawString(sb.toString(), x, y);

                    // change the x offset for the next drawing
                    TextLayout tl = new TextLayout(sb.toString(), attributes, getFontRenderContext());

                    // calculate real width
                    x = x + Math.max(tl.getAdvance(), (float) tl.getBounds().getWidth());
                }

                // empty sb
                sb = new StringBuffer();
                sb.append(c);

                // change the font
                attributes = iterator.getAttributes();
                setFont(new Font(attributes));
            }
        }

        // draw the rest
        if (sb.length() > 0) {
            drawString(sb.toString(), x, y);
        }

        // use the old font for the next string drawing
        setFont(font);
    }

    /*
     * ================================================================================ |
     * XXX 6. Transformations
     * ================================================================================
     */
    /**
     * Get the current transform.
     *
     * @return current transform
     */
    public AffineTransform getTransform() {
        return new AffineTransform(currentTransform);
    }

    /**
     * Set the current transform. Calls writeSetTransform(Transform).
     *
     * @param transform to be set
     */
    public void setTransform(AffineTransform transform) {
        // Fix for FREEHEP-569
        oldTransform.setTransform(currentTransform);
        currentTransform.setTransform(transform);
        try {
            writeSetTransform(transform);
        } catch (IOException e) {
            handleException(e);
        }
    }

    /**
     * Transforms the current transform. Calls writeTransform(Transform)
     *
     * @param transform to be applied
     */
    public void transform(AffineTransform transform) {
        currentTransform.concatenate(transform);
        try {
            writeTransform(transform);
        } catch (IOException e) {
            handleException(e);
        }
    }

    /**
     * Translates the current transform. Calls writeTransform(Transform)
     *
     * @param x amount by which to translate
     * @param y amount by which to translate
     */
    public void translate(double x, double y) {
        currentTransform.translate(x, y);
        try {
            writeTransform(new AffineTransform(1, 0, 0, 1, x, y));
        } catch (IOException e) {
            handleException(e);
        }
    }

    /**
     * Rotate the current transform over the Z-axis. Calls
     * writeTransform(Transform). Rotating with a positive angle theta rotates
     * points on the positive x axis toward the positive y axis.
     *
     * @param theta radians over which to rotate
     */
    public void rotate(double theta) {
        currentTransform.rotate(theta);
        try {
            writeTransform(new AffineTransform(Math.cos(theta), Math.sin(theta), -Math.sin(theta), Math.cos(theta), 0, 0));
        } catch (IOException e) {
            handleException(e);
        }
    }

    /**
     * Scales the current transform. Calls writeTransform(Transform).
     *
     * @param sx amount used for scaling
     * @param sy amount used for scaling
     */
    public void scale(double sx, double sy) {
        currentTransform.scale(sx, sy);
        try {
            writeTransform(new AffineTransform(sx, 0, 0, sy, 0, 0));
        } catch (IOException e) {
            handleException(e);
        }
    }

    /**
     * Shears the current transform. Calls writeTransform(Transform).
     *
     * @param shx amount for shearing
     * @param shy amount for shearing
     */
    public void shear(double shx, double shy) {
        currentTransform.shear(shx, shy);
        try {
            writeTransform(new AffineTransform(1, shy, shx, 1, 0, 0));
        } catch (IOException e) {
            handleException(e);
        }
    }

    /**
     * Writes out the transform as it needs to be concatenated to the internal
     * transform of the output format. If there is no implementation of an
     * internal transform, then this method needs to do nothing, BUT all
     * coordinates need to be transformed by the currentTransform before being
     * written out.
     *
     * @param transform to be written
     */
    protected abstract void writeTransform(AffineTransform transform) throws IOException;

    /**
     * Clears any existing transformation and sets the a new one.
     * The default implementation calls writeTransform using the
     * inverted affine transform to calculate it.
    s     *
     * @param transform to be written
     */
    protected void writeSetTransform(AffineTransform transform) throws IOException {
        try {
            AffineTransform deltaTransform = new AffineTransform(transform);
            deltaTransform.concatenate(oldTransform.createInverse());
            writeTransform(deltaTransform);
        } catch (NoninvertibleTransformException e) {
            handleException(e);
        }
    }

    /*
     * ================================================================================ |
     * XXX 7. Clipping
     * ================================================================================
     */

    /**
     * Gets the current clip in form of a Shape (Rectangle).
     *
     * @return current clip
     */
    public Shape getClip() {
        return (userClip != null) ? new Area(untransformShape(userClip)) : null;
    }

    /**
     * Gets the current clip in form of a Rectangle.
     *
     * @return current clip
     */
    public Rectangle getClipBounds() {
        Shape clip = getClip();
        return (clip != null) ? getClip().getBounds() : null;
    }

    /**
     * Gets the current clip in form of a Rectangle.
     *
     * @return current clip
     */
    public Rectangle getClipBounds(Rectangle r) {
        Rectangle bounds = getClipBounds();
        if (bounds != null)
            r.setBounds(bounds);
        return r;
    }

    /**
     * Clips rectangle. Calls clip(Rectangle).
     *
     * @param x rectangle for clipping
     * @param y rectangle for clipping
     * @param width rectangle for clipping
     * @param height rectangle for clipping
     */
    public void clipRect(int x, int y, int width, int height) {
        clip(new Rectangle(x, y, width, height));
    }

    /**
     * Clips rectangle. Calls clip(Rectangle).
     *
     * @param x rectangle for clipping
     * @param y rectangle for clipping
     * @param width rectangle for clipping
     * @param height rectangle for clipping
     */
    public void setClip(int x, int y, int width, int height) {
        setClip(new Rectangle(x, y, width, height));
    }

    /**
     * Clips shape. Clears userClip and calls clip(Shape).
     *
     * @param s used for clipping
     */
    public void setClip(Shape s) {

        Shape ts = transformShape(s);
        userClip = (ts != null) ? new Area(ts) : null;

    }

    /**
     * Clips using given shape. Dispatches to writeClip(Rectangle),
     * writeClip(Rectangle2D) or writeClip(Shape).
     *
     * @param s used for clipping
     */
    public void clip(Shape s) {
        Shape ts = transformShape(s);
        if (userClip != null) {
            if (ts != null) {
                userClip.intersect(new Area(ts));
            } else {
                userClip = null;
            }
        } else {
            userClip = (ts != null) ? new Area(ts) : null;
        }
    }

    /*
     * ================================================================================ |
     * XXX 8. Graphics State
     * ================================================================================
     */

    /* 8.1. stroke/linewidth */
    /**
     * Get the current stroke.
     *
     * @return current stroke
     */
    public Stroke getStroke() {
        return currentStroke;
    }

    /**
     * Sets the current stroke. Calls writeStroke if stroke is unequal to the
     * current stroke.
     *
     * @param stroke to be set
     */
    public void setStroke(Stroke stroke) {
        if (stroke.equals(currentStroke)) {
            return;
        }
        try {
            writeStroke(stroke);
        } catch (IOException e) {
            handleException(e);
        }
        currentStroke = stroke;
    }

    public abstract void writeStroke(Stroke stroke) throws IOException;;

    /* 8.2 Paint */

    /**
     * Sets the current color and the current paint. Calls writePaint(Color).
     * 
     * @param color to be set
     */
    public void setColor(Color color) {
        if (color == null)
            return;

        if (color.equals(getPaint()))
            return;

        try {
            currentColor = color;
            currentPaint = color;
            writePaint(color);
        } catch (IOException e) {
            handleException(e);
        }
    }

    /**
     * Sets the current paint.  In the case paint is a Color the current color is also
     * changed.
     *
     * @param paint to be set
     */
    public void setPaint(Paint paint) {
        if (paint == null)
            return;

        if (paint.equals(getPaint()))
            return;

        try {
            if (paint instanceof Color) {
                setColor((Color) paint);
            } else {
                currentPaint = paint;
                writePaint(paint);
            }
        } catch (IOException e) {
            handleException(e);
        }
    }

    /**
     * Writes out paint.
     *
     * @param paint to be written
     */
    protected abstract void writePaint(Paint paint) throws IOException;

    /* 8.3. font */
    /**
     * Gets the current font render context. This returns an standard
     * FontRenderContext with anti-aliasing and uses
     * fractional metrics.
     *
     * @return current font render context
     */
    public FontRenderContext getFontRenderContext() {
        // NOTE: not sure?
        // Fixed for VG-285
        return new FontRenderContext(new AffineTransform(1, 0, 0, 1, 0, 0), true, true);
    }

    /**
     * Gets the fontmetrics.
     *
     * @deprecated
     * @param font to be used for retrieving fontmetrics
     * @return fontmetrics for given font
     */
    public FontMetrics getFontMetrics(Font font) {
        return Toolkit.getDefaultToolkit().getFontMetrics(font);
    }

    /* 8.4. rendering hints */
    /**
     * Gets a copy of the rendering hints.
     *
     * @return clone of table of rendering hints.
     */
    public RenderingHints getRenderingHints() {
        return (RenderingHints) hints.clone();
    }

    /**
     * Adds to table of rendering hints.
     *
     * @param hints table to be added
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void addRenderingHints(Map hints) {
        hints.putAll(hints);
    }

    /**
     * Sets table of rendering hints.
     *
     * @param hints table to be set
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void setRenderingHints(Map hints) {
        hints.clear();
        hints.putAll(hints);
    }

    /**
     * Gets a given rendering hint.
     *
     * @param key hint key
     * @return hint associated to key
     */
    public Object getRenderingHint(RenderingHints.Key key) {
        return hints.get(key);
    }

    /**
     * Sets a given rendering hint.
     *
     * @param key hint key
     * @param hint to be associated with key
     */
    public void setRenderingHint(RenderingHints.Key key, Object hint) {
        // extra protection, failed on under MacOS X 10.2.6, jdk 1.4.1_01-39/14
        if ((key == null) || (hint == null))
            return;
        hints.put(key, hint);
    }

    /**
     * Sets the current font.
     * 
     * @param font to be set
     */
    public void setFont(Font font) {
        if (font == null)
            return;

        currentFont = font;
    }

    /*
     * ================================================================================ |
     * XXX 9. AUXILIARY
     * ================================================================================
     */

    /**
     * Handles an exception which has been caught. Dispatches exception to
     * writeWarning for UnsupportedOperationExceptions and writeError for others
     *
     * @param exception to be handled
     */
    protected void handleException(Exception e) {
        e.printStackTrace();
    }

    /**
     * Gets current composite.
     *
     * @return current composite
     */
    public Composite getComposite() {
        return currentComposite;
    }

    /**
     * Sets current composite.
     *
     * @param composite to be set
     */
    public void setComposite(Composite composite) {
        currentComposite = composite;
    }

    /**
     * Creates a polyline/polygon shape from a set of points. Needs to be
     * defined in subclass because its implementations could be device specific
     * 
     * @param xPoints X coordinates of the polyline.
     * @param yPoints Y coordinates of the polyline.
     * @param nPoints number of points of the polyline.
     * @param close is shape closed
     */
    protected Shape createShape(double[] xPoints, double[] yPoints, int nPoints, boolean close) {
        GeneralPath path = new GeneralPath(GeneralPath.WIND_EVEN_ODD);
        if (nPoints > 0) {
            path.moveTo((float) xPoints[0], (float) yPoints[0]);
            for (int i = 1; i < nPoints; i++) {
                path.lineTo((float) xPoints[i], (float) yPoints[i]);
            }
            if (close)
                path.closePath();
        }
        return path;
    }

    /**
     * Creates a polyline/polygon shape from a set of points.
     * Needs a bias!
     * 
     * @param xPoints X coordinates of the polyline.
     * @param yPoints Y coordinates of the polyline.
     * @param nPoints number of points of the polyline.
     * @param close is shape closed
     */
    protected Shape createShape(int[] xPoints, int[] yPoints, int nPoints, boolean close, boolean biased) {

        float offset = biased ? (float) 0.5 : 0.0f;
        GeneralPath path = new GeneralPath(GeneralPath.WIND_EVEN_ODD);
        if (nPoints > 0) {
            path.moveTo(xPoints[0] + offset, yPoints[0] + offset);
            int lastX = xPoints[0];
            int lastY = yPoints[0];
            if (close && (Math.abs(xPoints[nPoints - 1] - lastX) < 1) && (Math.abs(yPoints[nPoints - 1] - lastY) < 1)) {
                nPoints--;
            }
            for (int i = 1; i < nPoints; i++) {
                if ((Math.abs(xPoints[i] - lastX) > 1) || (Math.abs(yPoints[i] - lastY) > 1)) {
                    path.lineTo(xPoints[i] + offset, yPoints[i] + offset);
                    lastX = xPoints[i];
                    lastY = yPoints[i];
                }
            }
            if (close)
                path.closePath();
        }
        return path;
    }

    protected Shape transformShape(AffineTransform at, Shape s) {
        if (s == null)
            return null;
        return at.createTransformedShape(s);
    }

    protected Shape transformShape(Shape s) {
        return transformShape(getTransform(), s);
    }

    protected Shape untransformShape(Shape s) {
        if (s == null)
            return null;
        try {
            return transformShape(getTransform().createInverse(), s);
        } catch (NoninvertibleTransformException e) {
            return null;
        }
    }

    /**
     * Draws an overline for the text at (x, y). The method is usesefull for
     * drivers that do not support overlines by itself.
     *
     * @param text text for width calulation
     * @param font font for width calulation
     * @param x position of text
     * @param y position of text
     */
    protected void overLine(String text, Font font, float x, float y) {
        TextLayout layout = new TextLayout(text, font, getFontRenderContext());
        float width = Math.max(layout.getAdvance(), (float) layout.getBounds().getWidth());

        GeneralPath path = new GeneralPath();
        path.moveTo(x, y + (float) layout.getBounds().getY() - layout.getAscent());
        path.lineTo(x + width, y + (float) layout.getBounds().getY() - layout.getAscent() - layout.getAscent());
        draw(path);
    }

}
