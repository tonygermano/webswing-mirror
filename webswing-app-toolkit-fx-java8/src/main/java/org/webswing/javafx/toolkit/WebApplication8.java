package org.webswing.javafx.toolkit;

import com.sun.glass.ui.Pixels;

import com.sun.glass.ui.Robot;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public class WebApplication8 extends AbstractWebApplication {

	@Override
	public Pixels createPixels(int width, int height, IntBuffer data, float scale) {
		return new WebPixels8(width, height, data, scale);
	}

	@Override
	public Robot createRobot() {
		return null;
	}

}