package org.webswing.classloader;

import org.webswing.ext.services.SwingClassLoaderFactoryService;

public class SwingClassLoaderFactory implements SwingClassLoaderFactoryService {

    private static SwingClassLoaderFactory impl;

    public static SwingClassLoaderFactory getInstance() {
        if (impl == null) {
            impl = new SwingClassLoaderFactory();
        }
        return impl;
    }
    
    private SwingClassLoaderFactory() {
    }

    public ClassLoader createSwingClassLoader(ClassLoader parent) {
        return new SwingClassloader(parent);
    }
}
