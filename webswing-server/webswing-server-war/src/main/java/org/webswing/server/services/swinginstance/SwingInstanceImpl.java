package org.webswing.server.services.swinginstance;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.webswing.Constants;
import org.webswing.model.MsgIn;
import org.webswing.model.MsgInternal;
import org.webswing.model.MsgOut;
import org.webswing.model.c2s.ConnectionHandshakeMsgIn;
import org.webswing.model.c2s.ParamMsg;
import org.webswing.model.c2s.SimpleEventMsgIn;
import org.webswing.model.c2s.SimpleEventMsgIn.SimpleEventType;
import org.webswing.model.c2s.TimestampsMsgIn;
import org.webswing.model.internal.ApiCallMsgInternal;
import org.webswing.model.internal.ApiEventMsgInternal;
import org.webswing.model.internal.ApiEventMsgInternal.ApiEventType;
import org.webswing.model.internal.ExitMsgInternal;
import org.webswing.model.internal.JvmStatsMsgInternal;
import org.webswing.model.internal.OpenFileResultMsgInternal;
import org.webswing.model.internal.PrinterJobResultMsgInternal;
import org.webswing.model.s2c.AppFrameMsgOut;
import org.webswing.model.s2c.LinkActionMsg;
import org.webswing.model.s2c.LinkActionMsg.LinkActionType;
import org.webswing.model.s2c.SimpleEventMsgOut;
import org.webswing.server.common.model.AppletLauncherConfig;
import org.webswing.server.common.model.DesktopLauncherConfig;
import org.webswing.server.common.model.SwingConfig;
import org.webswing.server.common.model.SwingConfig.LauncherType;
import org.webswing.server.common.model.admin.SwingInstanceStatus;
import org.webswing.server.common.model.admin.SwingSession;
import org.webswing.server.common.util.CommonUtil;
import org.webswing.server.common.util.VariableSubstitutor;
import org.webswing.server.model.EncodedMessage;
import org.webswing.server.model.exception.WsException;
import org.webswing.server.services.files.FileTransferHandler;
import org.webswing.server.services.jvmconnection.JvmConnection;
import org.webswing.server.services.jvmconnection.JvmConnectionService;
import org.webswing.server.services.jvmconnection.JvmListener;
import org.webswing.server.services.security.api.AbstractWebswingUser;
import org.webswing.server.services.stats.StatisticsLogger;
import org.webswing.server.services.swingmanager.SwingInstanceManager;
import org.webswing.server.services.swingprocess.ProcessExitListener;
import org.webswing.server.services.swingprocess.SwingProcess;
import org.webswing.server.services.swingprocess.SwingProcessConfig;
import org.webswing.server.services.swingprocess.SwingProcessService;
import org.webswing.server.services.websocket.WebSocketConnection;
import org.webswing.server.util.FontUtils;
import org.webswing.server.util.ServerUtil;
import org.webswing.toolkit.WebToolkit;
import org.webswing.toolkit.WebToolkit6;
import org.webswing.toolkit.WebToolkit7;
import org.webswing.toolkit.WebToolkit8;
import org.webswing.toolkit.api.WebswingApi;
import org.webswing.toolkit.ge.WebGraphicsEnvironment6;
import org.webswing.toolkit.ge.WebGraphicsEnvironment7;
import org.webswing.toolkit.ge.WebGraphicsEnvironment8;

import main.Main;

public class SwingInstanceImpl implements SwingInstance, JvmListener {
	private static final String LAUNCHER_CONFIG = "launcherConfig";

	private static final Logger log = LoggerFactory.getLogger(SwingInstance.class);

	private final SwingInstanceManager manager;
	private final FileTransferHandler fileHandler;
	private final String instanceId;
	private final String clientId;
	private String clientIp;
	private AbstractWebswingUser user;
	private SwingProcess process;
	private JvmConnection jvmConnection;
	private SessionRecorder sessionRecorder;
	private WebSocketConnection webConnection;
	private WebSocketConnection mirroredWebConnection;
	private SwingConfig config;
	private Date disconnectedSince;
	private final Date startedAt = new Date();
	private final String queueId;
	private Date endedAt = null;
	private String customArgs = "";
	private int debugPort = 0;

