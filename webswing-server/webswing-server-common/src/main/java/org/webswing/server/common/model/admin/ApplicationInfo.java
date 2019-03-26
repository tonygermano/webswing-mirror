package org.webswing.server.common.model.admin;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.webswing.server.common.model.SecuredPathConfig;

public class ApplicationInfo extends BasicApplicationInfo {
	private static final long serialVersionUID = 7847135426884331742L;

	private SecuredPathConfig config;
	private Map<String, String> variables;
	private int connectedInstances;
	private int maxRunningInstances;
	private int finishedInstances;
	private Map<String, Map<Long, Number>> stats;
	private Map<String, List<String>> warnings;
	private InstanceManagerStatus status;

	public int getConnectedInstances() {
		return connectedInstances;
	}

	public void setConnectedInstances(int connectedInstances) {
		this.connectedInstances = connectedInstances;
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

	public SecuredPathConfig getConfig() {
		return config;
	}

	public void setConfig(SecuredPathConfig config) {
		this.config = config;
	}

	public Map<String, String> getVariables() {
		return variables;
	}

	public void setVariables(Map<String, String> variables) {
		this.variables = variables;
	}

	public Map<String, Map<Long, Number>> getStats() {
		return stats;
	}

	public void setStats(Map<String, Map<Long, Number>> stats) {
		this.stats = stats;
	}

	public void setWarnings(Map<String, List<String>> summaryWarnings) {
		this.warnings = summaryWarnings;
	}

	public Map<String, List<String>> getWarnings() {
		return warnings;
	}

	public InstanceManagerStatus getStatus() {
		return status;
	}

	public void setStatus(InstanceManagerStatus status) {
		this.status = status;
	}
}
