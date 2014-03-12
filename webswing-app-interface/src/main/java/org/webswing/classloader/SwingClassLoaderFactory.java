package org.webswing.classloader;

import org.webswing.common.SwingClassLoaderFactoryIfc;


public class SwingClassLoaderFactory implements SwingClassLoaderFactoryIfc{

    public ClassLoader createSwingClassLoader(ClassLoader parent){
        return new SwingClassloader(parent);
    }
}