	public SwingInstanceImpl(SwingInstanceManager manager, FileTransferHandler fileHandler, SwingProcessService processService, JvmConnectionService connectionService, ConnectionHandshakeMsgIn h, SwingConfig config, WebSocketConnection websocket) throws WsException {
		this.manager = manager;
		this.fileHandler = fileHandler;
		this.webConnection = websocket;
		this.instanceId = ServerUtil.resolveInstanceIdForMode(websocket, h, config);
		this.config = config;
		this.user = websocket.getUser();
		this.clientId = h.getClientId();
		this.customArgs = ServerUtil.getCustomArgs(websocket.getRequest());
		this.debugPort = ServerUtil.getDebugPort(websocket.getRequest());
		this.clientIp = ServerUtil.getClientIp(websocket);
		this.queueId = user.getUserId() + "-" + config.getName() + "-" + startedAt.getTime();
		try {
			this.jvmConnection = connectionService.connect(this.queueId, this);
			process = start(processService, config, h);
			notifyUserConnected();
		} catch (Exception e) {
			notifyExiting();
			throw new WsException("Failed to create Swing instance.", e);
		}
		this.sessionRecorder = ServerUtil.isRecording(websocket.getRequest()) ? new SessionRecorder(this) : null;
		logStatValue(StatisticsLogger.WEBSOCKET_CONNECTED, websocket.isWebsocketTransport() ? 1 : 2);
	}

	public void connectSwingInstance(WebSocketConnection r, ConnectionHandshakeMsgIn h) {
		if (h.isMirrored()) {// connect as mirror viewer
			connectMirroredWebSession(r);
		} else {// continue old session?
			if (h.getSessionId() != null && h.getSessionId().equals(getSessionId())) {
				sendToSwing(r, h);
			} else {
				boolean result = connectPrimaryWebSession(r);
				if (result) {
					r.broadcastMessage(SimpleEventMsgOut.continueOldSession.buildMsgOut());
				} else {
					r.broadcastMessage(SimpleEventMsgOut.applicationAlreadyRunning.buildMsgOut());
				}
			}
		}
	}

	private boolean connectPrimaryWebSession(WebSocketConnection resource) {
		if (resource != null) {
			if (this.webConnection != null && config.isAllowStealSession()) {
				synchronized (this.webConnection) {
					this.webConnection.broadcastMessage(SimpleEventMsgOut.sessionStolenNotification.buildMsgOut());
				}
				notifyUserDisconnected();
				this.webConnection = null;
			}
			if (this.webConnection == null) {
				this.webConnection = resource;
				logStatValue(StatisticsLogger.WEBSOCKET_CONNECTED, resource.isWebsocketTransport() ? 1 : 2);
				this.disconnectedSince = null;
				notifyUserConnected();
				return true;
			}
		}
		return false;

	}

	private void disconnectPrimaryWebSession() {
		if (this.webConnection != null) {
			notifyUserDisconnected();
			this.webConnection = null;
			this.disconnectedSince = new Date();
			logStatValue(StatisticsLogger.WEBSOCKET_CONNECTED, 0);
		}
	}

	public void shutdown(boolean force) {
		if (force) {
			kill(0);
		} else {
			SimpleEventMsgIn simpleEventMsgIn = new SimpleEventMsgIn();
			simpleEventMsgIn.setType(SimpleEventType.killSwing);
			sendToSwing(null, simpleEventMsgIn);
		}
	}

	private void connectMirroredWebSession(WebSocketConnection resource) {
		if (resource != null) {
			if (this.mirroredWebConnection != null) {
				synchronized (this.mirroredWebConnection) {
					this.mirroredWebConnection.broadcastMessage(SimpleEventMsgOut.sessionStolenNotification.buildMsgOut());
				}
				notifyMirrorViewDisconnected();
			}
			this.mirroredWebConnection = resource;
			notifyMirrorViewConnected();
		}
	}

	private void disconnectMirroredWebSession() {
		if (this.mirroredWebConnection != null) {
			notifyMirrorViewDisconnected();
			this.mirroredWebConnection = null;
		}
	}

