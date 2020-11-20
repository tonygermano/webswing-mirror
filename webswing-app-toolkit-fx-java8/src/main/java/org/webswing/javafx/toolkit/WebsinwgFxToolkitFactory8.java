package org.webswing.javafx.toolkit;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import com.sun.glass.ui.Application;
import com.sun.glass.ui.Pixels;
import com.sun.javafx.font.FontFactory;
import com.sun.prism.Graphics;
import com.sun.prism.web.WebFontFactory8;
import com.sun.prism.web.WebPrismGraphicsWrapper8;
import com.sun.prism.web.WebRTTextureWrapper;

public class WebsinwgFxToolkitFactory8 extends WebsinwgFxToolkitFactory{
	@Override
	public Graphics createWebPrismGraphicsWrapper(Graphics original, WebRTTextureWrapper textureWrapper) {
		return new WebPrismGraphicsWrapper8(original,textureWrapper);
	}

	@Override
	public Pixels createPixels(int width, int height, ByteBuffer data) {
		return new WebPixels8(width,height,data);
	}

	@Override
	public Pixels createPixels(int width, int height, IntBuffer data) {
		return new WebPixels8(width,height,data);
	}

	@Override
	public Application createApplication() {
		return new WebApplication8();
	}

	@Override
	public FontFactory createWebFontFactory(FontFactory fontFactory) {
		return new WebFontFactory8(fontFactory);
	}
}
