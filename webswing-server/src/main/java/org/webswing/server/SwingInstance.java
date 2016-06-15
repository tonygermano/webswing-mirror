package org.webswing.server;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.util.Date;

import org.apache.commons.lang.text.StrSubstitutor;
import org.atmosphere.cpr.AtmosphereResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.webswing.Constants;
import org.webswing.model.MsgIn;
import org.webswing.model.MsgOut;
import org.webswing.model.c2s.ConnectionHandshakeMsgIn;
import org.webswing.model.c2s.ParamMsg;
import org.webswing.model.c2s.SimpleEventMsgIn;
import org.webswing.model.c2s.SimpleEventMsgIn.SimpleEventType;
import org.webswing.model.s2c.SimpleEventMsgOut;
import org.webswing.model.server.SwingAppletDescriptor;
import org.webswing.model.server.SwingApplicationDescriptor;
import org.webswing.model.server.SwingDescriptor;
import org.webswing.model.server.admin.SwingInstanceStatus;
import org.webswing.model.server.admin.SwingJvmStats;
import org.webswing.model.server.admin.SwingSession;
import org.webswing.server.SwingJvmConnection.WebSessionListener;
import org.webswing.server.handler.JmsService;
import org.webswing.server.model.EncodedMessage;
import org.webswing.server.recording.SessionRecorder;
import org.webswing.server.util.ServerUtil;
import org.webswing.server.util.StatUtils;
import org.webswing.server.util.exec.SwingProcess;
import org.webswing.toolkit.WebToolkit;
import org.webswing.toolkit.WebToolkit6;
import org.webswing.toolkit.WebToolkit7;
import org.webswing.toolkit.WebToolkit8;

import main.Main;

public class SwingInstance implements WebSessionListener {
	private static final Logger log = LoggerFactory.getLogger(SwingInstance.class);

	private SwingProcess app;
	private String instanceId;
	private String clientId;
	private String user;
	private String clientIp;
	private AtmosphereResource resource;
	private AtmosphereResource mirroredResource;
	private SwingDescriptor application;
	private SwingJvmConnection connection;
	private Date disconnectedSince;
	private SessionRecorder sessionRecorder;
	private final Date startedAt = new Date();
	private Date endedAt = null;
	private String customArgs = "";
	private int debugPort = 0;

	public SwingInstance(String instanceId, ConnectionHandshakeMsgIn h, SwingDescriptor config, AtmosphereResource resource) throws Exception {
		this.instanceId = instanceId;
		this.application = config;
		this.user = ServerUtil.getUserName(resource);
		this.clientId = h.getClientId();
		this.customArgs = ServerUtil.getCustomArgs(resource.getRequest());
		this.debugPort = ServerUtil.getDebugPort(resource.getRequest());
		this.clientIp = ServerUtil.getClientIp(resource.getRequest());
		try {
			this.connection = new SwingJvmConnection(clientId, this);
			app = start(config, h);
		} catch (Exception e) {
			notifyExiting();
			throw new Exception(e);
		}
		connectPrimaryWebSession(resource);
		this.sessionRecorder = ServerUtil.isRecording(resource.getRequest()) ? new SessionRecorder(this) : null;
	}

	public boolean connectPrimaryWebSession(AtmosphereResource resource) {
		if (resource != null) {
			if (this.resource != null && application.isAllowStealSession()) {
				synchronized (this.resource) {
					ServerUtil.broadcastMessage(this.resource, new EncodedMessage(SimpleEventMsgOut.sessionStolenNotification.buildMsgOut()));
				}
				this.resource = null;
			}
			if (this.resource == null) {
				this.resource = resource;
				this.disconnectedSince = null;
				return true;
			}
		}
		return false;

	}

