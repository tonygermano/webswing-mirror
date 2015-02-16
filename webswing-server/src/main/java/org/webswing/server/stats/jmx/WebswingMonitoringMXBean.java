package org.webswing.server.stats.jmx;

import java.util.List;

public interface WebswingMonitoringMXBean {

	int getNumberOfActiveSessions();

	int getNumberOfFinishedSessions();

	List<SessionDetails> getSessionsDetails();

	List<ApplicationConiguration> getApplicationConfigurations();

}
