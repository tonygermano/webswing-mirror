package org.webswing;

import java.awt.Toolkit;
import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import org.webswing.toolkit.WebToolkit;
import org.webswing.util.Logger;
import org.webswing.util.Util;

public class SwingMain {

    public static Thread notifyExitThread = new Thread() {

        @Override
        public void run() {
            Util.getWebToolkit().getPaintDispatcher().notifyShutdown();
        }
    };

    public static void main(String[] args) {
        //set up instance of ExtLibImpl class providing jms connection and other services in separated classloader to prevent classpath pollution of swing application.  
        Runtime.getRuntime().addShutdownHook(notifyExitThread);
        try {
            //create classloader with swinglib classpath 
            List<URL> swingurls = new ArrayList<URL>();
            String classpath = System.getProperty(Constants.SWING_START_SYS_PROP_CLASS_PATH);
            String[] cp = classpath.split(";");
            for (String f : cp) {
                File file = new File(f);
                if (file.exists()) {
                    swingurls.add(file.toURI().toURL());
                } else {
                    Logger.error("SwingMain:main ERROR: Required classpath file '" + f + "' does not exist!");
                    System.exit(1);
                }
            }
            ClassLoader swingLibClassloader = new URLClassLoader(swingurls.toArray(new URL[0]));
            ClassLoader swingClassloader = ((WebToolkit) Toolkit.getDefaultToolkit()).getWebswingClassLoaderFactory().createSwingClassLoader(swingLibClassloader);
            Class<?> clazz = swingClassloader.loadClass(System.getProperty(Constants.SWING_START_SYS_PROP_MAIN_CLASS));
            Class<?> mainArgType[] = { (new String[0]).getClass() };
            String progArgs[] = args;
            java.lang.reflect.Method main = clazz.getMethod("main", mainArgType);
            Thread.currentThread().setContextClassLoader(swingClassloader);
            Object argsArray[] = { progArgs };
            main.invoke(null, argsArray);
        } catch (Exception e) {
            Logger.fatal("SwingMain:main", e);
            System.exit(1);
        }
    }

}
