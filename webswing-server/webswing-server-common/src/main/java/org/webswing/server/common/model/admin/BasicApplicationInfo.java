package org.webswing.server.common.model.admin;

import org.webswing.model.s2c.ApplicationInfoMsg;

import java.io.Serializable;

public class BasicApplicationInfo implements Serializable {

	private String name;
	private boolean enabled;
	private String path;
	private String url;
	private int runningInstances;

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getRunningInstances() {
		return runningInstances;
	}

	public void setRunningInstances(int runningInstances) {
		this.runningInstances = runningInstances;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public boolean isEnabled() {
		return enabled;
	}
}
