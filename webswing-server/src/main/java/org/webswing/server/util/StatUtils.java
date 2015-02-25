package org.webswing.server.util;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.openmbean.CompositeData;

import org.webswing.model.admin.s2c.SwingJvmStatsMsg;
import org.webswing.server.SwingInstance;
import org.webswing.util.Logger;

public class StatUtils {

	private static final int MB = 1024 * 1024;

	public static SwingJvmStatsMsg getSwingInstanceStats(SwingInstance instance, MBeanServerConnection mBeanServerConnection) {
		SwingJvmStatsMsg result = instance.getStats();
		resolveHeapMemory(mBeanServerConnection, result);
		return result;
	}

	private static void resolveHeapMemory(MBeanServerConnection mBeanServerConnection, SwingJvmStatsMsg stats) {
		if (mBeanServerConnection != null) {
			try {
				Object o = mBeanServerConnection.getAttribute(new ObjectName("java.lang:type=Memory"), "HeapMemoryUsage");
				CompositeData cd = (CompositeData) o;
				stats.setHeapSize(Double.valueOf((Long) cd.get("max")) / MB);
				stats.setHeapSizeUsed(Double.valueOf((Long) cd.get("used")) / MB);
			} catch (Exception e) {
				Logger.debug("Failed to read memory consumption stats.");
			}
		}
	}

	public static void logInboundData(SwingInstance instance, int length) {
		if (instance != null) {
			SwingJvmStatsMsg result = instance.getStats();
			result.setInboundMsgCount(result.getInboundMsgCount() + 1);
			result.setInboundDataSizeSum(result.getInboundDataSizeSum() + length);
		}
	}

	public static void logOutboundData(SwingInstance instance, int length) {
		if (instance != null) {
			SwingJvmStatsMsg result = instance.getStats();
			result.setOutboundMsgCount(result.getOutboundMsgCount() + 1);
			result.setOutboundDataSizeSum(result.getOutboundDataSizeSum() + length);
		}
	}

}
