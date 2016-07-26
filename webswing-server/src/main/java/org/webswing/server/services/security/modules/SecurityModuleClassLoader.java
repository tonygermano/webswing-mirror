package org.webswing.server.services.security.modules;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;

public class SecurityModuleClassLoader extends URLClassLoader {

	private ClassLoader webClassLoaders;

	public SecurityModuleClassLoader(URL[] urls, ClassLoader webClassLoaders) {
		super(urls, ClassLoader.getSystemClassLoader());
		this.webClassLoaders = webClassLoaders;
	}

	@Override
	public Class<?> loadClass(String name) throws ClassNotFoundException {
		try {
			return webClassLoaders.loadClass(name);
		} catch (ClassNotFoundException e) {
			return super.loadClass(name);
		}
	}

	@Override
	public void close() throws IOException {
		webClassLoaders = null;
		super.close();
	}
}