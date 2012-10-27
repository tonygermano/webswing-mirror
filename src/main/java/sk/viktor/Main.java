package sk.viktor;

import java.lang.reflect.Method;


public class Main {
    
    public static void main(String[] args) throws Exception {
        SwingClassloader cl = new SwingClassloader();
        Class<?> clazz = cl.loadClass("sk.viktor.server.SwingServer");
        Method startServer = clazz.getMethod("startServer");
        startServer.invoke(null);
    }
    
    public static void startSwing() throws Exception{
        Class<?> clazz = Main.class.getClassLoader().loadClass("com.sun.swingset3.SwingSet3");
        //Class<?> clazz = cl.loadClass("sk.viktor.Ceiling");
        // Get a class representing the type of the main method's argument
        Class mainArgType[] = { (new String[0]).getClass() };
        String progArgs[] = new String[0];

        // Find the standard main method in the class
        Method main = clazz.getMethod("main", mainArgType);

        // Create a list containing the arguments -- in this case,
        // an array of strings
        Object argsArray[] = { progArgs };

        // Call the method
        main.invoke(null, argsArray);
    }
}