	public void sendToWeb(MsgOut o) {
		EncodedMessage serialized = new EncodedMessage(o);
		if (sessionRecorder != null) {
			sessionRecorder.saveFrame(serialized.getProtoMessage());
		}
		if (webConnection != null) {
			synchronized (webConnection) {
				webConnection.broadcastMessage(serialized);
				int length = webConnection.isBinary() ? serialized.getProtoMessage().length : serialized.getJsonMessage().getBytes().length;
				logStatValue(StatisticsLogger.OUTBOUND_SIZE_METRIC, length);
			}
		}
		if (mirroredWebConnection != null) {
			synchronized (mirroredWebConnection) {
				mirroredWebConnection.broadcastMessage(serialized);
			}
		}
	}

	public boolean sendToSwing(WebSocketConnection r, MsgIn h) {
		if (isRunning()) {
			if (h instanceof SimpleEventMsgIn) {
				SimpleEventMsgIn m = (SimpleEventMsgIn) h;
				if (m.getType().equals(SimpleEventMsgIn.SimpleEventType.paintAck)) {
					if (((webConnection != null && r.uuid().equals(webConnection.uuid())) || (webConnection == null && mirroredWebConnection != null && r.uuid().equals(mirroredWebConnection.uuid())))) {
						jvmConnection.send(h);
					}
				} else if (m.getType().equals(SimpleEventMsgIn.SimpleEventType.unload)) {
					if (webConnection != null && r.uuid().equals(webConnection.uuid())) {
						jvmConnection.send(h);
					}
					disconnectPrimaryWebSession();
					disconnectMirroredWebSession();
				} else {
					jvmConnection.send(h);
				}
			} else if (h instanceof TimestampsMsgIn) {
				processTimestampMessage((TimestampsMsgIn) h);
			} else {
				jvmConnection.send(h);
			}
			return true;
		} else {
			return false;
		}
	}

	private void processTimestampMessage(TimestampsMsgIn h) {
		if (StringUtils.isNotEmpty(h.getSendTimestamp())) {
			long currentTime = System.currentTimeMillis();
			long sendTime = Long.parseLong(h.getSendTimestamp());

			if (StringUtils.isNotEmpty(h.getRenderingTime()) && StringUtils.isNotEmpty(h.getSendTimestamp())) {
				long renderingTime = Long.parseLong(h.getRenderingTime());
				long startTime = Long.parseLong(h.getStartTimestamp());
				logStatValue(StatisticsLogger.LATENCY_SERVER_RENDERING, sendTime - startTime);
				logStatValue(StatisticsLogger.LATENCY_CLIENT_RENDERING, renderingTime);
				logStatValue(StatisticsLogger.LATENCY, currentTime - startTime);
				logStatValue(StatisticsLogger.LATENCY_NETWORK_TRANSFER, currentTime - sendTime - renderingTime);
			} else {
				logStatValue(StatisticsLogger.LATENCY_PING, currentTime - sendTime);
			}
		}
	}

