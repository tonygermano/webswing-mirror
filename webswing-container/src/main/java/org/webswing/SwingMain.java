package org.webswing;

import java.awt.Toolkit;
import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import org.webswing.ignored.common.PaintManager;
import org.webswing.ignored.common.ServerJvmConnection;
import org.webswing.toolkit.WebToolkit;

public class SwingMain {


    
    public static void main(String[] args) throws Exception {

        //set up instance of ExtLibImpl class providing jms connection and other services in separated classloader to prevent classpath pollution of swing application.  
        List<URL> urls = new ArrayList<URL>();
        for (File f : new File(SwingMain.class.getClassLoader().getSystemResource("lib").toURI()).listFiles()) {
            urls.add(f.toURI().toURL());
        }
        if(System.getProperty("path.to.ext.lib")!=null){
            urls.add(new File(System.getProperty("path.to.ext.lib")).toURI().toURL());
        }
        
        ClassLoader extLibClassLoader = new URLClassLoader(urls.toArray(new URL[0]));
        Class<?> extLibclazz = extLibClassLoader.loadClass("org.webswing.extlib.ExtLibImpl");
        Method getInstanceExtLibMethod = extLibclazz.getMethod("getInstance");
        ServerJvmConnection conn=(ServerJvmConnection) getInstanceExtLibMethod.invoke(null);
        ((WebToolkit) Toolkit.getDefaultToolkit()).setJmsConnector(conn);
        
        //create classloader with swinglib classpath 
        List<URL> swingurls = new ArrayList<URL>();
        for (File f : new File(SwingMain.class.getClassLoader().getSystemResource("swinglib").toURI()).listFiles()) {
            swingurls.add(f.toURI().toURL());
        }
        ClassLoader swingLibClassloader = new URLClassLoader(swingurls.toArray(new URL[0]));    
        Class<?> swingClassloaderClass= extLibClassLoader.loadClass("org.webswing.extlib.SwingClassloader");
        Class<?> startArgTypes[] = {ClassLoader.class, (new String[0]).getClass() };
        Method startSwingMethod = swingClassloaderClass.getMethod("startSwing", startArgTypes);
        startSwingMethod.invoke(null, swingLibClassloader,args);
    }
}
