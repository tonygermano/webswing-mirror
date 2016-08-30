package org.webswing.server.services.stats.logger;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.webswing.server.services.stats.StatisticsLogger;

public class DefaultStatisticsLogger implements StatisticsLogger {

	private static final Logger log = LoggerFactory.getLogger(DefaultStatisticsLogger.class);
	private static final MetricRule DEFAULT_RULE_AVG = new MetricRule(Aggregation.AVG, TimeUnit.SECONDS.toMillis(10), 60);
	private static final MetricRule DEFAULT_RULE_AVG_PER_SEC = new MetricRule(Aggregation.AVG_PER_SEC, TimeUnit.SECONDS.toMillis(10), 60);
	private static final Map<String, MetricRule> rules = new HashMap<>();
	private static final Map<String, List<Aggregation>> summaryRulesMap = new HashMap<>();

	static {
		rules.put(INBOUND_SIZE_METRIC, DEFAULT_RULE_AVG_PER_SEC);
		rules.put(OUTBOUND_SIZE_METRIC, DEFAULT_RULE_AVG_PER_SEC);

		summaryRulesMap.put(MEMORY_ALLOCATED_METRIC, Arrays.asList(Aggregation.SUM));
		summaryRulesMap.put(MEMORY_USED_METRIC, Arrays.asList(Aggregation.SUM));
		summaryRulesMap.put(INBOUND_SIZE_METRIC, Arrays.asList(Aggregation.SUM));
		summaryRulesMap.put(OUTBOUND_SIZE_METRIC, Arrays.asList(Aggregation.SUM));
		summaryRulesMap.put(LATENCY_NETWORK, Arrays.asList(Aggregation.AVG));
		summaryRulesMap.put(LATENCY_CLIENT_RENDERING, Arrays.asList(Aggregation.AVG));
		summaryRulesMap.put(LATENCY_SERVER_RENDERING, Arrays.asList(Aggregation.AVG));
	}

	Map<String, InstanceStats> instanceMap = new HashMap<>();

	@Override
	public void log(String instance, String name, Number value) {
		log.trace("{},{},{}", new Object[] { instance, name, value });
		processMetric(instance, name, value);
	}

	public void processMetric(String instance, String name, Number value) {
		InstanceStats instanceStats = instanceMap.get(instance);
		if (instanceStats == null) {
			instanceStats = new InstanceStats();
			instanceMap.put(instance, instanceStats);
		}
		MetricRule rule = findRule(name);
		instanceStats.processMetric(rule, name, value);
	}

	private MetricRule findRule(String name) {
		MetricRule rule = rules.get(name);
		if (rule == null) {
			return DEFAULT_RULE_AVG;
		}
		return rule;
	}

	@Override
	public Map<String, Map<Long, Number>> getSummaryStats() {
		SummaryStats stats = new SummaryStats();
		for (String name : summaryRulesMap.keySet()) {
			List<Aggregation> summaryAggreg = summaryRulesMap.get(name);
			for (Aggregation aggregation : summaryAggreg) {
				stats.aggregate(instanceMap, name, aggregation);
			}
		}
		return stats.getStatistics();
	}

	@Override
	public Map<String, Map<Long, Number>> getInstanceStats(String instance) {
		InstanceStats stats = instanceMap.get(instance);
		if (stats != null) {
			return stats.getStatistics();
		}
		return null;
	}

	@Override
	public Map<String, Number> getInstanceMetrics(String instance) {
		InstanceStats stats = instanceMap.get(instance);
		if (stats != null) {
			return stats.getMetrics();
		}
		return null;
	}

	public void removeInstance(String instance) {
		instanceMap.remove(instance);
	}

}
