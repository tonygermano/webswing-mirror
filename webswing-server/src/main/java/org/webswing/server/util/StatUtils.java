package org.webswing.server.util;

import org.webswing.model.server.admin.SwingJvmStats;
import org.webswing.server.SwingInstance;

public class StatUtils {

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
