package org.webswing.server.services.security.modules;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.webswing.server.services.security.api.AbstractWebswingUser;
import org.webswing.server.services.security.api.WebswingAuthenticationException;
import org.webswing.server.services.security.extension.api.SecurityModuleExtension;
import org.webswing.server.services.security.extension.api.SecurityModuleExtension.BuiltInModuleExtensions;
import org.webswing.server.services.security.extension.api.SecurityModuleExtensionConfig;
import org.webswing.server.services.security.extension.api.WebswingExtendableSecurityModuleConfig;
import org.webswing.server.services.security.extension.onetimeurl.OneTimeUrlSecurityExtension;

public abstract class AbstractExtendableSecurityModule<T extends WebswingExtendableSecurityModuleConfig> extends AbstractSecurityModule<T> {
	private static final Logger log = LoggerFactory.getLogger(AbstractExtendableSecurityModule.class);

	private List<SecurityModuleExtension<?>> extensions = new ArrayList<>();

	public AbstractExtendableSecurityModule(T config) {
		super(config);
	}

	public static String getExtensionClassName(String name) {
		try {
			BuiltInModuleExtensions builtInExtensions = BuiltInModuleExtensions.valueOf(name);
			switch (builtInExtensions) {
			case oneTimeUrl:
				return OneTimeUrlSecurityExtension.class.getName();
			default:
				return null;
			}
		} catch (Exception e) {
			return name;
		}
	}

	@Override
	public void init() {
		super.init();
		if (getConfig().getExtensions() != null) {
			for (String extensionName : getConfig().getExtensions()) {
				SecurityModuleExtension<?> extension = null;
				ClassLoader cl = Thread.currentThread().getContextClassLoader();
				try {
					Class<?> extensionClass = cl.loadClass(getExtensionClassName(extensionName));
					Constructor<?> defaultConstructor = null;
					Constructor<?> configConstructor = null;
					for (Constructor<?> constructor : extensionClass.getConstructors()) {
						Class<?>[] parameterTypes = constructor.getParameterTypes();
						if (parameterTypes.length == 1) {
							if (SecurityModuleExtensionConfig.class.isAssignableFrom(parameterTypes[0])) {
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
							extension = (SecurityModuleExtension<?>) configConstructor.newInstance(getConfig().getValueAs(extensionName, configClass));
						} catch (Exception e) {
							log.error("Could not construct security module extension class (using SecurityModuleExtensionConfig constructor).", e);
						}
					}
					if (extension == null && defaultConstructor != null) {
						try {
							extension = (SecurityModuleExtension<?>) defaultConstructor.newInstance();
						} catch (Exception e) {
							log.error("Could not construct security module extension class (using Default constructor).", e);
						}
					}
					if (extension != null) {
						extensions.add(extension);
					}
				} catch (ClassNotFoundException e) {
					throw new RuntimeException("Failed to load Security module extensions.", e);
				}
			}
		}
	}

	@Override
	public AbstractWebswingUser doLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
		for (SecurityModuleExtension<?> extension : extensions) {
			try {
				AbstractWebswingUser result = extension.doSufficientPreValidation(this, request, response);
				if (result != null) {
					onAuthenticationSuccess(request, response);
				}
				return result;
			} catch (WebswingAuthenticationException e) {
				continue;
			}
		}

		return super.doLogin(request, response);
	}

	@Override
	protected boolean preVerify(HttpServletRequest request, HttpServletResponse response) throws WebswingAuthenticationException {
		for (SecurityModuleExtension<?> extension : extensions) {
			try {
				boolean valid = extension.doRequiredPreValidation(this, request, response);
				if (!valid) {
					return false;
				}
			} catch (WebswingAuthenticationException e) {
				throw e;
			}
		}
		return true;
	}

	@Override
	protected boolean postVerify(AbstractWebswingUser user, HttpServletRequest request, HttpServletResponse response) throws WebswingAuthenticationException {
		for (SecurityModuleExtension<?> extension : extensions) {
			try {
				boolean valid = extension.doRequiredPostValidation(this, user, request, response);
				if (!valid) {
					return false;
				}
			} catch (WebswingAuthenticationException e) {
				throw e;
			}
		}
		return true;

	}

	@Override
	protected AbstractWebswingUser decorateUser(AbstractWebswingUser user, HttpServletRequest request, HttpServletResponse response) {
		for (SecurityModuleExtension<?> extension : extensions) {
			user = extension.decorateUser(user, request, response);
		}
		return user;
	}

}
