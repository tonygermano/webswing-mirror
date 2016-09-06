package org.webswing;

import java.applet.Applet;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;

import org.webswing.applet.AppletContainer;
import org.webswing.toolkit.util.ClasspathUtil;
import org.webswing.toolkit.util.Logger;
import org.webswing.toolkit.util.Services;

public class SwingMain {

	public static ClassLoader swingLibClassLoader;
	public static ClassLoader securityClassLoader;
	
	public static void main(String[] args) {
		try {
			URL[] urls = ClasspathUtil.populateClassPath(System.getProperty(Constants.SWING_START_SYS_PROP_CLASS_PATH));
			/*
			 * wrap into additional URLClassLoader with class path urls because
			 * some resources may contain classes from packages that should be loaded
			 * with parent class loader that otherwise would not have a classpath
			 */
			ClassLoader wrapper = new URLClassLoader(urls, SwingMain.class.getClassLoader());
			swingLibClassLoader = Services.getClassLoaderService().createSwingClassLoader(urls, wrapper);

			if (isApplet()) {
				startApplet();
			} else {
				startSwingApp(args);
			}
		} catch (Exception e) {
			Logger.fatal("SwingMain:main", e);
			System.exit(1);
		}
	}

	private static void startSwingApp(String[] args) throws Exception {
		Class<?> clazz = swingLibClassLoader.loadClass(System.getProperty(Constants.SWING_START_SYS_PROP_MAIN_CLASS));
		Class<?> mainArgType[] = { (new String[0]).getClass() };
		java.lang.reflect.Method main = clazz.getMethod("main", mainArgType);
		setupContextClassLoader(swingLibClassLoader);
		Object argsArray[] = { args };
		main.invoke(null, argsArray);
	}

	private static void startApplet() throws Exception {
		Class<?> appletClazz = swingLibClassLoader.loadClass(System.getProperty(Constants.SWING_START_SYS_PROP_APPLET_CLASS));
		Map<String, String> props = resolveProps();
		setupContextClassLoader(swingLibClassLoader);
		if (Applet.class.isAssignableFrom(appletClazz)) {
			AppletContainer ac = new AppletContainer(appletClazz, props);
			ac.start();
		} else {
			Logger.error("Error in SwingMain: " + appletClazz.getCanonicalName() + " class is not a subclass of Applet");
		}
	}

	private static void setupContextClassLoader(ClassLoader swingClassLoader) {
		Thread.currentThread().setContextClassLoader(swingClassLoader);
		try {
			EventQueue q = Toolkit.getDefaultToolkit().getSystemEventQueue();
			Class<?> systemQueue = q.getClass();
			Field cl = systemQueue.getDeclaredField("classLoader");
			cl.setAccessible(true);
			cl.set(q, Thread.currentThread().getContextClassLoader());
		} catch (Exception e) {
			Logger.error("Error in SwingMain: EventQueue thread - setting context class loader failed.", e);
		}
	}

	private static Map<String, String> resolveProps() {
		HashMap<String, String> result = new HashMap<String, String>();
		for (Object keyObj : System.getProperties().keySet()) {
			String key = (String) keyObj;
			if (key.startsWith(Constants.SWING_START_STS_PROP_APPLET_PARAM_PREFIX)) {
				String paramKey = key.substring(Constants.SWING_START_STS_PROP_APPLET_PARAM_PREFIX.length());
				result.put(paramKey, System.getProperty(key));
			}
		}
		return result;
	}

	private static boolean isApplet() {
		return System.getProperty(Constants.SWING_START_SYS_PROP_APPLET_CLASS) != null;
	}
}
