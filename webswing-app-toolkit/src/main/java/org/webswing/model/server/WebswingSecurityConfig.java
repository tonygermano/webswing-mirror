package org.webswing.model.server;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class WebswingSecurityConfig implements Serializable {

	private static final long serialVersionUID = 7400007088370352772L;

	public static enum BuiltInModules {
		INHERITED,
		NONE,
		PROPERTY_FILE,
		SAML2
	}

	private String securityModule;
	private List<String> classPath;
	private Map<String, Object> config;

	public WebswingSecurityConfig() {
	}

	public WebswingSecurityConfig(String securityModule) {
		this.securityModule = securityModule;
	}

	public String getSecurityModule() {
		return securityModule;
	}

	public void setSecurityModule(String securityModule) {
		this.securityModule = securityModule;
	}

	public List<String> getClassPath() {
		return classPath;
	}

	public void setClassPath(List<String> classPath) {
		this.classPath = classPath;
	}

	public Map<String, Object> getConfig() {
		return config;
	}

	public void setConfig(Map<String, Object> config) {
		this.config = config;
	}

	@Override
	public String toString() {
		return "[SecurityModule=" + securityModule + "]";
	}

}