	@Override
	public void onJvmMessage(Serializable o) {
		if (o instanceof MsgInternal) {
			if (o instanceof ApiCallMsgInternal) {
				ApiCallMsgInternal query = (ApiCallMsgInternal) o;
				AbstractWebswingUser currentUser = webConnection != null ? webConnection.getUser() : null;
				Serializable result;
				switch (query.getMethod()) {
				case HasRole:
					if (currentUser == null) {
						query.setResult(null);
					} else {
						result = currentUser.hasRole((String) query.getArgs()[0]);
						query.setResult(result);
					}
					jvmConnection.send(query);
					break;
				case IsPermitted:
					if (currentUser == null) {
						query.setResult(null);
					} else {
						result = currentUser.isPermitted((String) query.getArgs()[0]);
						query.setResult(result);
					}
					jvmConnection.send(query);
					break;
				default:
					break;
				}
			} else if (o instanceof PrinterJobResultMsgInternal) {
				PrinterJobResultMsgInternal pj = (PrinterJobResultMsgInternal) o;
				boolean success = fileHandler.registerFile(pj.getPdfFile(), pj.getId(), 30, TimeUnit.MINUTES, getUser(), getInstanceId(), false, null);
				if (success) {
					AppFrameMsgOut f = new AppFrameMsgOut();
					LinkActionMsg linkAction = new LinkActionMsg(LinkActionType.print, pj.getId());
					f.setLinkAction(linkAction);
					sendToWeb(f);
				}
			} else if (o instanceof OpenFileResultMsgInternal) {
				OpenFileResultMsgInternal fr = (OpenFileResultMsgInternal) o;
				String id = UUID.randomUUID().toString();
				boolean success = fileHandler.registerFile(fr.getFile(), id, 30, TimeUnit.MINUTES, getUser(), getInstanceId(), fr.isWaitForFile(), fr.getOverwriteDetails());
				if (success) {
					AppFrameMsgOut f = new AppFrameMsgOut();
					LinkActionMsg linkAction = new LinkActionMsg(LinkActionType.file, id);
					f.setLinkAction(linkAction);
					sendToWeb(f);
				}
			} else if (o instanceof JvmStatsMsgInternal) {
				JvmStatsMsgInternal s = (JvmStatsMsgInternal) o;
				logStatValue(StatisticsLogger.MEMORY_ALLOCATED_METRIC, s.getHeapSize());
				logStatValue(StatisticsLogger.MEMORY_USED_METRIC, s.getHeapSizeUsed());
				logStatValue(StatisticsLogger.CPU_UTIL_METRIC, s.getCpuUsage());
				//we use this event as trigger to sample websocket latency (simple ping):
				sendToWeb(AppFrameMsgOut.ping());
			} else if (o instanceof ExitMsgInternal) {
				close();
				ExitMsgInternal e = (ExitMsgInternal) o;
				kill(e.getWaitForExit());
			}
		} else if (o instanceof MsgOut) {
			sendToWeb((MsgOut) o);
		}
	}

	private void close() {
		if (config.isAutoLogout()) {
			sendToWeb(SimpleEventMsgOut.shutDownAutoLogoutNotification.buildMsgOut());
		} else {
			sendToWeb(SimpleEventMsgOut.shutDownNotification.buildMsgOut());
		}
		jvmConnection.close();
		notifyExiting();

		if (config.isIsolatedFs() && config.isClearTransferDir()) {
			String transferDir = process.getConfig().getProperties().get(Constants.SWING_START_SYS_PROP_TRANSFER_DIR);
			try {
				if (transferDir != null) {
					FileUtils.deleteDirectory(new File(transferDir));
				}
			} catch (IOException e) {
				log.error("Failed to delete transfer dir " + transferDir, e);
			}
		}
	}

	public void notifyExiting() {
		endedAt = new Date();
		if (isRunning()) {
			process.setProcessExitListener(null);
		}
		if (sessionRecorder != null) {
			sessionRecorder.close();
		}
		manager.notifySwingClose(this);
	}

	public SwingSession toSwingSession(boolean stats) {
		SwingSession session = new SwingSession();
		session.setId(getInstanceId());
		session.setApplet(LauncherType.Applet.equals(getAppConfig().getLauncherType()));
		session.setApplication(getAppConfig().getName());
		session.setConnected(getSessionId() != null);
		if (!session.getConnected()) {
			session.setDisconnectedSince(getDisconnectedSince());
		}
		session.setStartedAt(getStartedAt());
		session.setUser(getUser());
		session.setEndedAt(getEndedAt());
		session.setStatus(getStatus());
		if (stats) {
			session.setStats(manager.getInstanceStats(getClientId()));
		}
		session.setMetrics(manager.getInstanceMetrics(getClientId()));
		session.setWarnings(manager.getInstanceWarnings(getClientId()));
		session.setRecorded(isRecording());
		session.setRecordingFile(getRecordingFile());
		return session;
	}

	public void kill(int delayMs) {
		if (process != null) {
			process.destroy(delayMs);
		}
	}