	public void disconnectPrimaryWebSession() {
		if (this.resource != null) {
			this.resource = null;
			this.disconnectedSince = new Date();
			SwingInstanceManager.getInstance().notifySwingInstanceChanged();
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

	public void connectMirroredWebSession(AtmosphereResource resource) {
		if (resource != null) {
			if (this.mirroredResource != null) {
				synchronized (this.mirroredResource) {
					ServerUtil.broadcastMessage(this.mirroredResource, new EncodedMessage(SimpleEventMsgOut.sessionStolenNotification.buildMsgOut()));
				}
			}
			this.mirroredResource = resource;
		}
	}

	public void disconnectMirroredWebSession() {
		SwingInstance.this.mirroredResource = null;
	}

	public void sendToWeb(MsgOut o) {
		EncodedMessage serialized = new EncodedMessage(o);
		if (sessionRecorder != null) {
			sessionRecorder.saveFrame(serialized.getProtoMessage());
		}
		if (resource != null) {
			synchronized (resource) {
				ServerUtil.broadcastMessage(resource, serialized);
				int length = resource.forceBinaryWrite() ? serialized.getProtoMessage().length : serialized.getJsonMessage().getBytes().length;
				StatUtils.logOutboundData(this, length);
			}
		}
		if (mirroredResource != null) {
			synchronized (mirroredResource) {
				ServerUtil.broadcastMessage(mirroredResource, serialized);
			}
		}
	}

	public boolean sendToSwing(AtmosphereResource r, MsgIn h) {
		if (isRunning()) {
			if (h instanceof SimpleEventMsgIn) {
				SimpleEventMsgIn m = (SimpleEventMsgIn) h;
				if (m.getType().equals(SimpleEventMsgIn.SimpleEventType.paintAck)) {
					if (((resource != null && r.uuid().equals(resource.uuid())) || (resource == null && mirroredResource != null && r.uuid().equals(mirroredResource.uuid())))) {
						connection.send(h);
					}
				} else if (m.getType().equals(SimpleEventMsgIn.SimpleEventType.unload)) {
					if (resource != null && r.uuid().equals(resource.uuid())) {
						connection.send(h);
					}
					SwingInstanceManager.getInstance().notifySessionDisconnected(r.uuid());
				} else {
					connection.send(h);
				}
			} else {
				connection.send(h);
			}
			return true;
		} else {
			return false;
		}
	}

	public void notifyExiting() {
		endedAt = new Date();
		if (isRunning()) {
			app.setCloseListener(null);
		}
		if (sessionRecorder != null) {
			sessionRecorder.close();
		}
		SwingInstanceManager.getInstance().notifySwingClose(this);
	}

	public SwingSession toSwingSession() {
		SwingSession session = new SwingSession();
		session.setId(getClientId());
		session.setApplet(getAppConfig() instanceof SwingAppletDescriptor);
		session.setApplication(getAppConfig().getName());
		session.setConnected(getSessionId() != null);
		if (!session.getConnected()) {
			session.setDisconnectedSince(getDisconnectedSince());
		}
		session.setStartedAt(getStartedAt());
		session.setUser(getUser());
		session.setEndedAt(getEndedAt());
		session.setStatus(getStatus());
		session.setState(getStats());
		session.setRecorded(isRecording());
		session.setRecordingFile(getRecordingFile());
		return session;
	}

	public void kill(int delayMs) {
		if (app != null) {
			app.destroy(delayMs);
		}
	}

	private SwingProcess start(final SwingDescriptor appConfig, final ConnectionHandshakeMsgIn handshake) throws Exception {
		final Integer screenWidth = handshake.getDesktopWidth();
		final Integer screenHeight = handshake.getDesktopHeight();
		final StrSubstitutor subs = ServerUtil.getConfigSubstitutor(user, clientId, clientIp, handshake.getLocale(), customArgs);
		SwingProcess swing = null;
		try {
			swing = new SwingProcess(clientId);
			File homeDir = getHomeDir(appConfig, subs);
			swing.setJreExecutable(subs.replace(appConfig.getJreExecutable()));
			swing.setBaseDir(homeDir.getAbsolutePath());
			swing.setMainClass(Main.class.getName());
			swing.setClassPath(new File(URI.create(ServerUtil.getWarFileLocation())).getAbsolutePath());
			String webSwingToolkitJarPath = getClassPathForClass(WebToolkit.class);
			String webSwingToolkitJarPathSpecific;
			String webToolkitClass;
			String javaVersion = subs.replace(appConfig.getJavaVersion());
			if (javaVersion.startsWith("1.6")) {
				webSwingToolkitJarPathSpecific = getClassPathForClass(WebToolkit6.class);
				webToolkitClass = WebToolkit6.class.getCanonicalName();
			} else if (javaVersion.startsWith("1.7")) {
				webSwingToolkitJarPathSpecific = getClassPathForClass(WebToolkit7.class);
				webToolkitClass = WebToolkit7.class.getCanonicalName();
			} else if (javaVersion.startsWith("1.8")) {
				webSwingToolkitJarPathSpecific = getClassPathForClass(WebToolkit8.class);
				webToolkitClass = WebToolkit8.class.getCanonicalName();
			} else {
				log.error("Java version " + javaVersion + " not supported in this version of Webswing.");
				throw new RuntimeException("Java version not supported. (Version starting with 1.6 , 1.7 and 1.8 are supported.)");
			}
			String bootCp = "-Xbootclasspath/a:\"" + webSwingToolkitJarPathSpecific + "\"" + File.pathSeparatorChar + "\"" + webSwingToolkitJarPath + "\"";

			if (!System.getProperty("os.name", "").startsWith("Windows")) {
				// filesystem isolation support on non windows systems:
				bootCp += File.pathSeparatorChar + "\"" + webSwingToolkitJarPath.substring(0, webSwingToolkitJarPath.lastIndexOf(File.separator)) + File.separator + "rt-win-shell.jar" + "\"";
			}
			String debug = appConfig.isDebug() && (debugPort != 0) ? " -Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,address=" + debugPort + ",server=y,suspend=y " : "";
			String aaFonts = appConfig.isAntiAliasText() ? " -Dawt.useSystemAAFontSettings=on -Dswing.aatext=true " : "";
			swing.setJvmArgs(bootCp + debug + aaFonts + " -noverify " + subs.replace(appConfig.getVmArgs()));
			swing.addProperty(Constants.SWING_START_SYS_PROP_CLIENT_ID, clientId);
			swing.addProperty(Constants.SWING_START_SYS_PROP_CLASS_PATH, subs.replace(appConfig.generateClassPathString()));
			swing.addProperty(Constants.TEMP_DIR_PATH, System.getProperty(Constants.TEMP_DIR_PATH));
			swing.addProperty(Constants.JMS_URL, JmsService.getUrl());

			swing.addProperty(Constants.SWING_START_SYS_PROP_THEME, subs.replace(appConfig.getTheme()));
			swing.addProperty(Constants.SWING_START_SYS_PROP_ISOLATED_FS, appConfig.isIsolatedFs() + "");
			swing.addProperty(Constants.SWING_START_SYS_PROP_ALLOW_DOWNLOAD, appConfig.isAllowDownload() + "");
			swing.addProperty(Constants.SWING_START_SYS_PROP_ALLOW_AUTO_DOWNLOAD, appConfig.isAllowAutoDownload() + "");
			swing.addProperty(Constants.SWING_START_SYS_PROP_ALLOW_UPLOAD, appConfig.isAllowUpload() + "");
			swing.addProperty(Constants.SWING_START_SYS_PROP_ALLOW_DELETE, appConfig.isAllowDelete() + "");
			swing.addProperty(Constants.SWING_START_SYS_PROP_ALLOW_JSLINK, appConfig.isAllowJsLink() + "");

			swing.addProperty(Constants.SWING_START_SYS_PROP_DIRECTDRAW, appConfig.isDirectdraw() + "");
			swing.addProperty(Constants.SWING_START_SYS_PROP_DIRECTDRAW_SUPPORTED, handshake.isDirectDrawSupported() + "");
			swing.addProperty(Constants.SWING_SESSION_TIMEOUT_SEC, appConfig.resolveSwingSessionTimeout() + "");
			swing.addProperty("awt.toolkit", webToolkitClass);
			swing.addProperty("java.awt.headless", "false");
			swing.addProperty("java.awt.graphicsenv", "org.webswing.toolkit.ge.WebGraphicsEnvironment");
			swing.addProperty("java.awt.printerjob", "org.webswing.toolkit.WebPrinterJob");
			swing.addProperty(Constants.SWING_SCREEN_WIDTH, ((screenWidth == null) ? Constants.SWING_SCREEN_WIDTH_MIN : screenWidth) + "");
			swing.addProperty(Constants.SWING_SCREEN_HEIGHT, ((screenHeight == null) ? Constants.SWING_SCREEN_HEIGHT_MIN : screenHeight) + "");

			if (appConfig instanceof SwingApplicationDescriptor) {
				SwingApplicationDescriptor application = (SwingApplicationDescriptor) appConfig;
				swing.setArgs(subs.replace(application.getArgs()));
				swing.addProperty(Constants.SWING_START_SYS_PROP_MAIN_CLASS, subs.replace(application.getMainClass()));
			} else if (appConfig instanceof SwingAppletDescriptor) {
				SwingAppletDescriptor applet = (SwingAppletDescriptor) appConfig;
				swing.addProperty(Constants.SWING_START_SYS_PROP_APPLET_DOCUMENT_BASE, handshake.getDocumentBase());
				swing.addProperty(Constants.SWING_START_SYS_PROP_APPLET_CLASS, applet.getAppletClass());
				for (String key : applet.getParameters().keySet()) {
					swing.addProperty(Constants.SWING_START_STS_PROP_APPLET_PARAM_PREFIX + subs.replace(key), subs.replace(applet.getParameters().get(key)));
				}
				if (handshake.getParams() != null) {
					for (ParamMsg p : handshake.getParams()) {
						swing.addProperty(Constants.SWING_START_STS_PROP_APPLET_PARAM_PREFIX + p.getName(), p.getValue());
					}
				}
			}
			swing.execute();
			swing.setCloseListener(new SwingProcess.CloseListener() {

				@Override
				public void onClose() {
					connection.close();
				}
			});
		} catch (Exception e1) {
			connection.close();
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

	private File getHomeDir(final SwingDescriptor appConfig, StrSubstitutor subs) {
		String dirString = subs.replace(appConfig.getHomeDir());
		File homeDir;
		if (dirString.startsWith("/") || dirString.startsWith("\\") || dirString.contains(":/") || dirString.contains(":\\")) {
			// path is absolute
			homeDir = new File(dirString);
		} else {
			// path is relative
			homeDir = new File(Main.getRootDir(), dirString);
		}
		if (!homeDir.exists()) {
			homeDir.mkdirs();
		}
		return homeDir;
	}

	public String getClientId() {
		return clientId;
	}

	public SwingDescriptor getAppConfig() {
		return application;
	}

	public String getSessionId() {
		if (resource != null) {
			return resource.uuid();
		}
		return null;
	}

	public String getMirroredSessionId() {
		if (mirroredResource != null) {
			return mirroredResource.uuid();
		}
		return null;
	}

	public boolean isRunning() {
		return (app != null && app.isRunning());
	}

	public String getUser() {
		return user;
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

	public SwingJvmStats getStats() {
		return connection.getLatest();
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
		if (app == null) {
			return SwingInstanceStatus.NOT_STARTED;
		} else {
			if (isRunning()) {
				if (getEndedAt() == null) {
					return SwingInstanceStatus.RUNNING;
				} else {
					return SwingInstanceStatus.EXITING;
				}
			} else {
				if (app.isForceKilled()) {
					return SwingInstanceStatus.FORCE_KILLED;
				} else {
					return SwingInstanceStatus.FINISHED;
				}
			}
		}
	}

}
