package org.webswing.server.services.security.modules;

import org.webswing.server.services.security.api.WebswingSecurityModule;
import org.webswing.server.services.security.api.WebswingSecurityModuleConfig;

public abstract class AbstractSecurityModule<T extends WebswingSecurityModuleConfig> implements WebswingSecurityModule {

	private T config;

	public AbstractSecurityModule(T config) {
		this.config = config;
	}

	public T getConfig() {
		return config;
	}

	@Override
	public void init() {
	}

	@Override
	public void destroy() {
	}

}
