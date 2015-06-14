package org.webswing.server;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Serializable;
import java.net.URI;
import java.net.URLDecoder;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import javax.jms.Connection;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.management.MBeanServerConnection;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import main.Main;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.commons.lang.text.StrSubstitutor;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DefaultLogger;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Java;
import org.apache.tools.ant.types.Environment.Variable;
import org.apache.tools.ant.types.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.webswing.Constants;
import org.webswing.model.MsgInternal;
import org.webswing.model.MsgOut;
import org.webswing.model.c2s.ConnectionHandshakeMsgIn;
import org.webswing.model.internal.OpenFileResultMsgInternal;
import org.webswing.model.internal.PrinterJobResultMsgInternal;
import org.webswing.model.s2c.AppFrameMsgOut;
import org.webswing.model.s2c.LinkActionMsg;
import org.webswing.model.s2c.LinkActionMsg.LinkActionType;
import org.webswing.model.s2c.SimpleEventMsgOut;
import org.webswing.model.server.SwingAppletDescriptor;
import org.webswing.model.server.SwingApplicationDescriptor;
import org.webswing.model.server.SwingDescriptor;
import org.webswing.server.handler.FileServlet;
import org.webswing.server.handler.JmsService;
import org.webswing.server.util.Los;
import org.webswing.server.util.ServerUtil;
import org.webswing.server.util.SwingAntTimestampedLogger;
import org.webswing.toolkit.WebToolkit;
import org.webswing.toolkit.WebToolkit6;
import org.webswing.toolkit.WebToolkit7;
import org.webswing.toolkit.WebToolkit8;

public class SwingJvmConnection implements MessageListener {

	private static final Logger log = LoggerFactory.getLogger(SwingJvmConnection.class);
	private static ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("vm://localhost");

	private Connection connection;
	private Session session;
	private MessageProducer producer;
	private MessageConsumer consumer;
	private WebSessionListener webListener;
	private Future<?> app;
	private String clientId;
	private JMXConnector jmxConnection;

	private ExecutorService swingAppExecutor = Executors.newSingleThreadExecutor();
	private boolean jmsOpen = false;
	private String customArgs = "";
	private int debugPort = 0;

	public SwingJvmConnection(ConnectionHandshakeMsgIn handshake, SwingDescriptor appConfig, WebSessionListener webListener, String customArgs, int debugPort) {
		this.webListener = webListener;
		this.clientId = handshake.getClientId();
		this.customArgs = customArgs;
		this.debugPort = debugPort;
		try {
			initialize();
			app = start(appConfig, handshake);
		} catch (JMSException e) {
			log.error("SwingJvmConnection:init", e);
		}
	}

	public boolean isRunning() {
		if (app == null || app.isDone() || app.isCancelled()) {
			return false;
		} else {
			return true;
		}
	}

	private void initialize() throws JMSException {
		synchronized (this) {
			connection = connectionFactory.createConnection();
			connection.start();
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			Queue producerQueue = session.createQueue(clientId + Constants.SERVER2SWING);
			Queue consumerQueue = session.createQueue(clientId + Constants.SWING2SERVER);
			consumer = session.createConsumer(consumerQueue);
			consumer.setMessageListener(this);
			producer = session.createProducer(producerQueue);
			jmsOpen = true;
			connection.setExceptionListener(new ExceptionListener() {

				@Override
				public void onException(JMSException paramJMSException) {
					log.warn("JMS encountered an exception: " + paramJMSException.getMessage());
					try {
						synchronized (SwingJvmConnection.this) {
							consumer.close();
							producer.close();
							session.close();
							connection.close();
							jmsOpen = false;
						}
					} catch (JMSException e) {
						log.error("SwingJvmConnection:initialize1", e);
					}
					try {
						SwingJvmConnection.this.initialize();
					} catch (JMSException e) {
						log.error("SwingJvmConnection:initialize2", e);

					}
				}
			});
		}
	}

