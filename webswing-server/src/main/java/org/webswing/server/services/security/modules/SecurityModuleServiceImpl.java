package org.webswing.server.services.security.modules;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.webswing.model.server.SecurityMode;
import org.webswing.server.services.security.api.WebswingCredentials;
import org.webswing.server.services.security.api.WebswingSecurityModule;
import org.webswing.server.services.security.modules.custom.CustomSecurityModule;
import org.webswing.server.services.security.modules.custom.CustomSecurityModuleConfig;
import org.webswing.server.services.security.modules.none.UnsecuredSecurityModule;
import org.webswing.server.services.security.modules.property.PropertySecurityModule;
import org.webswing.server.services.security.modules.property.PropertySecurityModuleConfig;

import com.google.inject.Singleton;

@Singleton
public class SecurityModuleServiceImpl implements SecurityModuleService {
	private static final Logger log = LoggerFactory.getLogger(SecurityModuleServiceImpl.class);

	public WebswingSecurityModule<? extends WebswingCredentials> create(SecurityMode mode, Map<String, Object> config) {
		switch (mode) {
		case INHERITED:
			return null;
		case NONE:
			return new UnsecuredSecurityModule(config);
		case PROPERTY_FILE:
			return new PropertySecurityModule(instantiateConfig(config, PropertySecurityModuleConfig.class));
		case CUSTOM:
			return new CustomSecurityModule(instantiateConfig(config, CustomSecurityModuleConfig.class));
		default:
			break;
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private static <T> T instantiateConfig(final Map<String, Object> config, final Class<T> clazz) {
		if (config != null) {
			return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class[] { clazz }, new InvocationHandler() {

				@Override
				@SuppressWarnings("rawtypes")
				public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
					BeanInfo info = Introspector.getBeanInfo(clazz);
					PropertyDescriptor[] pds = info.getPropertyDescriptors();
					for (PropertyDescriptor pd : pds) {
						if (pd.getReadMethod().equals(method)) {
							Object value = config.get(pd.getName());
							if (method.getReturnType().isAssignableFrom(value.getClass())) {
								return value;
							} else if (value instanceof Map && method.getReturnType().isInterface()) {
								return instantiateConfig((Map) value, method.getReturnType());
							} else {
								log.error("Invalid SecurityModule configuration. Type of " + clazz.getName() + "." + pd.getName() + " is not " + method.getReturnType());
								return null;
							}
						}
					}
					return null;
				}
			});
		}
		return null;
	}

}
