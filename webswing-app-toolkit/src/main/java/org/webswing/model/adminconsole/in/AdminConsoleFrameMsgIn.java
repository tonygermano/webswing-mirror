package org.webswing.model.adminconsole.in;

import org.webswing.model.MsgIn;

public class AdminConsoleFrameMsgIn implements MsgIn {

	private static final long serialVersionUID = 3253892297375570414L;

	private AdminConsoleHandshakeMsgIn handshake;
	private ToggleRecordingMsgIn toggleRecording;
	private ShutdownMsgIn shutdown;
	private RequestThreadDumpMsgIn requestThreadDump;
	private ToggleStatisticsLoggingMsgIn toggleStatisticsLogging;
	private GetThreadDumpMsgIn getThreadDump;
	private GetInstanceCountsStatsWarningsMsgIn getInstanceCountsStatsWarnings;
	private GetSwingSessionsMsgIn getSwingSessions;
	private GetServerInfoMsgIn getServerInfo;
	private GetConfigMsgIn getConfig;
	private GetMetaMsgIn getMeta;
	private ResolveConfigMsgIn resolveConfig;
	private SaveConfigMsgIn saveConfig;
	private SearchVariablesMsgIn searchVariables;
	private StartAppMsgIn startApp;
	private StopAppMsgIn stopApp;
	private CreateAppMsgIn createApp;
	private RemoveAppMsgIn removeApp;
	
	public AdminConsoleHandshakeMsgIn getHandshake() {
		return handshake;
	}

	public void setHandshake(AdminConsoleHandshakeMsgIn handshake) {
		this.handshake = handshake;
	}

	public ToggleRecordingMsgIn getToggleRecording() {
		return toggleRecording;
	}

	public void setToggleRecording(ToggleRecordingMsgIn toggleRecording) {
		this.toggleRecording = toggleRecording;
	}

	public ShutdownMsgIn getShutdown() {
		return shutdown;
	}

	public void setShutdown(ShutdownMsgIn shutdown) {
		this.shutdown = shutdown;
	}

	public RequestThreadDumpMsgIn getRequestThreadDump() {
		return requestThreadDump;
	}

	public void setRequestThreadDump(RequestThreadDumpMsgIn requestThreadDump) {
		this.requestThreadDump = requestThreadDump;
	}

	public GetThreadDumpMsgIn getGetThreadDump() {
		return getThreadDump;
	}

	public void setGetThreadDump(GetThreadDumpMsgIn getThreadDump) {
		this.getThreadDump = getThreadDump;
	}

	public GetInstanceCountsStatsWarningsMsgIn getGetInstanceCountsStatsWarnings() {
		return getInstanceCountsStatsWarnings;
	}

	public void setGetInstanceCountsStatsWarnings(GetInstanceCountsStatsWarningsMsgIn getInstanceCountsStatsWarnings) {
		this.getInstanceCountsStatsWarnings = getInstanceCountsStatsWarnings;
	}

	public GetSwingSessionsMsgIn getGetSwingSessions() {
		return getSwingSessions;
	}

	public void setGetSwingSessions(GetSwingSessionsMsgIn getSwingSessions) {
		this.getSwingSessions = getSwingSessions;
	}

	public GetServerInfoMsgIn getGetServerInfo() {
		return getServerInfo;
	}

	public void setGetServerInfo(GetServerInfoMsgIn getServerInfo) {
		this.getServerInfo = getServerInfo;
	}

	public ToggleStatisticsLoggingMsgIn getToggleStatisticsLogging() {
		return toggleStatisticsLogging;
	}

	public void setToggleStatisticsLogging(ToggleStatisticsLoggingMsgIn toggleStatisticsLogging) {
		this.toggleStatisticsLogging = toggleStatisticsLogging;
	}

	public GetConfigMsgIn getGetConfig() {
		return getConfig;
	}

	public void setGetConfig(GetConfigMsgIn getConfig) {
		this.getConfig = getConfig;
	}

	public GetMetaMsgIn getGetMeta() {
		return getMeta;
	}

	public void setGetMeta(GetMetaMsgIn getMeta) {
		this.getMeta = getMeta;
	}

	public ResolveConfigMsgIn getResolveConfig() {
		return resolveConfig;
	}

	public void setResolveConfig(ResolveConfigMsgIn resolveConfig) {
		this.resolveConfig = resolveConfig;
	}

	public SaveConfigMsgIn getSaveConfig() {
		return saveConfig;
	}

	public void setSaveConfig(SaveConfigMsgIn saveConfig) {
		this.saveConfig = saveConfig;
	}

	public SearchVariablesMsgIn getSearchVariables() {
		return searchVariables;
	}

	public void setSearchVariables(SearchVariablesMsgIn searchVariables) {
		this.searchVariables = searchVariables;
	}

	public StartAppMsgIn getStartApp() {
		return startApp;
	}

	public void setStartApp(StartAppMsgIn startApp) {
		this.startApp = startApp;
	}

	public StopAppMsgIn getStopApp() {
		return stopApp;
	}

	public void setStopApp(StopAppMsgIn stopApp) {
		this.stopApp = stopApp;
	}

	public CreateAppMsgIn getCreateApp() {
		return createApp;
	}

	public void setCreateApp(CreateAppMsgIn createApp) {
		this.createApp = createApp;
	}

	public RemoveAppMsgIn getRemoveApp() {
		return removeApp;
	}

	public void setRemoveApp(RemoveAppMsgIn removeApp) {
		this.removeApp = removeApp;
	}

}
