package org.webswing.server.common.model.admin;

import java.io.Serializable;

import org.webswing.server.common.model.SecuredPathConfig;
import org.webswing.server.common.model.meta.MetaObject;

public class ApplicationInfo implements Serializable {
	private static final long serialVersionUID = 7847135426884331742L;
	private String path;
	private String url;
	private byte[] icon;
	private String name;
	private InstanceManagerStatus status;
	private SecuredPathConfig config;
	private int connectedInstances;
	private int runningInstances;
	private int maxRunningInstances;
	private int finishedInstances;
	private SwingJvmStats avgStats;
	private SwingJvmStats maxStats;

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

	public int getConnectedInstances() {
		return connectedInstances;
	}

	public void setConnectedInstances(int connectedInstances) {
		this.connectedInstances = connectedInstances;
	}

	public void setRunningInstances(int runningInstances) {
		this.runningInstances = runningInstances;
	}

	public int getFinishedInstances() {
		return finishedInstances;
	}

	public void setFinishedInstances(int finishedInstances) {
		this.finishedInstances = finishedInstances;
	}

	public int getMaxRunningInstances() {
		return maxRunningInstances;
	}

	public void setMaxRunningInstances(int maxRunningInstances) {
		this.maxRunningInstances = maxRunningInstances;
	}

	public SwingJvmStats getAvgStats() {
		return avgStats;
	}

	public void setAvgStats(SwingJvmStats avgStats) {
		this.avgStats = avgStats;
	}

	public SwingJvmStats getMaxStats() {
		return maxStats;
	}

	public void setMaxStats(SwingJvmStats maxStats) {
		this.maxStats = maxStats;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public byte[] getIcon() {
		return icon;
	}

	public void setIcon(byte[] icon) {
		this.icon = icon;
	}

	public SecuredPathConfig getConfig() {
		return config;
	}

	public void setConfig(SecuredPathConfig config) {
		this.config = config;
	}

	public InstanceManagerStatus getStatus() {
		return status;
	}

	public void setStatus(InstanceManagerStatus status) {
		this.status = status;
	}

}
