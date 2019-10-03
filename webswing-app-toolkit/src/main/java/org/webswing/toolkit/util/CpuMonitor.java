package org.webswing.toolkit.util;

import java.lang.management.ManagementFactory;
import java.util.concurrent.TimeUnit;

public class CpuMonitor {
	static long previousCPUTime = 0;
	static long previousTime = 0;

	static {
		getCpuUtilization();
	}

	public static double getCpuUtilization() {
		try {
			com.sun.management.OperatingSystemMXBean operatingSystemMXBean = (com.sun.management.OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
			long currentCpuTime = operatingSystemMXBean.getProcessCpuTime();
			
			long now = ManagementFactory.getRuntimeMXBean().getUptime();
			
			long cpuTimeDelta = currentCpuTime - previousCPUTime;
			previousCPUTime = currentCpuTime;
			
			long timeDelta = now - previousTime;
			previousTime = now;
			
			int processors = Runtime.getRuntime().availableProcessors();
			if (timeDelta == 0 || processors == 0) {
				return 0;
			}
			double cpuUsage = (double) TimeUnit.NANOSECONDS.toMillis(cpuTimeDelta) / (double) timeDelta;
			cpuUsage = cpuUsage / processors;
			return Math.max(0, cpuUsage) * 100;
		} catch (NoClassDefFoundError e) {
			Logger.warn("Class not found to monitor CPU utilization. Monitoring CPU disabled.", e);
		}
		
		return 0;
	}
}
