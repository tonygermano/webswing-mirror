package org.webswing.server.util;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.openmbean.CompositeData;

import org.webswing.model.admin.s2c.JsonSwingJvmStats;
import org.webswing.server.SwingInstance;

public class StatUtils {

    private static final int MB = 1024 * 1024;

    public static JsonSwingJvmStats getSwingInstanceStats(SwingInstance instance, MBeanServerConnection mBeanServerConnection) {
        JsonSwingJvmStats result = instance.getStats();
        resolveHeapMemory(mBeanServerConnection, result);
        return result;
    }

    private static void resolveHeapMemory(MBeanServerConnection mBeanServerConnection, JsonSwingJvmStats stats) {
        if (mBeanServerConnection != null) {
            try {
                Object o = mBeanServerConnection.getAttribute(new ObjectName("java.lang:type=Memory"), "HeapMemoryUsage");
                CompositeData cd = (CompositeData) o;
                stats.setHeapSize(Double.valueOf((Long) cd.get("max")) / MB);
                stats.setHeapSizeUsed(Double.valueOf((Long) cd.get("used")) / MB);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void logInboundData(SwingInstance instance, int length) {
        if (instance != null) {
            JsonSwingJvmStats result = instance.getStats();
            result.setInboundMsgCount(result.getInboundMsgCount() + 1);
            result.setInboundDataSizeSum(result.getInboundDataSizeSum() + length);
        }
    }

    public static void logOutboundData(SwingInstance instance, String msg) {
        if (instance != null) {
            JsonSwingJvmStats result = instance.getStats();
            result.setOutboundMsgCount(result.getOutboundMsgCount() + 1);
            result.setOutboundDataSizeSum(result.getOutboundDataSizeSum() + msg.getBytes().length);
        }
    }

}
