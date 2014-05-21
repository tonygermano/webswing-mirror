package org.webswing.common;


public interface SwingClassLoaderFactoryIfc {
    ClassLoader createSwingClassLoader(ClassLoader parent);
}
