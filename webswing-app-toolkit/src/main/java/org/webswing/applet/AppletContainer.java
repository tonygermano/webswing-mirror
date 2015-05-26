package org.webswing.applet;

import java.applet.Applet;
import java.applet.AppletContext;
import java.awt.Dimension;
import java.util.Map;

import javax.swing.JWindow;

import org.webswing.Constants;
import org.webswing.util.Logger;

public class AppletContainer extends JWindow {

	private static final long serialVersionUID = 236630332897974628L;
	private boolean active;
	private Applet applet;

	private WebAppletStub stub;
	private WebAppletContext context;

	public AppletContainer(Class<?> appletClazz, Map<String, String> props) {
		this.setPreferredSize(getInitialDimensions());
		this.setLocation(0, 0);
		try {
			applet = (Applet) appletClazz.getConstructor().newInstance();
		} catch (Exception e) {
			Logger.error("Failed to instantiate Applet class" + appletClazz.getCanonicalName(), e);
		}
		stub = new WebAppletStub(this, props);
		context = new WebAppletContext(this);
		applet.setStub(stub);
		this.getContentPane().add(applet);
	}

	public void start() {
		applet.init();
		active = true;
		applet.start();
		this.pack();
		this.setVisible(true);
	}

	public void stop() {
		active = false;
		applet.stop();
	}

	public boolean isActive() {
		return active;
	}

	private Dimension getInitialDimensions() {
		int screenWidth = Integer.parseInt(System.getProperty(Constants.SWING_SCREEN_WIDTH, Constants.SWING_SCREEN_WIDTH_MIN + ""));
		int screenHeight = Integer.parseInt(System.getProperty(Constants.SWING_SCREEN_HEIGHT, Constants.SWING_SCREEN_HEIGHT_MIN + ""));
		return new Dimension(screenWidth, screenHeight);
	}

	public AppletContext getContext() {
		return context;
	}

	public Applet getApplet() {
		return applet;
	}
}
