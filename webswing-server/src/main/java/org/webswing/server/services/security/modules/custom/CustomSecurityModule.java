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
			try {
				Constructor<?> configConstructor = moduleClass.getConstructor();
				custom = (WebswingSecurityModule<WebswingCredentials>) configConstructor.newInstance();
			} catch (Exception e) {
				try {
					Constructor<?> configConstructor = moduleClass.getConstructor(WebswingSecurityModuleConfig.class);
					Class<?> configClass = configConstructor.getParameterTypes()[0];
					custom = (WebswingSecurityModule<WebswingCredentials>) configConstructor.newInstance(ServerUtil.instantiateConfig(config.getConfig(), configClass));
				} catch (Exception e1) {
					log.error("Could not construct custom security module class (using Default constructor).", e);
					log.error("Could not construct custom security module class (using WebswingSecurityModuleConfig constructor).", e1);
				}
			}
			custom.init();
		} catch (Exception e) {
			log.error("Failed to initialize custom security module. ", e);
		}
	}

	@Override
	public WebswingCredentials getCredentials(HttpServletRequest request, HttpServletResponse response, WebswingAuthenticationException e) throws ServletException, IOException {
		if (custom != null) {
			return custom.getCredentials(request, response, e);
		}
		return null;
	}

	@Override
	public WebswingUser getUser(WebswingCredentials credentials) throws WebswingAuthenticationException {
		if (custom != null) {
			return custom.getUser(credentials);
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
