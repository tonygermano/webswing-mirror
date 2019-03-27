package org.webswing.server.services.stats;


public interface StatisticsLogger extends StatisticsReader{

	public static final String INBOUND_SIZE_METRIC = "inboundSize";
	public static final String OUTBOUND_SIZE_METRIC = "outboundSize";
	public static final String MEMORY_ALLOCATED_METRIC = "memoryAllocated";
	public static final String MEMORY_USED_METRIC = "memoryUsed";
	public static final String EDT_BLOCKED_SEC_METRIC = "edtThreadBlockedForSeconds";
	public static final String LATENCY_SERVER_RENDERING = "latencyServerRendering";
	public static final String LATENCY_CLIENT_RENDERING = "latencyClientRendering";
	public static final String LATENCY_NETWORK_TRANSFER = "latencyNetworkTransfer";
	public static final String LATENCY_PING = "latencyPing";
	public static final String LATENCY= "latency";
	public static final String CPU_UTIL_METRIC = "cpuUtilization"; // total CPU utilization, SESSION + SERVER
	public static final String CPU_UTIL_SESSION_METRIC = "cpuUtilizationSession";
	public static final String CPU_UTIL_SERVER_METRIC = "cpuUtilizationServer";
	public static final String WEBSOCKET_CONNECTED = "webSocketConnected";

	void log(String instance, String name, Number value);

	void removeInstance(String instance);

}
