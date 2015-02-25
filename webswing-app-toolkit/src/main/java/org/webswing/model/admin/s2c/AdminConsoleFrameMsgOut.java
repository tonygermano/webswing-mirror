package org.webswing.model.admin.s2c;

import java.util.ArrayList;
import java.util.List;

import org.webswing.model.MsgOut;
import org.webswing.model.server.WebswingConfiguration;
import org.webswing.model.server.WebswingConfigurationBackup;

public class AdminConsoleFrameMsgOut implements MsgOut {

	private static final long serialVersionUID = -607390486198389246L;

	private String type = "admin";
	private List<SwingSessionMsg> sessions = new ArrayList<SwingSessionMsg>();
	private List<SwingSessionMsg> closedSessions = new ArrayList<SwingSessionMsg>();
	private WebswingConfiguration configuration;
	private WebswingConfiguration liveConfiguration;
	private WebswingConfigurationBackup configurationBackup;
	private ServerPropertiesMsg serverProperties;
	private String userConfig;
	private MessageMsg message;

	public List<SwingSessionMsg> getSessions() {
		return sessions;
	}

	public void setSessions(List<SwingSessionMsg> sessions) {
		this.sessions = sessions;
	}

	public WebswingConfiguration getConfiguration() {
		return configuration;
	}

	public void setConfiguration(WebswingConfiguration configuration) {
		this.configuration = configuration;
	}

	public WebswingConfigurationBackup getConfigurationBackup() {
		return configurationBackup;
	}

	public void setConfigurationBackup(WebswingConfigurationBackup configurationBackup) {
		this.configurationBackup = configurationBackup;
	}

	public List<SwingSessionMsg> getClosedSessions() {
		return closedSessions;
	}

	public void setClosedSessions(List<SwingSessionMsg> closedSessions) {
		this.closedSessions = closedSessions;
	}

	public WebswingConfiguration getLiveConfiguration() {
		return liveConfiguration;
	}

	public void setLiveConfiguration(WebswingConfiguration liveConfiguration) {
		this.liveConfiguration = liveConfiguration;
	}

	public ServerPropertiesMsg getServerProperties() {
		return serverProperties;
	}

	public void setServerProperties(ServerPropertiesMsg serverProperties) {
		this.serverProperties = serverProperties;
	}

	public String getUserConfig() {
		return userConfig;
	}

	public void setUserConfig(String userConfig) {
		this.userConfig = userConfig;
	}

	public String getType() {
		return type;
	}

	public MessageMsg getMessage() {
		return message;
	}

	public void setMessage(MessageMsg message) {
		this.message = message;
	}

}
