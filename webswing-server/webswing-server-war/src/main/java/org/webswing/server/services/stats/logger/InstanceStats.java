package org.webswing.server.services.stats.logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class InstanceStats {
	private Map<String, Map<Long, Number>> statisticsLog = new HashMap<>();
	private Map<String, Long> lastTimestampMap = new HashMap<String, Long>();
	private Map<String, List<Number>> lastTimestampNumbers = new HashMap<String, List<Number>>();
	private Map<String, Number> lastMetrics = new HashMap<>();
	private Map<String, String> warnings = new HashMap<>();

	public void processMetric(MetricRule rule, String name, Number value) {
		//round timestamp to interval milis
		long timestamp = (System.currentTimeMillis() / rule.getInterval()) * rule.getInterval();

		//create map if null
		Map<Long, Number> valueMap = statisticsLog.get(name);
		if (valueMap == null) {
			valueMap = new LinkedHashMap<Long, Number>() {
				private static final long serialVersionUID = 3552039647099141391L;

				@Override
				protected boolean removeEldestEntry(Map.Entry<Long, Number> eldest) {
					long current = (System.currentTimeMillis() / rule.getInterval()) * rule.getInterval();
					long maxAge = rule.metricHistoryLimit * rule.getInterval();
					return eldest.getKey() < (current - maxAge);
				}
			};
			statisticsLog.put(name, valueMap);
		}

		//flush last timestamp entry if interval passed
		Long last = lastTimestampMap.get(name);
		if (last != null && last != timestamp && lastTimestampNumbers.get(name) != null) {
			List<Number> list = lastTimestampNumbers.remove(name);
			Number aggregated = calculateValue(rule, list);
			valueMap.put(last, aggregated);
			lastMetrics.put(name, aggregated);
		}

		//store current value to temp map
		lastTimestampMap.put(name, timestamp);
		if (lastTimestampNumbers.get(name) == null) {
			lastTimestampNumbers.put(name, new ArrayList<Number>());
		}
		lastTimestampNumbers.get(name).add(value);
	}

	private Number calculateValue(MetricRule rule, List<Number> list) {
		//store value
		Number result = 0;
		if (list != null && list.size() > 0) {
			for (Iterator<Number> iterator = list.iterator(); iterator.hasNext();) {
				Number number = iterator.next();
				switch (rule.getAggregation()) {
				case MIN:
					result = Math.min(number.doubleValue(), result.doubleValue());
					break;
				case MAX:
					result = Math.max(number.doubleValue(), result.doubleValue());
					break;
				case SUM:
				case AVG:
				default:
					result = result.doubleValue() + number.doubleValue();
					break;
				}
			}
			if (rule.getAggregation().equals(Aggregation.AVG)) {
				result = result.doubleValue() / list.size();
			}
			if (rule.getAggregation().equals(Aggregation.AVG_PER_SEC)) {
				result = result.doubleValue() / (rule.getInterval() / 1000);
			}
		}
		return result;
	}

	public Map<String, Number> getMetrics() {
		List<Aggregation> aggregations = Arrays.asList(Aggregation.MIN, Aggregation.MAX, Aggregation.AVG);
		Map<String, Number> metrics = new HashMap<>(lastMetrics);
		MetricRule rule = new MetricRule(Aggregation.MIN, 0, 0);
		for (String name : statisticsLog.keySet()) {
			List<Number> valueList = new ArrayList<>(statisticsLog.get(name).values());
			for (Aggregation a : aggregations) {
				rule.setAggregation(a);
				metrics.put(name + "." + a, calculateValue(rule, valueList));
			}
		}
		return metrics;
	}

	public Map<String, Map<Long, Number>> getStatistics() {
		return statisticsLog;
	}
}