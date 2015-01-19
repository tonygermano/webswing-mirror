package org.webswing.directdraw.toolkit;

import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.ImageCapabilities;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.awt.image.VolatileImage;

public class VolatileWebImageWrapper extends VolatileImage {

	private WebImage webImage;
	private ImageCapabilities caps;

	public VolatileWebImageWrapper(ImageCapabilities caps, WebImage webImage) {
		this.webImage = webImage;
		this.caps = caps;
	}

	@Override
	public BufferedImage getSnapshot() {
		return null;
	}

	@Override
	public int getWidth() {
		return this.webImage.getWidth(null);
	}

	@Override
	public int getHeight() {
		return this.webImage.getHeight(null);
	}

	@Override
	public Graphics2D createGraphics() {
		return (Graphics2D) this.webImage.getGraphics();
	}

	@Override
	public int validate(GraphicsConfiguration gc) {
		this.webImage.reset();
		return VolatileImage.IMAGE_RESTORED;
	}

	@Override
	public boolean contentsLost() {
		return false;
	}

	@Override
	public ImageCapabilities getCapabilities() {
		return this.caps;
	}

	@Override
	public int getWidth(ImageObserver observer) {
		return this.webImage.getWidth(observer);
	}

	@Override
	public int getHeight(ImageObserver observer) {
		return this.webImage.getHeight(observer);
	}

	@Override
	public Object getProperty(String name, ImageObserver observer) {
		return this.webImage.getProperty(name, observer);
	}

	public WebImage getWebImage() {
		return webImage;
	}
}
