package org.webswing.server.services.config;

import org.webswing.server.common.model.SecuredPathConfig;

public class ConfigurationChangeEvent {
	private String path;
	private SecuredPathConfig oldConfig;
	private SecuredPathConfig newConfig;

	public ConfigurationChangeEvent(String path, SecuredPathConfig oldConfig, SecuredPathConfig newConfig) {
		super();
		this.path = path;
		this.oldConfig = oldConfig;
		this.newConfig = newConfig;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public SecuredPathConfig getOldConfig() {
		return oldConfig;
	}

	public void setOldConfig(SecuredPathConfig oldConfig) {
		this.oldConfig = oldConfig;
	}

	public SecuredPathConfig getNewConfig() {
		return newConfig;
	}

	public void setNewConfig(SecuredPathConfig newConfig) {
		this.newConfig = newConfig;
	}

}
