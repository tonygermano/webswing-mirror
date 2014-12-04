package org.webswing.ext.services;

public interface SwingClassLoaderFactoryService {

    ClassLoader createSwingClassLoader(ClassLoader parent);
}
