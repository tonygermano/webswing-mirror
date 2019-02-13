package org.webswing.javafx.toolkit;

import com.sun.glass.ui.GlassRobot;
import com.sun.glass.ui.Pixels;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public class WebApplication11 extends AbstractWebApplication {
	@Override
	public Pixels createPixels(int width, int height, IntBuffer data, float v, float v1) {
		return new WebPixels11(width, height, data,v,v1);
	}

	@Override
	public GlassRobot createRobot() {
		return null;
	}

}