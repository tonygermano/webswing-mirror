package org.webswing.server.util;

import java.util.List;
import java.util.Map;

import org.webswing.server.services.stats.StatisticsReader;
import org.webswing.server.services.stats.logger.Aggregation;
import org.webswing.server.services.stats.logger.InstanceStats;
import org.webswing.server.services.stats.logger.SummaryStats;

public class LoggerStatisticsUtil {

	public static Map<String, Map<Long, Number>> mergeSummaryInstanceStats(List<InstanceStats> statsList) {
		SummaryStats stats = new SummaryStats();
		for (String name : StatisticsReader.summaryRulesMap.keySet()) {
			List<Aggregation> summaryAggreg = StatisticsReader.summaryRulesMap.get(name);
			for (Aggregation aggregation : summaryAggreg) {
				stats.aggregate(statsList, name, aggregation);
			}
		}
		return stats.getStatistics();
	}
	
}