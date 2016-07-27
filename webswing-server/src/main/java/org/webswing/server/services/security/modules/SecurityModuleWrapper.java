package org.webswing.server.services.security.modules;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.concurrent.Callable;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.text.StrSubstitutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.webswing.server.model.exception.WsException;
import org.webswing.server.services.security.api.AbstractWebswingUser;
import org.webswing.server.services.security.api.WebswingAuthenticationException;
import org.webswing.server.services.security.api.WebswingCredentials;
import org.webswing.server.services.security.api.WebswingSecurityModule;
import org.webswing.server.services.security.api.WebswingSecurityModuleConfig;
import org.webswing.server.services.security.otp.api.OneTimePasswordModule;
import org.webswing.server.services.security.otp.api.OneTimeToken;
import org.webswing.server.util.ServerUtil;
import org.webswing.toolkit.util.ClasspathUtil;

public class SecurityModuleWrapper implements WebswingSecurityModule<WebswingCredentials>, OneTimePasswordModule {
	private static final Logger log = LoggerFactory.getLogger(SecurityModuleWrapper.class);

	private WebswingSecurityModule<WebswingCredentials> custom;
	private SecurityModuleWrapperConfig config;
	private URLClassLoader customCL;

	public SecurityModuleWrapper(SecurityModuleWrapperConfig config) {
		this.config = config;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void init() {
		try {
			URL[] urls = ClasspathUtil.populateClassPath(getClassPath());
			customCL = new SecurityModuleClassLoader(urls, SecurityModuleWrapper.class.getClassLoader());
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
				runWithContextClassLoader(new Callable<Object>() {

					@Override
					public Object call() throws Exception {
						custom.init();
						return null;
					}
				});
			} else {
				log.error("Custom security module class should define a default or WebswingSecurityModuleConfig constructor!");
			}
		} catch (Exception e) {
			log.error("Failed to initialize security module. ", e);
			throw new RuntimeException("Failed to initialize Secuirty module.", e);
		}
	}

	@Override
	public WebswingCredentials getCredentials(final HttpServletRequest request, final HttpServletResponse response, final WebswingAuthenticationException e) throws ServletException, IOException {
		if (custom != null) {
			WebswingCredentials credentials;
			try {
				credentials = runWithContextClassLoader(new Callable<WebswingCredentials>() {

					@Override
					public WebswingCredentials call() throws Exception {
						return custom.getCredentials(request, response, e);
					}
				});
			} catch (Exception e1) {
				if (e1 instanceof ServletException) {
					throw (ServletException) e1;
				} else if (e1 instanceof IOException) {
					throw (IOException) e1;
				} else {
					throw new IOException("Failed to get user. Unexpected exception", e);
				}
			}
			return credentials;
		}
		return null;

	}

	@Override
	public AbstractWebswingUser getUser(final WebswingCredentials credentials) throws WebswingAuthenticationException {
		if (custom != null) {
			AbstractWebswingUser user;
			try {
				user = runWithContextClassLoader(new Callable<AbstractWebswingUser>() {

					@Override
					public AbstractWebswingUser call() throws Exception {
						return custom.getUser(credentials);
					}
				});
			} catch (Exception e) {
				if (e instanceof WebswingAuthenticationException) {
					throw (WebswingAuthenticationException) e;
				} else {
					throw new WebswingAuthenticationException("Failed to get user. Unexpected exception", e);
				}
			}
			return user;
		}
		return null;
	}

	private <T> T runWithContextClassLoader(Callable<T> contextCallback) throws Exception {
		ClassLoader original = Thread.currentThread().getContextClassLoader();
		try {
			Thread.currentThread().setContextClassLoader(customCL);
			return contextCallback.call();
		} finally {
			Thread.currentThread().setContextClassLoader(original);
		}
	}

	@Override
	public void destroy() {
		if (custom != null) {
			try {
				runWithContextClassLoader(new Callable<Object>() {

					@Override
					public Object call() throws Exception {
						custom.destroy();
						return null;
					}
				});
			} catch (Exception e1) {
				log.error("Destroying SecurityModule failed.", e1);
			}
		}
		if (customCL != null) {
			try {
				customCL.close();
			} catch (IOException e) {
				log.error("Closing Custom SecurityModule classloader failed.", e);
			}
		}
	}

	public String getClassPath() {
		StrSubstitutor subs = ServerUtil.getConfigSubstitutor();
		String cp = ServerUtil.generateClassPathString(config.getClassPath());
		return subs.replace(cp);
	}

	public AbstractWebswingUser verifyOneTimePassword(final String otp) throws WebswingAuthenticationException {
		if (custom != null && custom instanceof OneTimePasswordModule) {
			try {
				return runWithContextClassLoader(new Callable<AbstractWebswingUser>() {

					@Override
					public AbstractWebswingUser call() throws Exception {
						AbstractWebswingUser result = ((OneTimePasswordModule) custom).verifyOneTimePassword(otp);
						return result;
					}
				});
			} catch (WebswingAuthenticationException e1) {
				throw e1;
			} catch (Exception e) {
				log.error("Verifying OTP failed.", e);
				throw new WebswingAuthenticationException("Verifying OTP failed.", e);
			}
		}
		return null;
	}

	@Override
	public String generateOneTimePassword(final String swingPath, final String requestingClient, final String user, final String[] roles, final String[] permissions) throws WebswingAuthenticationException {
		if (custom != null && custom instanceof OneTimePasswordModule) {
			try {
				return runWithContextClassLoader(new Callable<String>() {

					@Override
					public String call() throws Exception {
						String result = ((OneTimePasswordModule) custom).generateOneTimePassword(swingPath, requestingClient, user, roles, permissions);
						return result;
					}
				});
			} catch (Exception e1) {
				log.error("Failed to generate OTP.", e1);
				throw new WebswingAuthenticationException("Failed to generate OTP.", e1);
			}
		}
		return null;
	}

}
