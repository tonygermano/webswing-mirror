package org.webswing;

import java.lang.reflect.Method;

import org.webswing.ignored.common.PaintManager;
import org.webswing.ignored.special.UIManagerConfigurator;
import org.webswing.server.SwingServer;


public class SwingMain {

    public static Thread notifyExitThread=new Thread() {
        @Override
        public void run() {
            PaintManager.getInstance().notifyShutDown();
        }
    };
    
    public static void main(String[] args) throws Exception {
        UIManagerConfigurator.configureUI();
        Runtime.getRuntime().addShutdownHook(notifyExitThread);

        SwingClassloader cl = new SwingClassloader();
        Class<?> clazz = cl.loadClass(System.getProperty(SwingServer.SWING_START_SYS_PROP_MAIN_CLASS));

        // Get a class representing the type of the main method's argument
        Class<?> mainArgType[] = { (new String[0]).getClass() };
        String progArgs[] = args;

        // Find the standard main method in the class
        Method main = clazz.getMethod("main", mainArgType);
        
        // well-behaved Java packages work relative to the
        // context classloader.  Others don't (like commons-logging)
        //????Thread.currentThread().setContextClassLoader(cl);
        
        // Create a list containing the arguments -- in this case,
        // an array of strings
        Object argsArray[] = { progArgs };

        // Call the method
        main.invoke(null, argsArray);
    }
}
