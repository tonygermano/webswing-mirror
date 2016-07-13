package org.webswing.server.services.security.modules.custom;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.net.URLClassLoader;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.webswing.server.services.security.api.WebswingAuthenticationException;
import org.webswing.server.services.security.api.WebswingCredentials;
import org.webswing.server.services.security.api.WebswingSecurityModule;
import org.webswing.server.services.security.api.WebswingSecurityModuleConfig;
import org.webswing.server.services.security.api.WebswingUser;
import org.webswing.server.util.ServerUtil;
import org.webswing.toolkit.util.ClasspathUtil;

public class CustomSecurityModule implements WebswingSecurityModule<WebswingCredentials> {
	private static final Logger log = LoggerFactory.getLogger(CustomSecurityModule.class);

	private WebswingSecurityModule<WebswingCredentials> custom;
	private CustomSecurityModuleConfig config;
	private URLClassLoader customCL;

	public CustomSecurityModule(CustomSecurityModuleConfig config) {
		this.config = config;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void init() {
		try {
			StringBuilder classpath = new StringBuilder();
			for (String path : config.getClassPath()) {
				classpath.append(path).append(";");
			}
			URL[] urls = ClasspathUtil.populateClassPath(classpath.substring(0, classpath.length() - 1));
			customCL = new URLClassLoader(urls, this.getClass().getClassLoader());
			Class<?> moduleClass = customCL.loadClass(config.getClassName());

			Constructor<?> defaultConstructor = null;
			Constructor<?> configConstructor = null;
			for (Constructor<?> constructor : moduleClass.getConstructors()) {
				Class<?>[] parameterTypes = constructor.getParameterTypes();
				if (parameterTypes.length == 1) {
					if (WebswingSecurityModuleConfig.class.isAssignableFrom(parameterTypes[0])) {
						configConstructor = constructor;
						break;
					}
				} else if (parameterTypes.length == 0) {
					defaultConstructor = constructor;
				}
			}

			if (configConstructor != null) {
				Class<?> configClass = configConstructor.getParameterTypes()[0];
				try {
					custom = (WebswingSecurityModule<WebswingCredentials>) configConstructor.newInstance(ServerUtil.instantiateConfig(config.getConfig(), configClass, config.getContext()));
				} catch (Exception e) {
					log.error("Could not construct custom security module class (using WebswingSecurityModuleConfig constructor).", e);
				}
			}
			if (custom == null && defaultConstructor != null) {
				try {
					custom = (WebswingSecurityModule<WebswingCredentials>) defaultConstructor.newInstance();
				} catch (Exception e) {
					log.error("Could not construct custom security module class (using Default constructor).", e);
				}
			}
			if (custom != null) {
				custom.init();
			} else {
				log.error("Custom security module class should define a default or WebswingSecurityModuleConfig constructor!");
			}
		} catch (Exception e) {
			log.error("Failed to initialize custom security module. ", e);
		}
	}

	@Override
	public WebswingCredentials getCredentials(HttpServletRequest request, HttpServletResponse response, WebswingAuthenticationException e) throws ServletException, IOException {
		if (custom != null) {
			ClassLoader original = Thread.currentThread().getContextClassLoader();
			WebswingCredentials credentials;
			try {
				Thread.currentThread().setContextClassLoader(customCL);
				credentials = custom.getCredentials(request, response, e);
			} finally {
				Thread.currentThread().setContextClassLoader(original);
			}
			return credentials;
		}
		return null;
	}

	@Override
	public WebswingUser getUser(WebswingCredentials credentials) throws WebswingAuthenticationException {
		if (custom != null) {
			ClassLoader original = Thread.currentThread().getContextClassLoader();
			WebswingUser user;
			try {
				Thread.currentThread().setContextClassLoader(customCL);
				user = custom.getUser(credentials);
			} finally {
				Thread.currentThread().setContextClassLoader(original);
			}
			return user;
		}
		return null;
	}

	@Override
	public void destroy() {
		if (custom != null) {
			custom.destroy();
			try {
				customCL.close();
			} catch (IOException e) {
				log.error("Closing Custom SecurityModule classloader failed.", e);
			}
		}
	}

}
