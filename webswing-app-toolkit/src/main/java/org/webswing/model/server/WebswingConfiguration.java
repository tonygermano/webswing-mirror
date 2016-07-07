package org.webswing.model.server;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WebswingConfiguration implements Serializable {

	private static final long serialVersionUID = 7439369522912189703L;

	private String masterWebFolder = "${user.dir}/www";

	private SecurityMode masterSecurityMode = SecurityMode.PROPERTY_FILE;

	private Map<String, Object> masterSecurityConfig = new HashMap<String, Object>();

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

	public SecurityMode getMasterSecurityMode() {
		return masterSecurityMode;
	}

	public void setMasterSecurityMode(SecurityMode masterSecurityMode) {
		this.masterSecurityMode = masterSecurityMode;
	}

	public Map<String, Object> getMasterSecurityConfig() {
		return masterSecurityConfig;
	}

	public void setMasterSecurityConfig(Map<String, Object> masterSecurityConfig) {
		this.masterSecurityConfig = masterSecurityConfig;
	}

	public String getMasterWebFolder() {
		return masterWebFolder;
	}

	public void setMasterWebFolder(String masterWebFolder) {
		this.masterWebFolder = masterWebFolder;
	}
	
}