	private SwingProcess start(SwingProcessService processService, final SwingConfig appConfig, final ConnectionHandshakeMsgIn handshake) throws Exception {
		final Integer screenWidth = handshake.getDesktopWidth();
		final Integer screenHeight = handshake.getDesktopHeight();
		final VariableSubstitutor subs = VariableSubstitutor.forSwingInstance(manager.getConfig(), user.getUserId(), getClientId(), clientIp, handshake.getLocale(), customArgs);
		SwingProcess swing = null;
		try {
			SwingProcessConfig swingConfig = new SwingProcessConfig();
			swingConfig.setName(getClientId());
			File homeDir = manager.resolveFile(subs.replace(appConfig.getUserDir()));
			swingConfig.setJreExecutable(subs.replace(appConfig.getJreExecutable()));
			swingConfig.setBaseDir(homeDir == null ? "." : homeDir.getAbsolutePath());
			swingConfig.setMainClass(Main.class.getName());
			swingConfig.setClassPath(new File(URI.create(CommonUtil.getWarFileLocation())).getAbsolutePath());
			String webSwingToolkitApiJarPath = getClassPathForClass(WebswingApi.class);
			String webSwingToolkitJarPath = getClassPathForClass(WebToolkit.class);
			String webSwingToolkitJarPathSpecific;
			String webToolkitClass;
			String webGraphicsEnvClass;
			String javaVersion = subs.replace(appConfig.getJavaVersion());
			if (javaVersion.startsWith("1.6")) {
				webSwingToolkitJarPathSpecific = getClassPathForClass(WebToolkit6.class);
				webToolkitClass = WebToolkit6.class.getCanonicalName();
				webGraphicsEnvClass = WebGraphicsEnvironment6.class.getCanonicalName();
			} else if (javaVersion.startsWith("1.7")) {
				webSwingToolkitJarPathSpecific = getClassPathForClass(WebToolkit7.class);
				webToolkitClass = WebToolkit7.class.getCanonicalName();
				webGraphicsEnvClass = WebGraphicsEnvironment7.class.getCanonicalName();
			} else if (javaVersion.startsWith("1.8")) {
				webSwingToolkitJarPathSpecific = getClassPathForClass(WebToolkit8.class);
				webToolkitClass = WebToolkit8.class.getCanonicalName();
				webGraphicsEnvClass = WebGraphicsEnvironment8.class.getCanonicalName();
			} else {
				log.error("Java version " + javaVersion + " not supported in this version of Webswing.");
				throw new RuntimeException("Java version not supported. (Version starting with 1.6 , 1.7 and 1.8 are supported.)");
			}
			String bootCp = "-Xbootclasspath/a:\"" + webSwingToolkitApiJarPath + "\"" + File.pathSeparatorChar + "\"" + webSwingToolkitJarPathSpecific + "\"" + File.pathSeparatorChar + "\"" + webSwingToolkitJarPath + "\"";

			if (!System.getProperty("os.name", "").startsWith("Windows")) {
				// filesystem isolation support on non windows systems:
				bootCp += File.pathSeparatorChar + "\"" + webSwingToolkitJarPath.substring(0, webSwingToolkitJarPath.lastIndexOf(File.separator)) + File.separator + "rt-win-shell.jar" + "\"";
			}
			String debug = appConfig.isDebug() && (debugPort != 0) ? " -Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,address=" + debugPort + ",server=y,suspend=y " : "";
			String vmArgs = appConfig.getVmArgs() == null ? "" : subs.replace(appConfig.getVmArgs());
			swingConfig.setJvmArgs(bootCp + debug + " -noverify " + vmArgs);
			swingConfig.addProperty(Constants.SWING_START_SYS_PROP_CLIENT_ID, getClientId());
			swingConfig.addProperty(Constants.SWING_START_SYS_PROP_JMS_ID, this.queueId);
			swingConfig.addProperty(Constants.SWING_START_SYS_PROP_CLASS_PATH, subs.replace(CommonUtil.generateClassPathString(appConfig.getClassPathEntries())));
			swingConfig.addProperty(Constants.TEMP_DIR_PATH, System.getProperty(Constants.TEMP_DIR_PATH));
			swingConfig.addProperty(Constants.JMS_URL, System.getProperty(Constants.JMS_URL, Constants.JMS_URL_DEFAULT));

			swingConfig.addProperty(Constants.SWING_START_SYS_PROP_THEME, subs.replace(appConfig.getTheme()));
			swingConfig.addProperty(Constants.SWING_START_SYS_PROP_ISOLATED_FS, appConfig.isIsolatedFs());
			swingConfig.addProperty(Constants.SWING_START_SYS_PROP_TRANSFER_DIR, subs.replace(appConfig.getTransferDir()));
			swingConfig.addProperty(Constants.SWING_START_SYS_PROP_ALLOW_DOWNLOAD, appConfig.isAllowDownload());
			swingConfig.addProperty(Constants.SWING_START_SYS_PROP_ALLOW_AUTO_DOWNLOAD, appConfig.isAllowAutoDownload());
			swingConfig.addProperty(Constants.SWING_START_SYS_PROP_ALLOW_UPLOAD, appConfig.isAllowUpload());
			swingConfig.addProperty(Constants.SWING_START_SYS_PROP_TRANSPARENT_FILE_OPEN, appConfig.isTransparentFileOpen());
			swingConfig.addProperty(Constants.SWING_START_SYS_PROP_TRANSPARENT_FILE_SAVE, appConfig.isTransparentFileSave());
			swingConfig.addProperty(Constants.SWING_START_SYS_PROP_ALLOW_DELETE, appConfig.isAllowDelete());
			swingConfig.addProperty(Constants.SWING_START_SYS_PROP_ALLOW_JSLINK, appConfig.isAllowJsLink());

			swingConfig.addProperty(Constants.SWING_START_SYS_PROP_DIRECTDRAW, appConfig.isDirectdraw());
			swingConfig.addProperty(Constants.SWING_START_SYS_PROP_DIRECTDRAW_SUPPORTED, handshake.isDirectDrawSupported());
			swingConfig.addProperty(Constants.SWING_SESSION_TIMEOUT_SEC, appConfig.getSwingSessionTimeout());
			swingConfig.addProperty("awt.toolkit", webToolkitClass);
			swingConfig.addProperty("java.awt.headless", "false");
			swingConfig.addProperty("java.awt.graphicsenv", webGraphicsEnvClass);
			swingConfig.addProperty("java.awt.printerjob", "org.webswing.toolkit.WebPrinterJob");
			swingConfig.addProperty("sun.awt.fontconfig", FontUtils.createFontConfiguration(appConfig, subs));
			swingConfig.addProperty(Constants.SWING_SCREEN_WIDTH, ((screenWidth == null) ? Constants.SWING_SCREEN_WIDTH_MIN : screenWidth));
			swingConfig.addProperty(Constants.SWING_SCREEN_HEIGHT, ((screenHeight == null) ? Constants.SWING_SCREEN_HEIGHT_MIN : screenHeight));

			switch (appConfig.getLauncherType()) {
			case Applet:
				AppletLauncherConfig applet = appConfig.getValueAs(LAUNCHER_CONFIG, AppletLauncherConfig.class);
				swingConfig.addProperty(Constants.SWING_START_SYS_PROP_APPLET_DOCUMENT_BASE, handshake.getDocumentBase());
				swingConfig.addProperty(Constants.SWING_START_SYS_PROP_APPLET_CLASS, applet.getAppletClass());
				for (String key : applet.getParameters().keySet()) {
					swingConfig.addProperty(Constants.SWING_START_STS_PROP_APPLET_PARAM_PREFIX + subs.replace(key), subs.replace(applet.getParameters().get(key)));
				}
				if (handshake.getParams() != null) {
					for (ParamMsg p : handshake.getParams()) {
						swingConfig.addProperty(Constants.SWING_START_STS_PROP_APPLET_PARAM_PREFIX + p.getName(), p.getValue());
					}
				}
				break;
			case Desktop:
				DesktopLauncherConfig desktop = appConfig.getValueAs(LAUNCHER_CONFIG, DesktopLauncherConfig.class);
				swingConfig.setArgs(subs.replace(desktop.getArgs()));
				swingConfig.addProperty(Constants.SWING_START_SYS_PROP_MAIN_CLASS, subs.replace(desktop.getMainClass()));
				break;
			default:
				throw new IllegalStateException("Launcher type not recognized.");
			}

			swing = processService.create(swingConfig);
			swing.execute();
			swing.setProcessExitListener(new ProcessExitListener() {

				@Override
				public void onClose() {
					close();
				}
			});
		} catch (Exception e1) {
			close();
			throw new Exception(e1);
		}
		return swing;
	}

