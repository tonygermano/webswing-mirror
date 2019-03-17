package org.webswing.server.services.stats;

import org.webswing.server.base.WebswingService;

public interface StatisticsLoggerService extends WebswingService {

	StatisticsLogger createLogger();
	
	StatisticsLogger getServerLogger();
	
}
