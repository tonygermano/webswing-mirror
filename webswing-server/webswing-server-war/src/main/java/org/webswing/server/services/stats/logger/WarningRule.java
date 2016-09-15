package org.webswing.server.services.stats.logger;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.webswing.server.services.stats.StatisticsLogger;

public abstract class WarningRule {

	private static final DateFormat format = new SimpleDateFormat("dd MMM HH:mm:ss");

	public abstract String checkWarning(Map<String, Number> lastMetrics);

	public static WarningRule memoryUtilizationRule(final double threshold) {
		return new WarningRule() {
			@Override
			public String checkWarning(Map<String, Number> lastMetrics) {
				Number allocated = lastMetrics.get(StatisticsLogger.MEMORY_ALLOCATED_METRIC);
				Number used = lastMetrics.get(StatisticsLogger.MEMORY_USED_METRIC);
				if (allocated != null && allocated.doubleValue() != 0 && used != null) {
					double utilization = used.doubleValue() / allocated.doubleValue();
					if (utilization >= threshold) {
						return warning(StatisticsLogger.MEMORY_USED_METRIC, "Utilization is too high!");
					}
				}
				return null;
			}
		};
	}

	public static WarningRule thresholdRule(final String metric, final double threshold) {
		return WarningRule.thresholdRule(metric, threshold, "Value is too high");
	}

	public static WarningRule thresholdRule(final String metric, final double threshold, final String msg) {
		return new WarningRule() {
			@Override
			public String checkWarning(Map<String, Number> lastMetrics) {
				Number value = lastMetrics.get(metric);
				if (metric != null) {
					if (value.doubleValue() >= threshold) {
						return warning(metric, msg);
					}
				}
				return null;
			}
		};
	}

	private static String warning(String metric, String message) {
		return "WARNING (" + format.format(new Date()) + ") " + metric + ": " + message;
	}
}
