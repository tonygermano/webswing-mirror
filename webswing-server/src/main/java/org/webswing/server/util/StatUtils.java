package org.webswing.server.util;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.openmbean.CompositeData;

import org.webswing.model.server.admin.SwingJvmStats;
import org.webswing.server.SwingInstance;
import org.webswing.toolkit.util.Logger;

public class StatUtils {

	private static final int MB = 1024 * 1024;

	public static SwingJvmStats getSwingInstanceStats(SwingInstance instance, MBeanServerConnection mBeanServerConnection) {
		SwingJvmStats result = instance.getStats();
		resolveHeapMemory(mBeanServerConnection, result);
		return result;
	}

	private static void resolveHeapMemory(MBeanServerConnection mBeanServerConnection, SwingJvmStats stats) {
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
			SwingJvmStats result = instance.getStats();
			result.setInboundMsgCount(result.getInboundMsgCount() + 1);
			result.setInboundDataSizeSum(result.getInboundDataSizeSum() + length);
		}
	}

	public static void logOutboundData(SwingInstance instance, int length) {
		if (instance != null) {
			SwingJvmStats result = instance.getStats();
			result.setOutboundMsgCount(result.getOutboundMsgCount() + 1);
			result.setOutboundDataSizeSum(result.getOutboundDataSizeSum() + length);
		}
	}

}
