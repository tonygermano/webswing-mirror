package org.webswing.server.services.stats;

import java.util.List;
import java.util.Map;

public interface StatisticsReader {

	Map<String, Map<Long, Number>> getSummaryStats();

	Map<String,List<String>> getSummaryWarnings();

	/**
	 * @return Map &lt; name_of_metric, Map &lt; timestamp, value &gt; &gt;
	 */
	Map<String, Map<Long, Number>> getInstanceStats(String instance);

	/**
	 * @return Map &lt; name_of_metric, value &gt;
	 */
	Map<String, Number> getInstanceMetrics(String clientId);

	List<String> getInstanceWarnings(String instance);

	List<String> getInstanceWarningHistory(String instance);

}
