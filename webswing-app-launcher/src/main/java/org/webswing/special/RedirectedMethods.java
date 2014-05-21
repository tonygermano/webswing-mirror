package org.webswing.special;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;

import org.webswing.SwingMain;

public class RedirectedMethods {

    public static void exit(int status) {
        System.exit(1);
    }

    public static void dummy() {
        //  do nothing
    }

    public static void dummy2() {
        //  do nothing
    }

    public static void writeObject(Object o) {
        //bug in java serialization- serializing class from custom classloader fails
        System.out.println("DUMMY writeObject(Object o)");
    }

    public static ClassLoader getSystemClassLoader() {
        return SwingMain.swingLibClassloader;
    }

    public static URL getSystemResource(String name) {
        return SwingMain.swingLibClassloader.getResource(name);
    }

    public static InputStream getSystemResourceAsStream(String name) {
        return SwingMain.swingLibClassloader.getResourceAsStream(name);
    }

    public static Enumeration<URL> getSystemResources(String name) throws IOException {
        return SwingMain.swingLibClassloader.getResources(name);
    }
}
