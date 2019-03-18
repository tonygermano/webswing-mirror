package org.webswing.server.services.stats;

import java.lang.management.ManagementFactory;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import org.webswing.server.model.exception.WsInitException;
import org.webswing.server.services.stats.logger.DefaultStatisticsLogger;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class StatisticsLoggerServiceImpl implements StatisticsLoggerService{
	
	public static final String SERVER_LOGGER_INSTANCE_NAME = "server";
	private static final long SERVER_LOGGER_PERIOD = 1000L;
	
	private Timer timer;
	
	private StatisticsLogger serverLogger = new DefaultStatisticsLogger();

	@Inject
	public StatisticsLoggerServiceImpl() {
	}
	
	@Override
	public void start() throws WsInitException {
		timer = new Timer(true);
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				double cpuUsage = CpuMonitor.getCpuUtilization();
				getServerLogger().log(SERVER_LOGGER_INSTANCE_NAME, StatisticsLogger.CPU_UTIL_METRIC, cpuUsage);
				getServerLogger().log(SERVER_LOGGER_INSTANCE_NAME, StatisticsLogger.CPU_UTIL_SERVER_METRIC, cpuUsage);
			}
		}, SERVER_LOGGER_PERIOD, SERVER_LOGGER_PERIOD);
	}

	@Override
	public void stop() {
		timer.cancel();
	}
	
	public StatisticsLogger createLogger(){
		return new DefaultStatisticsLogger();
	}
	
	@Override
	public StatisticsLogger getServerLogger() {
		return serverLogger;
	}
	
	private static class CpuMonitor {
		static long previousCPUTime = 0;
		static long previousTime = 0;

		static {
			getCpuUtilization();
		}

		static double getCpuUtilization() {
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
		}
	}

}
