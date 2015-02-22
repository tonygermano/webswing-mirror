package org.webswing;

import java.awt.EventQueue;
import java.awt.Toolkit;
import java.io.File;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.webswing.util.Logger;
import org.webswing.util.Services;

public class SwingMain {

	public static ClassLoader swingLibClassloader;

	public static void main(String[] args) {
		// set up instance of ExtLibImpl class providing jms connection and other services in separated classloader to prevent classpath pollution of swing application.
		try {
			// create classloader with swinglib classpath
			List<URL> swingurls = new ArrayList<URL>();
			String classpath = System.getProperty(Constants.SWING_START_SYS_PROP_CLASS_PATH);
			String[] cp = scanForFiles(classpath.split(";"), ".");
			Logger.debug("Swing classpath: " + Arrays.asList(cp));
			for (String f : cp) {
				File file = new File(f);
				if (file.exists()) {
					swingurls.add(file.toURI().toURL());
				} else {
					Logger.error("SwingMain:main ERROR: Required classpath file '" + f + "' does not exist!");
				}
			}
			swingLibClassloader = new URLClassLoader(swingurls.toArray(new URL[0]), SwingMain.class.getClassLoader());
			ClassLoader swingClassloader = Services.getClassloaderService().createSwingClassLoader(swingLibClassloader);
			Class<?> clazz = swingClassloader.loadClass(System.getProperty(Constants.SWING_START_SYS_PROP_MAIN_CLASS));
			Class<?> mainArgType[] = { (new String[0]).getClass() };
			String progArgs[] = args;
			java.lang.reflect.Method main = clazz.getMethod("main", mainArgType);
			Thread.currentThread().setContextClassLoader(swingClassloader);
			try {
				EventQueue q = Toolkit.getDefaultToolkit().getSystemEventQueue();
				Class<?> systemQueue = q.getClass();
				Field cl = systemQueue.getDeclaredField("classLoader");
				cl.setAccessible(true);
				cl.set(q, Thread.currentThread().getContextClassLoader());
			} catch (Exception e) {
				Logger.error("Error in SwingMain: EventQueue thread - setting context classloader failed.", e);
			}
			Object argsArray[] = { progArgs };
			main.invoke(null, argsArray);
		} catch (Exception e) {
			Logger.fatal("SwingMain:main", e);
			System.exit(1);
		}
	}

	public static String[] scanForFiles(String[] patternPaths, String base) {
		base = base.replaceAll("\\\\", "/");
		List<String> result = new ArrayList<String>();
		for (String pattern : patternPaths) {
			if (pattern.contains("?") || pattern.contains("*")) {
				pattern = pattern.replaceAll("\\\\", "/");
				String[] pathSegs = pattern.split("/");
				boolean absolute = pathSegs[0].length() == 0 || pathSegs[0].contains(":") ? true : false;
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

}