	private String getClassPathForClass(Class<?> clazz) throws UnsupportedEncodingException {
		String cp = URLDecoder.decode(clazz.getProtectionDomain().getCodeSource().getLocation().getPath(), "UTF-8");
		if (cp.endsWith(clazz.getCanonicalName().replace(".", "/") + ".class")) {
			cp = cp.substring(0, cp.length() - (clazz.getCanonicalName().length() + 8));
		}
		return cp;
	}

	public String getClientId() {
		return clientId;
	}

	public SwingConfig getAppConfig() {
		return config;
	}

	public String getSessionId() {
		if (webConnection != null) {
			return webConnection.uuid();
		}
		return null;
	}

	public String getMirroredSessionId() {
		if (mirroredWebConnection != null) {
			return mirroredWebConnection.uuid();
		}
		return null;
	}

	public boolean isRunning() {
		return (process != null && process.isRunning());
	}

	public String getUser() {
		return user.getUserId();
	}

	public Date getDisconnectedSince() {
		return disconnectedSince;
	}

	public Date getStartedAt() {
		return startedAt;
	}

	public Date getEndedAt() {
		return endedAt;
	}

	public Boolean isRecording() {
		return sessionRecorder != null && !sessionRecorder.isFailed();
	}

	public String getRecordingFile() {
		return sessionRecorder != null ? sessionRecorder.getFileName() : null;
	}

