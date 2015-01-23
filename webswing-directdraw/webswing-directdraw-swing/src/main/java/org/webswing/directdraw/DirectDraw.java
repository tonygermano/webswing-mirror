package org.webswing.directdraw;

import java.awt.ImageCapabilities;

import org.webswing.directdraw.toolkit.DrawInstructionFactory;
import org.webswing.directdraw.toolkit.VolatileWebImageWrapper;
import org.webswing.directdraw.toolkit.WebImage;
import org.webswing.directdraw.util.DrawConstantPool;

public class DirectDraw {
	private static final int DRAW_CONSTANTS_POOL_CACHE_CAPACITY = 8192;
	private static final int IMAGES_POOL_CACHE_CAPACITY = 256;

	private DirectDrawServicesAdapter services = new DirectDrawServicesAdapter();
	private DrawInstructionFactory instructionFactory = new DrawInstructionFactory(this);

	private DrawConstantPool constantPool;
	private DrawConstantPool imagePool;

	public DirectDraw() {
		resetConstantCache();
	}

	public DirectDraw(DirectDrawServicesAdapter services) {
		this();
		this.services = services;
	}

	public void resetConstantCache() {
		constantPool = new DrawConstantPool(DRAW_CONSTANTS_POOL_CACHE_CAPACITY);
		imagePool = new DrawConstantPool(IMAGES_POOL_CACHE_CAPACITY);
	}

	public DrawConstantPool getConstantPool() {
		return constantPool;
	}

	public DrawConstantPool getImagePool() {
		return imagePool;
	}

	public DirectDrawServicesAdapter getServices() {
		return services;
	}

	public WebImage createImage(int w, int h) {
		return new WebImage(this, w, h);
	}

	public VolatileWebImageWrapper createVolatileImage(int w, int h, ImageCapabilities caps) {
		return new VolatileWebImageWrapper(caps, createImage(w, h));
	}

	public DrawInstructionFactory getInstructionFactory() {
		return instructionFactory;
	}

}