	public synchronized void send(Serializable o) {
		if (jmsOpen) {
			try {
				if (o instanceof String) {
					producer.send(session.createTextMessage((String) o));
				} else {
					producer.send(session.createObjectMessage(o));
				}
			} catch (JMSException e) {
				log.debug("SwingJvmConnection:send", e);
			}
		}
	}

	public void onMessage(Message m) {
		try {
			if (m instanceof ObjectMessage) {
				Serializable o = ((ObjectMessage) m).getObject();
				if (o instanceof MsgInternal) {
					if (o instanceof PrinterJobResultMsgInternal) {
						PrinterJobResultMsgInternal pj = (PrinterJobResultMsgInternal) o;
						FileServlet.registerFile(pj.getPdfFile(), pj.getId(), 30, TimeUnit.MINUTES, webListener.getUser());
						AppFrameMsgOut f = new AppFrameMsgOut();
						LinkActionMsg linkAction = new LinkActionMsg(LinkActionType.print, pj.getId());
						f.setLinkAction(linkAction);
						webListener.sendToWeb(f);
					} else if (o instanceof OpenFileResultMsgInternal) {
						OpenFileResultMsgInternal fr = (OpenFileResultMsgInternal) o;
						String id = UUID.randomUUID().toString();
						FileServlet.registerFile(fr.getF(), id, 30, TimeUnit.MINUTES, webListener.getUser());
						AppFrameMsgOut f = new AppFrameMsgOut();
						LinkActionMsg linkAction = new LinkActionMsg(LinkActionType.file, id);
						f.setLinkAction(linkAction);
						webListener.sendToWeb(f);
					}
				} else if (o instanceof MsgOut) {
					webListener.sendToWeb((MsgOut) o);
				}
			} else if (m instanceof TextMessage) {
				String text = ((TextMessage) m).getText();
				if (text.startsWith(Constants.SWING_PID_NOTIFICATION) && this.jmxConnection == null) {
					this.jmxConnection = getLocalMBeanServerConnectionStatic(text.substring(Constants.SWING_PID_NOTIFICATION.length()));
				}
			}
		} catch (Exception e) {
			log.error("SwingJvmConnection:onMessage", e);

		}

	}

	public void close(boolean withShutdownEvent) {
		if (withShutdownEvent) {
			webListener.sendToWeb(SimpleEventMsgOut.shutDownNotification.buildMsgOut());
		}
		try {
			synchronized (this) {
				consumer.close();
				producer.close();
				session.close();
				connection.close();
				jmsOpen = false;
			}
			if (jmxConnection != null) {
				JMXConnector thisjmxConnection = jmxConnection;
				jmxConnection = null;
				thisjmxConnection.close();
			}
		} catch (Exception e) {
			log.debug("SwingJvmConnection:close", e);
		}
		webListener.notifyClose();
	}

