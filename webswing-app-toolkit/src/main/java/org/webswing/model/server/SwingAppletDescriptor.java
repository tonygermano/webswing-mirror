package org.webswing.model.server;

import java.util.HashMap;

public class SwingAppletDescriptor extends SwingDescriptor {

	private static final long serialVersionUID = -6022666479559719641L;
	private String appletClass;
	private HashMap<String, String> parameters;

	public String getAppletClass() {
		return appletClass;
	}

	public void setAppletClass(String appletClass) {
		this.appletClass = appletClass;
	}

	public HashMap<String, String> getParameters() {
		return parameters;
	}

	public void setParameters(HashMap<String, String> parameters) {
		this.parameters = parameters;
	}

}