	public String getInstanceId() {
		return instanceId;
	}

	public SwingInstanceStatus getStatus() {
		if (process == null) {
			return SwingInstanceStatus.NOT_STARTED;
		} else {
			if (isRunning()) {
				if (getEndedAt() == null) {
					return SwingInstanceStatus.RUNNING;
				} else {
					return SwingInstanceStatus.EXITING;
				}
			} else {
				if (process.isForceKilled()) {
					return SwingInstanceStatus.FORCE_KILLED;
				} else {
					return SwingInstanceStatus.FINISHED;
				}
			}
		}
	}

	@Override
	public void webSessionDisconnected(String connectionId) {
		if (getSessionId() != null && getSessionId().equals(connectionId)) {
			disconnectPrimaryWebSession();
		} else if (getMirroredSessionId() != null && getMirroredSessionId().equals(connectionId)) {
			disconnectMirroredWebSession();
		}
	}

	@Override
	public String getMirrorSessionId() {
		return mirroredWebConnection != null ? mirroredWebConnection.uuid() : null;
	}

	public void logStatValue(String name, Number value) {
		if (StringUtils.isNotEmpty(name)) {
			manager.logStatValue(getClientId(), name, value);
		}
	}

	private void notifyUserConnected() {
		sendUserApiEventMsg(ApiEventType.UserConnected, webConnection);
	}

	private void notifyUserDisconnected() {
		sendUserApiEventMsg(ApiEventType.UserDisconnected, webConnection);
	}

	private void notifyMirrorViewConnected() {
		sendUserApiEventMsg(ApiEventType.MirrorViewConnected, mirroredWebConnection);
	}

	private void notifyMirrorViewDisconnected() {
		sendUserApiEventMsg(ApiEventType.MirrorViewDisconnected, mirroredWebConnection);
	}

	private void sendUserApiEventMsg(ApiEventType type, WebSocketConnection r) {
		ApiEventMsgInternal event;
		if (r != null && r.getUser() != null) {
			AbstractWebswingUser connectedUser = r.getUser();
			event = new ApiEventMsgInternal(type, connectedUser.getUserId(), new HashMap<String, Serializable>(connectedUser.getUserAttributes()));
		} else {
			event = new ApiEventMsgInternal(type, null, null);
		}
		jvmConnection.send(event);
	}

}