	public Future<?> start(final SwingDescriptor appConfig, final ConnectionHandshakeMsgIn handshake) {
		final Integer screenWidth = handshake.getDesktopWidth();
		final Integer screenHeight = handshake.getDesktopHeight();
		final StrSubstitutor subs = ServerUtil.getConfigSubstitutorMap(webListener.getUser(), getClientId());

		Future<?> future = swingAppExecutor.submit(new Callable<Object>() {
			public Object call() throws Exception {
				try {
					Project project = new Project();
					// set home directory
					File homeDir = getHomeDir(appConfig, subs);
					project.setBaseDir(homeDir);
					// setup logging
					project.init();
					DefaultLogger logger = new SwingAntTimestampedLogger();
					project.addBuildListener(logger);
					PrintStream out = new PrintStream(new Los(clientId));
					logger.setOutputPrintStream(out);
					logger.setErrorPrintStream(out);
					logger.setMessageOutputLevel(Project.MSG_INFO);
					// System.setOut(new PrintStream(new DemuxOutputStream(project, false)));
					// System.setErr(new PrintStream(new DemuxOutputStream(project, true)));
					project.fireBuildStarted();
					Throwable caught = null;
					try {
						Java javaTask = new Java();
						javaTask.setTaskName(clientId);
						javaTask.setProject(project);
						javaTask.setFork(true);
						javaTask.setFailonerror(true);
						javaTask.setClassname("main.Main");
						Path classPath = javaTask.createClasspath();
						classPath.setLocation(new File(URI.create(ServerUtil.getWarFileLocation())));
						String webSwingToolkitJarPath = "\"" + URLDecoder.decode(WebToolkit.class.getProtectionDomain().getCodeSource().getLocation().getPath(), "UTF-8") + "\"";
						String webSwingToolkitJarPathSpecific;
						String webToolkitClass;
						if (System.getProperty("java.version").startsWith("1.6")) {
							webSwingToolkitJarPathSpecific = "\"" + URLDecoder.decode(WebToolkit6.class.getProtectionDomain().getCodeSource().getLocation().getPath(), "UTF-8") + "\"";
							webToolkitClass = WebToolkit6.class.getCanonicalName();
						} else if (System.getProperty("java.version").startsWith("1.7")) {
							webSwingToolkitJarPathSpecific = "\"" + URLDecoder.decode(WebToolkit7.class.getProtectionDomain().getCodeSource().getLocation().getPath(), "UTF-8") + "\"";
							webToolkitClass = WebToolkit7.class.getCanonicalName();
						} else if (System.getProperty("java.version").startsWith("1.8")) {
							webSwingToolkitJarPathSpecific = "\"" + URLDecoder.decode(WebToolkit8.class.getProtectionDomain().getCodeSource().getLocation().getPath(), "UTF-8") + "\"";
							webToolkitClass = WebToolkit8.class.getCanonicalName();
						} else {
							log.error("Java version " + System.getProperty("java.version") + " not supported in this version. Check www.webswing.org for supported versions.");
							throw new RuntimeException("Java version not supported");
						}
						String bootCp = "-Xbootclasspath/a:" + webSwingToolkitJarPathSpecific + File.pathSeparatorChar + webSwingToolkitJarPath;

						if (!System.getProperty("os.name", "").startsWith("Windows")) {
							// filesystem isolation support on non windows systems:
							bootCp += File.pathSeparatorChar + webSwingToolkitJarPath.substring(0, webSwingToolkitJarPath.lastIndexOf(File.separator)) + File.separator + "rt-win-shell.jar\"";
						}
						log.info("Setting bootclasspath to: " + bootCp);
						String debug = appConfig.isDebug() && (debugPort != 0) ? " -Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,address=" + debugPort + ",server=y,suspend=y " : "";
						String aaFonts = appConfig.isAntiAliasText() ? " -Dawt.useSystemAAFontSettings=on -Dswing.aatext=true " : "";
						javaTask.setJvmargs(bootCp + debug + aaFonts + " -noverify -Dcom.sun.management.jmxremote " + subs.replace(appConfig.getVmArgs()));
						addSysProperty(javaTask, Constants.SWING_START_SYS_PROP_CLIENT_ID, clientId);
						addSysProperty(javaTask, Constants.SWING_START_SYS_PROP_CLASS_PATH, subs.replace(appConfig.generateClassPathString()));
						addSysProperty(javaTask, Constants.TEMP_DIR_PATH, System.getProperty(Constants.TEMP_DIR_PATH));
						addSysProperty(javaTask, Constants.JMS_URL, JmsService.getUrl());

						addSysProperty(javaTask, Constants.SWING_START_SYS_PROP_ISOLATED_FS, appConfig.isIsolatedFs() + "");
						addSysProperty(javaTask, Constants.SWING_START_SYS_PROP_ALLOW_DOWNLOAD, appConfig.isAllowDownload() + "");
						addSysProperty(javaTask, Constants.SWING_START_SYS_PROP_ALLOW_UPLOAD, appConfig.isAllowUpload() + "");
						addSysProperty(javaTask, Constants.SWING_START_SYS_PROP_ALLOW_DELETE, appConfig.isAllowDelete() + "");

						addSysProperty(javaTask, Constants.SWING_START_SYS_PROP_DIRECTDRAW, appConfig.isDirectdraw() + "");
						addSysProperty(javaTask, Constants.SWING_START_SYS_PROP_DIRECTDRAW_SUPPORTED, handshake.isDirectDrawSupported() + "");
						addSysProperty(javaTask, Constants.SWING_SESSION_TIMEOUT_SEC, appConfig.getSwingSessionTimeout() + "");
						addSysProperty(javaTask, "awt.toolkit", webToolkitClass);
						addSysProperty(javaTask, "java.awt.headless", "false");
						addSysProperty(javaTask, "java.awt.graphicsenv", "org.webswing.toolkit.ge.WebGraphicsEnvironment");
						addSysProperty(javaTask, "java.awt.printerjob", "org.webswing.toolkit.WebPrinterJob");
						addSysProperty(javaTask, Constants.SWING_SCREEN_WIDTH, ((screenWidth == null) ? Constants.SWING_SCREEN_WIDTH_MIN : screenWidth) + "");
						addSysProperty(javaTask, Constants.SWING_SCREEN_HEIGHT, ((screenHeight == null) ? Constants.SWING_SCREEN_HEIGHT_MIN : screenHeight) + "");

						if (appConfig instanceof SwingApplicationDescriptor) {
							SwingApplicationDescriptor application = (SwingApplicationDescriptor) appConfig;
							javaTask.setArgs(subs.replace(application.getArgs()) + " " + customArgs);
							addSysProperty(javaTask, Constants.SWING_START_SYS_PROP_MAIN_CLASS, subs.replace(application.getMainClass()));
						} else if (appConfig instanceof SwingAppletDescriptor) {
							SwingAppletDescriptor applet = (SwingAppletDescriptor) appConfig;
							addSysProperty(javaTask, Constants.SWING_START_SYS_PROP_APPLET_DOCUMENT_BASE, handshake.getDocumentBase());
							addSysProperty(javaTask, Constants.SWING_START_SYS_PROP_APPLET_CLASS, applet.getAppletClass());
							for (String key : applet.getParameters().keySet()) {
								addSysProperty(javaTask, Constants.SWING_START_STS_PROP_APPLET_PARAM_PREFIX + subs.replace(key), subs.replace(applet.getParameters().get(key)));
							}
						}

						javaTask.init();
						javaTask.executeJava();
					} catch (BuildException e) {
						project.fireBuildFinished(caught);
						throw e;
					}
					project.fireBuildFinished(caught);
				} catch (Exception e) {
					close(true);
					log.error("Failed to start swing jre.", e);
				}
				close(true);
				return null;
			}

		});
		return future;
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

	private void addSysProperty(Java javaTask, String key, String value) {
		Variable v = new Variable();
		v.setKey(key);
		v.setValue(value);
		javaTask.addSysproperty(v);
	}

	public String getClientId() {
		return clientId;
	}

	@SuppressWarnings("restriction")
	private JMXConnector getLocalMBeanServerConnectionStatic(String pid) {
		try {
			String address = sun.management.ConnectorAddressLink.importFrom(Integer.parseInt(pid));
			JMXServiceURL jmxUrl = new JMXServiceURL(address);
			return JMXConnectorFactory.connect(jmxUrl);
		} catch (Exception e) {
			log.warn("Failed to connect to JMX of swing instance with pid " + pid + ". Reason: " + e.getMessage());
			log.debug("Exception details:", e);
		}
		return null;
	}

	public MBeanServerConnection getJmxConnection() {
		try {
			if (jmxConnection != null) {
				return jmxConnection.getMBeanServerConnection();
			}
		} catch (IOException e) {
			log.warn("Failed to connect to JMX of swing instance. Reason:" + e.getMessage());
			log.debug("Exception details:", e);
		}
		return null;
	}

	public interface WebSessionListener {

		String getUser();

		void sendToWeb(MsgOut o);

		void notifyClose();
	}
}
