package main;

import java.awt.Toolkit;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.ProtectionDomain;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.webswing.Constants;
import org.webswing.toolkit.WebToolkit;

public class Main {

	@SuppressWarnings("restriction")
	public static void main(String[] args) throws Exception {
		boolean client = System.getProperty(Constants.SWING_START_SYS_PROP_CLIENT_ID) != null;
		System.setProperty(Constants.CREATE_NEW_TEMP, getCreateNewTemp(args));

		ProtectionDomain domain = Main.class.getProtectionDomain();
		URL location = domain.getCodeSource().getLocation();
		System.setProperty(Constants.WAR_FILE_LOCATION, location.toExternalForm());

		List<URL> urls = new ArrayList<URL>();
		if (client) {
			// initialize jmx agent
			sun.management.Agent.startAgent();

			populateClasspathFromDir("WEB-INF/swing-lib", urls);
			initializeExtLibServices(urls);
			retainOnlyLauncherUrl(urls);
		} else {
			initTempDirPath(args);
			populateClasspathFromDir("WEB-INF/server-lib", urls);
		}
		ClassLoader defaultCL = new URLClassLoader(urls.toArray(new URL[0]), null);
		Thread.currentThread().setContextClassLoader(defaultCL);
		Class<?> mainClass;
		if (client) {
			mainClass = defaultCL.loadClass("org.webswing.SwingMain");
		} else {
			mainClass = defaultCL.loadClass("org.webswing.ServerMain");
		}

		Method method = mainClass.getMethod("main", new Class[] { args.getClass() });
		method.setAccessible(true);
		try {
			method.invoke(null, new Object[] { args });
		} catch (IllegalAccessException e) {
			// This should not happen, as we have
			// disabled access checks
		}
	}

	public static String getCreateNewTemp(String[] args) {
		// create the command line parser
		for (int i = 0; i < args.length; i += 2) {
			if (args[i].equals("-d") && i + 1 < args.length) {
				return args[i + 1];
			}
		}
		return "false";

	}

	private static void retainOnlyLauncherUrl(List<URL> urls) {
		for (Iterator<URL> i = urls.iterator(); i.hasNext();) {
			URL thisurl = i.next();
			if (!thisurl.getFile().contains("webswing-app-launcher")) {
				i.remove();
			}
		}

	}

	private static void initializeExtLibServices(List<URL> urls) throws Exception {
		ClassLoader extLibClassLoader = new URLClassLoader(urls.toArray(new URL[0]), null);
		Class<?> classLoaderUtilclass = extLibClassLoader.loadClass("org.webswing.util.ClassLoaderUtil");
		Method initializeServicesMethod = classLoaderUtilclass.getMethod("initializeServices");
		initializeServicesMethod.invoke(null);
		((WebToolkit) Toolkit.getDefaultToolkit()).init();
	}

	private static void populateClasspathFromDir(String dir, List<URL> urls) throws IOException, MalformedURLException {
		for (URL f : getFilesFromPath(Main.class.getClassLoader().getResource(dir))) {
			urls.add(f);
		}
	}

	private static List<URL> getFilesFromPath(URL r) throws IOException {
		List<URL> urls = new ArrayList<URL>();
		String[] splitPath = r.getPath().split("\\!/");
		String jar = splitPath[0];
		String path = splitPath[1];
		JarFile jarFile = new JarFile(new File(URI.create(jar)));
		Enumeration<JarEntry> jarEntries = jarFile.entries();
		String tempDirPath = getTempDir().getAbsolutePath();
		while (jarEntries.hasMoreElements()) {
			JarEntry jarEntry = jarEntries.nextElement();
			if (!jarEntry.isDirectory() && jarEntry.getName().endsWith(".jar") && jarEntry.getName().startsWith(path)) {
				urls.add(jarEntryAsFile(jarFile, jarEntry, tempDirPath).toURI().toURL());
			}
		}
		return urls;
	}

	private static File jarEntryAsFile(JarFile jarFile, JarEntry jarEntry, String tempDirPath) throws IOException {
		InputStream input = null;
		OutputStream output = null;
		try {
			String name = jarEntry.getName();
			if (name.contains("/")) {
				name = name.replace('/', '_');
				int i = name.lastIndexOf(".");
				String extension = i > -1 ? name.substring(i) : "";
				name = name.substring(0, name.length() - extension.length()) + extension;
			}
			File file = new File(tempDirPath + File.separator + name);
			if (!file.exists()) {
				file.createNewFile();
				file.deleteOnExit();
				input = jarFile.getInputStream(jarEntry);
				output = new FileOutputStream(file);
				int readCount;
				byte[] buffer = new byte[4096];
				while ((readCount = input.read(buffer)) != -1) {
					output.write(buffer, 0, readCount);
				}
			}
			return file;
		} finally {
			close(input);
			close(output);
		}
	}

	private static void close(Closeable closeable) {
		if (closeable != null) {
			try {
				closeable.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static File getTempDir() {
		if (System.getProperty(Constants.TEMP_DIR_PATH) == null) {
			File baseDir = new File(System.getProperty(Constants.TEMP_DIR_PATH_BASE, System.getProperty("java.io.tmpdir")));
			if (!baseDir.exists()) {
				baseDir.mkdirs();
			}
			SimpleDateFormat sdf = new SimpleDateFormat("yy-MM-dd-HH-mm-ss");
			String baseName;
			if (Boolean.parseBoolean(System.getProperty(Constants.CREATE_NEW_TEMP, ""))) {
				baseName = sdf.format(new Date()) + "-";
				for (int counter = 0; counter < 10; counter++) {
					File tempDir = new File(baseDir, baseName + counter);
					if (tempDir.mkdir()) {
						System.setProperty(Constants.TEMP_DIR_PATH, tempDir.toURI().toString());
						return tempDir;
					}
				}
			} else {
				baseName = "release";
				File tempDir = new File(baseDir, baseName);
				if (!tempDir.exists()) {
					tempDir.mkdir();
				} else {
					for (File f : tempDir.listFiles()) {
						if (!f.delete()) {
							throw new IllegalStateException("Not possible to clean the temp folder. Make sure no other instance of webswing is running or use '-d true' option to create a new temp folder.");
						}
					}
				}
				System.setProperty(Constants.TEMP_DIR_PATH, tempDir.toURI().toString());
				return tempDir;
			}
			throw new IllegalStateException("Failed to create directory within " + 10 + " attempts (tried " + baseName + " to " + baseName + (100 - 1) + ')');
		} else {
			return new File(URI.create(System.getProperty(Constants.TEMP_DIR_PATH)));
		}
	}

	private static void initTempDirPath(String[] args) {
		if (args != null) {
			for (int i = 0; i < args.length - 1; i++) {
				if ("-t".equals(args[i]) || "-temp".equals(args[i])) {
					System.setProperty(Constants.TEMP_DIR_PATH_BASE, args[i + 1]);
					return;
				}
			}
		}
		System.setProperty(Constants.TEMP_DIR_PATH_BASE, "tmp");
	}
}
