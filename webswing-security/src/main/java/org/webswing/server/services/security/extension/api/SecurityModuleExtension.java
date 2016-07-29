package org.webswing.server.services.security.extension.api;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.webswing.server.services.security.api.AbstractWebswingUser;
import org.webswing.server.services.security.api.WebswingAuthenticationException;
import org.webswing.server.services.security.modules.AbstractSecurityModule;

public class SecurityModuleExtension<T extends SecurityModuleExtensionConfig> {

	public static enum BuiltInModuleExtensions {
		oneTimeUrl;
	}

	private T config;

	public SecurityModuleExtension(T config) {
		this.config = config;
	}

	public T getConfig() {
		return config;
	}

	public AbstractWebswingUser doSufficientPreValidation(AbstractSecurityModule<?> m, HttpServletRequest request, HttpServletResponse response) throws WebswingAuthenticationException {
		throw new WebswingAuthenticationException("Not valid");
	}

	public boolean doRequiredPreValidation(AbstractSecurityModule<?> m, HttpServletRequest request, HttpServletResponse response) throws WebswingAuthenticationException {
		return true;
	}

	public boolean doRequiredPostValidation(AbstractSecurityModule<?> m, AbstractWebswingUser user, HttpServletRequest request, HttpServletResponse response) throws WebswingAuthenticationException {
		return true;
	}

	public AbstractWebswingUser decorateUser(AbstractWebswingUser user, HttpServletRequest request, HttpServletResponse response) {
		return user;
	}
}