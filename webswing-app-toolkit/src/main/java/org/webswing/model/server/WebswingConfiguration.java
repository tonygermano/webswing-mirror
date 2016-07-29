package org.webswing.model.server;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.webswing.model.server.WebswingSecurityConfig.BuiltInModules;

public class WebswingConfiguration implements Serializable {

	private static final long serialVersionUID = 7439369522912189703L;

	private String webFolder = "www";

	private WebswingSecurityConfig securityConfig = new WebswingSecurityConfig(BuiltInModules.PROPERTY_FILE.name());

	private List<SwingApplicationDescriptor> applications = new ArrayList<SwingApplicationDescriptor>();

	private List<SwingAppletDescriptor> applets = new ArrayList<SwingAppletDescriptor>();

	public void setApplications(List<SwingApplicationDescriptor> applications) {
		this.applications = applications;
	}

	public List<SwingApplicationDescriptor> getApplications() {
		return applications;
	}

	public List<SwingAppletDescriptor> getApplets() {
		return applets;
	}

	public void setApplets(List<SwingAppletDescriptor> applets) {
		this.applets = applets;
	}

	public String getWebFolder() {
		return webFolder;
	}

	public void setWebFolder(String webFolder) {
		this.webFolder = webFolder;
	}

	public WebswingSecurityConfig getSecurityConfig() {
		return securityConfig;
	}

	public void setSecurityConfig(WebswingSecurityConfig securityConfig) {
		this.securityConfig = securityConfig;
	}
	
	

}
