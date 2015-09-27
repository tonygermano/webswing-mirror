package org.webswing.directdraw.toolkit;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
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
		this.dif = thisImage.getContext().getInstructionFactory();
		this.id = this.thisImage.getNextGraphicsId();
	}

	public WebGraphics(WebGraphics g) {
		super(g);
		this.thisImage = g.thisImage;
		this.dif = thisImage.getContext().getInstructionFactory();
		this.id = this.thisImage.getNextGraphicsId();
	}

	@Override
	public void draw(Shape s) {
		if (getStroke() instanceof BasicStroke) {
			thisImage.addInstruction(this, dif.draw(s, getClip()));
		} else {
			fill(getStroke().createStrokedShape(s));
		}
	}

	@Override
	public void fill(Shape s) {
		thisImage.addFillInstruction(this, s);
	}

	@Override
	protected void writeImage(RenderedImage image, AffineTransform xform) throws IOException {
		thisImage.addImage(this, image, null, xform, null);
	}

	@Override
	protected void writeImage(Image image, ImageObserver observer, AffineTransform xform, Rectangle2D.Float crop, Color bkg) throws IOException {
		if (image instanceof WebImage || image instanceof VolatileWebImageWrapper) {
			crop = crop != null ? crop : new Rectangle2D.Float(0, 0, image.getWidth(observer), image.getHeight(observer));
			WebImage wi = image instanceof WebImage ? (WebImage) image : ((VolatileWebImageWrapper) image).getWebImage();
			if (wi.isDirty()) {
				thisImage.addInstruction(this, dif.drawWebImage(wi.extractReadOnlyWebImage(false), xform, crop, bkg, getClip()));
			}
		} else {
			thisImage.addImage(this, image, observer, xform, crop);
		}
	}

	@Override
	protected void writeString(String string, double x, double y) throws IOException {
		thisImage.addInstruction(this, dif.drawString(string, x, y, getClip()));
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
	protected void writeComposite(Composite composite) {
		if (composite instanceof AlphaComposite) {
			AlphaComposite ac = (AlphaComposite) composite;
			thisImage.addInstruction(this, dif.setComposite(ac));
		}
	}

	@Override
	protected void writeFont(Font font) {
		thisImage.addInstruction(this, dif.setFont(font));
	}

	@Override
	public void copyArea(int x, int y, int width, int height, int dx, int dy) {
		Point2D abs = getTransform().transform(new Point(x, y), null);
		int absx = (int) abs.getX();
		int absy = (int) abs.getY();
		thisImage.addInstruction(this, dif.copyArea(absx + dx, absy + dy, width, height, dx, dy, getClip()));
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
		return GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
	}

	public boolean isDisposed() {
		return disposed;
	}

	@Override
	public void dispose() {
		thisImage.addInstruction(this, dif.disposeGraphics(this));
		thisImage.dispose(this);
		disposed = true;
	}

	public int getId() {
		return id;
	}

}
