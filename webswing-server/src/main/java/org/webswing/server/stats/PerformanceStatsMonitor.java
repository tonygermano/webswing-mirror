package org.webswing.server.stats;

import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.webswing.model.admin.s2c.JsonSwingJvmStats;
import org.webswing.server.SwingInstance;
import org.webswing.server.SwingInstanceManager;

public class PerformanceStatsMonitor {

	ScheduledExecutorService statsCollector = Executors.newSingleThreadScheduledExecutor();

	public PerformanceStatsMonitor() {
		Runnable collect = new Runnable() {

			@Override
			public void run() {
				try {
					for (SwingInstance si : SwingInstanceManager.getInstance().getSwingInstanceSet()) {
						JsonSwingJvmStats stats = si.collectStats();
						stats.setSnapshotTime(new Date());
						System.out.println(stats);
					}
					SwingInstanceManager.getInstance().notifySwingChangeChange();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		statsCollector.scheduleWithFixedDelay(collect, 5, 5, TimeUnit.SECONDS);
	}

}
