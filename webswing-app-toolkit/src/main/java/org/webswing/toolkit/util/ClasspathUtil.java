package org.webswing.toolkit.util;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ClasspathUtil {
	public static URL[] populateClassPath(String classpath) throws MalformedURLException {
		List<URL> urls = new ArrayList<URL>();
		String[] cp = scanForFiles(classpath.split(";"));
		Logger.debug("Swing classpath: " + Arrays.asList(cp));
		for (String f : cp) {
			if (f.length() > 0) {
				File file = new File(f);
				if (file.exists()) {
					urls.add(file.toURI().toURL());
				} else {
					Logger.error("SwingMain:main ERROR: Required classpath file '" + f + "' does not exist!");
				}
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
}
