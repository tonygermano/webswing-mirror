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
import java.net.URL;
import java.net.URLClassLoader;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.webswing.Constants;
import org.webswing.common.ImageServiceIfc;
import org.webswing.common.ServerConnectionIfc;
import org.webswing.common.SwingClassLoaderFactoryIfc;
import org.webswing.toolkit.WebToolkit;

public class Main {
 

    public static void main(String[] args) throws Exception {
        boolean client = System.getProperty(Constants.SWING_START_SYS_PROP_CLIENT_ID) != null;
        ProtectionDomain domain = Main.class.getProtectionDomain();
        URL location = domain.getCodeSource().getLocation();
        System.setProperty(Constants.WAR_FILE_LOCATION, location.toExternalForm());

        List<URL> urls = new ArrayList<URL>();
        if (client) {
            populateClasspathFromDir("WEB-INF/swing-lib", urls);
            initializeExtLibServices(urls);
            retainOnlyLauncherUrl(urls);
        } else {
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

    private static void retainOnlyLauncherUrl(List<URL> urls) {
        for (Iterator<URL> i = urls.iterator(); i.hasNext();) {
            URL thisurl = i.next();
            if (!thisurl.getFile().contains("webswing-app-launcher")){
                i.remove();
            }
        }

    }

    private static void initializeExtLibServices(List<URL> urls) throws Exception {
        ClassLoader extLibClassLoader = new URLClassLoader(urls.toArray(new URL[0]),null);
        Class<?> serverConnectionServiceclazz = extLibClassLoader.loadClass("org.webswing.ext.services.ServerConnectionService");
        Method getInstanceOfServerConnectionServiceMethod = serverConnectionServiceclazz.getMethod("getInstance");
        ServerConnectionIfc connService = (ServerConnectionIfc) getInstanceOfServerConnectionServiceMethod.invoke(null);
        Class<?> imageServiceclazz = extLibClassLoader.loadClass("org.webswing.ext.services.ImageService");
        Method getInstanceOfImageServiceMethod = imageServiceclazz.getMethod("getInstance");
        ImageServiceIfc imgService = (ImageServiceIfc) getInstanceOfImageServiceMethod.invoke(null);
        Class<?> swingClassloaderFactoryClass = extLibClassLoader.loadClass("org.webswing.classloader.SwingClassLoaderFactory");
        ((WebToolkit) Toolkit.getDefaultToolkit()).setServerConnection(connService);
        ((WebToolkit) Toolkit.getDefaultToolkit()).setImageService(imgService);
        ((WebToolkit) Toolkit.getDefaultToolkit()).setWebswingClassLoaderFactory((SwingClassLoaderFactoryIfc) swingClassloaderFactoryClass.newInstance());
        ((WebToolkit) Toolkit.getDefaultToolkit()).init();
    }

    private static void populateClasspathFromDir(String dir, List<URL> urls) throws IOException, MalformedURLException {
        for (URL f : getFilesFromPath(Main.class.getClassLoader().getResource(dir))) {
            urls.add(f);
        }
    }

    private static List<URL> getFilesFromPath(URL r) throws IOException {
        List<URL> urls = new ArrayList<URL>();
        String jar = r.getPath().split("\\!/")[0].substring(6);
        String path = r.getPath().split("\\!/")[1];
        JarFile jarFile = new JarFile(jar);
        Enumeration<JarEntry> jarEntries = jarFile.entries();
        while (jarEntries.hasMoreElements()) {
            JarEntry jarEntry = jarEntries.nextElement();
            if (!jarEntry.isDirectory() && jarEntry.getName().endsWith(".jar") && jarEntry.getName().startsWith(path)) {
                urls.add(jarEntryAsFile(jarFile, jarEntry).toURI().toURL());
            }
        }
        return urls;
    }

    private static File jarEntryAsFile(JarFile jarFile, JarEntry jarEntry) throws IOException {
        InputStream input = null;
        OutputStream output = null;
        try {
            String name = jarEntry.getName().replace('/', '_');
            int i = name.lastIndexOf(".");
            String extension = i > -1 ? name.substring(i) : "";
            File file = new File(getTempDir().getAbsolutePath() + File.separator + name.substring(0, name.length() - extension.length()) + extension);
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
            File baseDir = new File(System.getProperty("java.io.tmpdir"));
            String baseName = System.currentTimeMillis() + "-";

            for (int counter = 0; counter < 100; counter++) {
                File tempDir = new File(baseDir, baseName + counter);
                if (tempDir.mkdir()) {
                    System.setProperty(Constants.TEMP_DIR_PATH, tempDir.getAbsolutePath());
                    return tempDir;
                }
            }
            throw new IllegalStateException("Failed to create directory within " + 100 + " attempts (tried " + baseName + "0 to " + baseName + (100 - 1) + ')');
        } else {
            return new File(System.getProperty(Constants.TEMP_DIR_PATH));
        }
    }

}
