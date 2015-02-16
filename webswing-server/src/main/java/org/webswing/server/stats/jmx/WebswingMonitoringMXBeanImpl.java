package org.webswing.server.stats.jmx;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.webswing.model.admin.s2c.JsonAdminConsoleFrame;
import org.webswing.model.admin.s2c.JsonSwingSession;
import org.webswing.model.server.SwingApplicationDescriptor;
import org.webswing.server.ConfigurationManager;
import org.webswing.server.SwingInstanceManager;

public class WebswingMonitoringMXBeanImpl implements WebswingMonitoringMXBean {

	long lastsessionUpdate = -1;
	JsonAdminConsoleFrame sessionInfo;
	long lastconfigUpdate = -1;
	Map<String, SwingApplicationDescriptor> configInfo;

	@Override
	public int getNumberOfActiveSessions() {
		updateSessionInfo();
		return sessionInfo.getSessions().size();
	}

	@Override
	public int getNumberOfFinishedSessions() {
		updateSessionInfo();
		return sessionInfo.getClosedSessions().size();
	}

	@Override
	public List<SessionDetails> getSessionsDetails() {
		updateSessionInfo();
		List<SessionDetails> result = new ArrayList<SessionDetails>();
		for (JsonSwingSession ss : sessionInfo.getSessions()) {
			result.add(new SessionDetails(ss));
		}
		return result;
	}

	@Override
	public List<ApplicationConiguration> getApplicationConfigurations() {
		updateConfigInfo();
		List<ApplicationConiguration> result = new ArrayList<ApplicationConiguration>();
		for (SwingApplicationDescriptor a : configInfo.values()) {
			result.add(new ApplicationConiguration(a));
		}
		return result;
	}

	private void updateSessionInfo() {
		if (lastsessionUpdate == -1 || (System.currentTimeMillis() - lastsessionUpdate > 1000)) {
			sessionInfo = SwingInstanceManager.getInstance().extractStatus();
			lastsessionUpdate = System.currentTimeMillis();
		}
	}

	private void updateConfigInfo() {
		if (lastconfigUpdate == -1 || (System.currentTimeMillis() - lastconfigUpdate > 1000)) {
			configInfo = ConfigurationManager.getInstance().getApplications();
			lastconfigUpdate = System.currentTimeMillis();
		}
	}
}
