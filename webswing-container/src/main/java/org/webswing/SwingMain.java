package org.webswing;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import org.webswing.ignored.common.PaintManager;
import org.webswing.ignored.common.ServerJvmConnection;


public class SwingMain {


    
    public static void main(String[] args) throws Exception {


        
        //set up instance of ExtLibImpl class providing jms connection and other services in separated classloader to prevent classpath pollution of swing application.  
        List<URL> urls = new ArrayList<URL>();
        for (File f : new File("lib").listFiles()) {
            urls.add(f.toURI().toURL());
        }
        ClassLoader extLibClassLoader = new URLClassLoader(urls.toArray(new URL[0]));
        Class<?> extLibclazz = extLibClassLoader.loadClass("org.webswing.extlib.ExtLibImpl");
        Method getInstanceExtLibMethod = extLibclazz.getMethod("getInstance", new Class<?>[]{PaintManager.class});
        ServerJvmConnection conn=(ServerJvmConnection) getInstanceExtLibMethod.invoke(null, PaintManager.getInstance());
        PaintManager.getInstance().setJmsService(conn);
        
        //create classloader with swinglib classpath 
        List<URL> swingurls = new ArrayList<URL>();
        for (File f : new File("swinglib").listFiles()) {
            swingurls.add(f.toURI().toURL());
        }
        ClassLoader swingLibClassloader = new URLClassLoader(swingurls.toArray(new URL[0]));    
        Class<?> swingClassloaderClass= extLibClassLoader.loadClass("org.webswing.extlib.SwingClassloader");
        Class<?> startArgTypes[] = {ClassLoader.class, (new String[0]).getClass() };
        Method startSwingMethod = swingClassloaderClass.getMethod("startSwing", startArgTypes);
        startSwingMethod.invoke(null, swingLibClassloader,args);
        
        
        
    }
}
