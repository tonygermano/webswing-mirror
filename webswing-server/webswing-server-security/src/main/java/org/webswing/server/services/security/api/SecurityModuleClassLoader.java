package org.webswing.server.services.security.api;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;

public class SecurityModuleClassLoader extends URLClassLoader {

	private ClassLoader webClassLoader;

	public SecurityModuleClassLoader(URL[] urls, ClassLoader webClassLoaders) {
		super(urls, ClassLoader.getSystemClassLoader());
		this.webClassLoader = webClassLoaders;
	}

	public Class<?> loadClass(String name) throws ClassNotFoundException {
		try {
			return webClassLoader.loadClass(name);
		} catch (ClassNotFoundException e) {
			return super.loadClass(name);
		}
	}

	public void close() throws IOException {
		webClassLoader = null;
		super.close();
	}

	public URL getResource(String name) {
		URL url = super.getResource(name);
		if (url == null) {
			url = webClassLoader.getResource(name);
		}
		return url;
	}

}