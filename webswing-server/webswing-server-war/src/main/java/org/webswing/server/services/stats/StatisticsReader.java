package org.webswing.server.services.stats;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.webswing.server.services.stats.logger.Aggregation;
import org.webswing.server.services.stats.logger.InstanceStats;

public interface StatisticsReader {
	
	Map<String, List<Aggregation>> summaryRulesMap = new HashMap<>();

	Map<String, Map<String, BigDecimal>> getSummaryStats();

	Map<String,List<String>> getSummaryWarnings();

	/**
	 * @return Map &lt; name_of_metric, Map &lt; timestamp, value &gt; &gt;
	 */
	Map<String, ?> getInstanceStats(String instance);

	/**
	 * @return Map &lt; name_of_metric, value &gt;
	 */
	Map<String, ? extends Number> getInstanceMetrics(String clientId);

	List<String> getInstanceWarnings(String instance);

	List<String> getInstanceWarningHistory(String instance);
	
	Collection<InstanceStats> getAllInstanceStats();

}
