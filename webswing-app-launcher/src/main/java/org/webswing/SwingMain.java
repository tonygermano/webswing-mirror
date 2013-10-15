package org.webswing;

import java.awt.Toolkit;
import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import org.webswing.common.ImageServiceIfc;
import org.webswing.common.ServerConnectionIfc;
import org.webswing.toolkit.WebToolkit;

public class SwingMain {
    // to run as debug:
    //-Dpath.to.ifc.lib=c:\DATA\Workspaces\play\WebSwingServer2.0.git\webswing\webswing-app-interface\target\classes 
    //-Dawt.toolkit=org.webswing.toolkit.WebToolkit -Djava.awt.graphicsenv=org.webswing.toolkit.ge.WebGraphicsEnvironment
    //-noverify -Dwebswing.mainClass=com.sun.swingset3.SwingSet3 -Dwebswing.clientId=debug
    
    public static void main(String[] args) throws Exception {
//        Thread.sleep(30000);
        //set up instance of ExtLibImpl class providing jms connection and other services in separated classloader to prevent classpath pollution of swing application.  
        List<URL> urls = new ArrayList<URL>();
        for (File f : new File(SwingMain.class.getClassLoader().getResource("lib").toURI()).listFiles()) {
            urls.add(f.toURI().toURL());
        }
        if(System.getProperty("path.to.ifc.lib")!=null){
            urls.add(new File(System.getProperty("path.to.ifc.lib")).toURI().toURL());
        }
        
        ClassLoader extLibClassLoader = new URLClassLoader(urls.toArray(new URL[0]));
        Class<?> serverConnectionServiceclazz = extLibClassLoader.loadClass("org.webswing.ext.services.ServerConnectionService");
        Method getInstanceOfServerConnectionServiceMethod = serverConnectionServiceclazz.getMethod("getInstance");
        ServerConnectionIfc connService=(ServerConnectionIfc) getInstanceOfServerConnectionServiceMethod.invoke(null);
        Class<?> imageServiceclazz = extLibClassLoader.loadClass("org.webswing.ext.services.ImageService");
        Method getInstanceOfImageServiceMethod = imageServiceclazz.getMethod("getInstance");
        ImageServiceIfc imgService=(ImageServiceIfc) getInstanceOfImageServiceMethod.invoke(null);
        ((WebToolkit) Toolkit.getDefaultToolkit()).setServerConnection(connService);
        ((WebToolkit) Toolkit.getDefaultToolkit()).setImageService(imgService);
        ((WebToolkit) Toolkit.getDefaultToolkit()).init();
        
        //create classloader with swinglib classpath 
        List<URL> swingurls = new ArrayList<URL>();
        for (File f : new File(SwingMain.class.getClassLoader().getResource("swinglib").toURI()).listFiles()) {
            swingurls.add(f.toURI().toURL());
        }
        ClassLoader swingLibClassloader = new URLClassLoader(swingurls.toArray(new URL[0]));    
        Class<?> swingClassloaderClass= extLibClassLoader.loadClass("org.webswing.classloader.SwingClassloader");
        Class<?> startArgTypes[] = {ClassLoader.class, (new String[0]).getClass() };
        Method startSwingMethod = swingClassloaderClass.getMethod("startSwing", startArgTypes);
        startSwingMethod.invoke(null, swingLibClassloader,args);
    }
}
