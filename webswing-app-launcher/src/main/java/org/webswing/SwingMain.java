package org.webswing;

import java.applet.Applet;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.io.File;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

import org.webswing.applet.*;
import org.webswing.toolkit.util.*;

public class SwingMain {

	public static ClassLoader swingLibClassLoader;

	public static void main(String[] args) {
		try {
			URL[] urls = populateClassPath();
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

	public static URL[] populateClassPath() throws MalformedURLException {
		List<URL> urls = new ArrayList<URL>();
		String classpath = System.getProperty(Constants.SWING_START_SYS_PROP_CLASS_PATH);
		String[] cp = scanForFiles(classpath.split(";"));
		Logger.debug("Swing classpath: " + Arrays.asList(cp));
		for (String f : cp) {
			File file = new File(f);
			if (file.exists()) {
				urls.add(file.toURI().toURL());
			} else {
				Logger.error("SwingMain:main ERROR: Required classpath file '" + f + "' does not exist!");
			}
		}
		return urls.toArray(new URL[urls.size()]);
	}

	public static String[] scanForFiles(String[] patternPaths) {
		List<String> result = new ArrayList<String>();
		for (String pattern : patternPaths) {
			if (pattern.contains("?") || pattern.contains("*")) {
				pattern = pattern.replaceAll("\\\\", "/");
				String[] pathSegs = pattern.split("/");
				boolean absolute = pathSegs[0].length() == 0 || pathSegs[0].contains(":");
				String currentBase = absolute ? "/" : "";
				scanForPatternFiles(pathSegs, currentBase, result);
			} else {
				result.add(pattern);
			}
		}
		return result.toArray(new String[result.size()]);
	}

	private static void scanForPatternFiles(String[] pathSegs, String currentBase, List<String> result) {
		String pathSeg = pathSegs[0];
		if (pathSegs.length > 1) {
			if (pathSeg.contains("?") || pathSeg.contains("*")) {
				File currentBaseFolder = new File(currentBase.isEmpty() ? "." : currentBase);
				if (currentBaseFolder.exists() && currentBaseFolder.isDirectory()) {
					for (String name : currentBaseFolder.list()) {
						if (matches(pathSeg, name)) {
							scanForPatternFiles(Arrays.copyOfRange(pathSegs, 1, pathSegs.length), currentBase + name + "/", result);
						}
					}
				}
			} else {
				currentBase += pathSeg + "/";
				scanForPatternFiles(Arrays.copyOfRange(pathSegs, 1, pathSegs.length), currentBase, result);
			}
		} else {
			File currentBaseFolder = new File(currentBase.isEmpty() ? "." : currentBase);
			if (currentBaseFolder.exists() && currentBaseFolder.isDirectory()) {
				for (String name : currentBaseFolder.list()) {
					if (matches(pathSeg, name)) {
						result.add(currentBase + name);
					}
				}
			}
		}
	}

	private static boolean matches(String pathSeg, String name) {
		return name.matches("^" + pathSeg.replaceAll("\\.", "\\\\.").replaceAll("\\[", "\\\\[").replaceAll("\\]", "\\\\]").replaceAll("\\?", ".").replaceAll("\\*", ".*") + "$");
	}

	private static boolean isApplet() {
		return System.getProperty(Constants.SWING_START_SYS_PROP_APPLET_CLASS) != null;
	}
}
