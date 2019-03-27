package org.webswing.javafx.toolkit;

import com.sun.glass.ui.Pixels;

import com.sun.glass.ui.Robot;

import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import com.sun.glass.ui.Screen;

import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class WebApplication8 extends AbstractWebApplication {

	@Override
	protected Screen[] staticScreen_getScreens() {
		try {
			Constructor<Screen> screenConstructor = Screen.class.getDeclaredConstructor(long.class, int.class, int.class, int.class, int.class, int.class, int.class, int.class, int.class, int.class, int.class, int.class, float.class);
			screenConstructor.setAccessible(true);

			Rectangle b = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration().getBounds();
			int depth = 24;
			int dpi = 72;
			Screen[] screens = new Screen[1];
			screens[0] = screenConstructor.newInstance(1L, depth, b.x, b.y, b.width, b.height, b.x, b.y, b.width, b.height, dpi, dpi, 1.0f);
			return screens;
		} catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
			throw new RuntimeException("Unable to construct a Screen", e);
		}
	}

	@Override
	public Pixels createPixels(int width, int height, IntBuffer data, float scale) {
		return new WebPixels8(width, height, data, scale);
	}

	@Override
	public Robot createRobot() {
		return null;
	}

}