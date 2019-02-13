package com.sun.prism.web;

import com.sun.prism.Graphics;

public class WebPrismGraphicsWrapper8 extends AbstractWebPrismGraphicsWrapper {
	public WebPrismGraphicsWrapper8(Graphics original, WebTextureWrapper texture) {
		super(original, texture);
	}

	@Override
	public void setPixelScaleFactor(float pixelScale) {
		original.setPixelScaleFactor(pixelScale);
	}

	@Override
	public float getPixelScaleFactor() {
		return original.getPixelScaleFactor();
	}
}
