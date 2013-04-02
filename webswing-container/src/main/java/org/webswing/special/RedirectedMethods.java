package org.webswing.special;


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
}
