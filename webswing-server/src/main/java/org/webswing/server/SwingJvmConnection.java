package org.webswing.server;

import java.io.File;
import java.io.PrintStream;
import java.io.Serializable;
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

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DefaultLogger;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Java;
import org.apache.tools.ant.types.Environment.Variable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.webswing.Constants;
import org.webswing.model.admin.s2c.JsonSwingJvmStats;
import org.webswing.model.c2s.JsonConnectionHandshake;
import org.webswing.model.s2c.JsonAppFrame;
import org.webswing.model.s2c.JsonLinkAction;
import org.webswing.model.s2c.JsonLinkAction.JsonLinkActionType;
import org.webswing.model.s2c.OpenFileResult;
import org.webswing.model.s2c.PrinterJobResult;
import org.webswing.model.server.SwingApplicationDescriptor;
import org.webswing.server.handler.FileServlet;
import org.webswing.server.util.Los;
import org.webswing.server.util.ServerUtil;
import org.webswing.server.util.SwingAntTimestampedLogger;
import org.webswing.toolkit.WebToolkit;

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
    private JsonSwingJvmStats currentStatus;

    private ExecutorService swingAppExecutor = Executors.newSingleThreadExecutor();

    public SwingJvmConnection(JsonConnectionHandshake handshake, SwingApplicationDescriptor appConfig, WebSessionListener webListener) {
        this.webListener = webListener;
        this.clientId = handshake.clientId;
        try {
            initialize();
            app = start(appConfig, handshake.desktopWidth, handshake.desktopHeight);
        } catch (JMSException e) {
            e.printStackTrace();
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
        connection = connectionFactory.createConnection();
        connection.start();
        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Queue producerQueue = session.createQueue(clientId + Constants.SERVER2SWING);
        Queue consumerQueue = session.createQueue(clientId + Constants.SWING2SERVER);
        consumer = session.createConsumer(consumerQueue);
        consumer.setMessageListener(this);
        producer = session.createProducer(producerQueue);
        connection.setExceptionListener(new ExceptionListener() {

            @Override
            public void onException(JMSException paramJMSException) {
                log.warn("JMS encountered an exception: " + paramJMSException.getMessage());
                try {
                    consumer.close();
                    producer.close();
                    session.close();
                    connection.close();
                } catch (JMSException e) {
                    e.printStackTrace();
                }
                try {
                    SwingJvmConnection.this.initialize();
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void send(Serializable o) {
        try {
            if (o instanceof String) {
                producer.send(session.createTextMessage((String) o));
            } else {
                producer.send(session.createObjectMessage(o));
            }
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    public void onMessage(Message m) {
        try {
            if (m instanceof ObjectMessage) {
                if (((ObjectMessage) m).getObject() instanceof JsonSwingJvmStats) {
                    currentStatus = (JsonSwingJvmStats) ((ObjectMessage) m).getObject();
                    SwingInstanceManager.getInstance().notifySwingChangeChange();
                }
                if (((ObjectMessage) m).getObject() instanceof PrinterJobResult) {
                    PrinterJobResult pj = (PrinterJobResult) ((ObjectMessage) m).getObject();
                    FileServlet.registerFile(pj.getPdf(), pj.getId(), 30, TimeUnit.MINUTES, pj.getClientId());
                    JsonAppFrame f = new JsonAppFrame();
                    JsonLinkAction linkAction = new JsonLinkAction(JsonLinkActionType.print, pj.getId());
                    f.setLinkAction(linkAction);
                    webListener.sendToWeb(f);
                }
                if (((ObjectMessage) m).getObject() instanceof OpenFileResult) {
                    OpenFileResult fr = (OpenFileResult) ((ObjectMessage) m).getObject();
                    String id = UUID.randomUUID().toString();
                    FileServlet.registerFile(fr.getF(), id, 30, TimeUnit.MINUTES, fr.getClientId());
                    JsonAppFrame f = new JsonAppFrame();
                    JsonLinkAction linkAction = new JsonLinkAction(JsonLinkActionType.file, id);
                    f.setLinkAction(linkAction);
                    webListener.sendToWeb(f);
                }
                webListener.sendToWeb(((ObjectMessage) m).getObject());
            } else if (m instanceof TextMessage) {
                String text = ((TextMessage) m).getText();
                if (text.equals(Constants.SWING_SHUTDOWN_NOTIFICATION)) {
                    // close(true); - handled by ant thread
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void close(boolean withShutdownEvent) {
        try {
            consumer.close();
            producer.close();
            session.close();
            connection.close();
        } catch (JMSException e) {
            e.printStackTrace();
        }
        if (withShutdownEvent) {
            webListener.sendToWeb(Constants.SWING_SHUTDOWN_NOTIFICATION);
        }
        webListener.notifyClose();
    }

    public Future<?> start(final SwingApplicationDescriptor appConfig, final Integer screenWidth, final Integer screenHeight) {
        Future<?> future = swingAppExecutor.submit(new Callable<Object>() {

            public Object call() throws Exception {
                try {
                    Project project = new Project();
                    //set home directory
                    String dirString = appConfig.getHomeDir();
                    File homeDir = new File(dirString);
                    if (!homeDir.exists()) {
                        homeDir.mkdirs();
                    }
                    project.setBaseDir(homeDir);
                    //setup logging
                    project.init();
                    DefaultLogger logger = new SwingAntTimestampedLogger();
                    project.addBuildListener(logger);
                    PrintStream os = new PrintStream(new Los(clientId));
                    logger.setOutputPrintStream(os);
                    logger.setErrorPrintStream(os);
                    logger.setMessageOutputLevel(Project.MSG_INFO);
                    //System.setOut(new PrintStream(new DemuxOutputStream(project, false)));
                    //System.setErr(new PrintStream(new DemuxOutputStream(project, true)));
                    project.fireBuildStarted();
                    Throwable caught = null;
                    try {
                        Java javaTask = new Java();
                        javaTask.setTaskName(clientId);
                        javaTask.setProject(project);
                        javaTask.setFork(true);
                        javaTask.setFailonerror(true);
                        javaTask.setJar(new File(ServerUtil.getWarFileLocation().substring(6)));
                        javaTask.setArgs(appConfig.getArgs());
                        String webSwingToolkitJarPath = WebToolkit.class.getProtectionDomain().getCodeSource().getLocation().toExternalForm().substring(6);
                        String bootCp = "-Xbootclasspath/a:" + webSwingToolkitJarPath;
                        String debug = System.getProperty(Constants.SWING_DEBUG_FLAG, "").equals("true") ? " -Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,address=8000,server=y,suspend=y " : "";
                        String aaFonts = appConfig.isAntiAliasText() ? " -Dawt.useSystemAAFontSettings=on -Dswing.aatext=true " : "";
                        javaTask.setJvmargs(bootCp + debug + aaFonts + " -noverify " + appConfig.getVmArgs());

                        Variable clientIdVar = new Variable();
                        clientIdVar.setKey(Constants.SWING_START_SYS_PROP_CLIENT_ID);
                        clientIdVar.setValue(clientId);
                        javaTask.addSysproperty(clientIdVar);

                        Variable classpathVar = new Variable();
                        classpathVar.setKey(Constants.SWING_START_SYS_PROP_CLASS_PATH);
                        classpathVar.setValue(appConfig.generateClassPathString());
                        javaTask.addSysproperty(classpathVar);

                        Variable tempDirVar = new Variable();
                        tempDirVar.setKey(Constants.TEMP_DIR_PATH);
                        tempDirVar.setValue(System.getProperty(Constants.TEMP_DIR_PATH));
                        javaTask.addSysproperty(tempDirVar);

                        Variable mainClass = new Variable();
                        mainClass.setKey(Constants.SWING_START_SYS_PROP_MAIN_CLASS);
                        mainClass.setValue(appConfig.getMainClass());
                        javaTask.addSysproperty(mainClass);

                        Variable inactivityTimeout = new Variable();
                        inactivityTimeout.setKey(Constants.SWING_SESSION_TIMEOUT_SEC);
                        inactivityTimeout.setValue(appConfig.getSwingSessionTimeout() + "");
                        javaTask.addSysproperty(inactivityTimeout);

                        Variable toolkitImplClass = new Variable();
                        toolkitImplClass.setKey("awt.toolkit");
                        toolkitImplClass.setValue("org.webswing.toolkit.WebToolkit");
                        javaTask.addSysproperty(toolkitImplClass);

                        Variable graphicsConfigImplClass = new Variable();
                        graphicsConfigImplClass.setKey("java.awt.graphicsenv");
                        graphicsConfigImplClass.setValue("org.webswing.toolkit.ge.WebGraphicsEnvironment");
                        javaTask.addSysproperty(graphicsConfigImplClass);

                        Variable printerJobImplClass = new Variable();
                        printerJobImplClass.setKey("java.awt.printerjob");
                        printerJobImplClass.setValue("org.webswing.toolkit.WebPrinterJob");
                        javaTask.addSysproperty(printerJobImplClass);

                        Variable screenWidthVar = new Variable();
                        screenWidthVar.setKey(Constants.SWING_SCREEN_WIDTH);
                        screenWidthVar.setValue(((screenWidth == null || screenWidth < Constants.SWING_SCREEN_WIDTH_MIN) ? Constants.SWING_SCREEN_WIDTH_MIN : screenWidth) + "");
                        javaTask.addSysproperty(screenWidthVar);

                        Variable screenHeigthVar = new Variable();
                        screenHeigthVar.setKey(Constants.SWING_SCREEN_HEIGHT);
                        screenHeigthVar.setValue(((screenHeight == null || screenHeight < Constants.SWING_SCREEN_HEIGHT_MIN) ? Constants.SWING_SCREEN_HEIGHT_MIN : screenHeight) + "");
                        javaTask.addSysproperty(screenHeigthVar);

                        javaTask.init();
                        javaTask.executeJava();
                    } catch (BuildException e) {
                        project.fireBuildFinished(caught);
                        throw e;
                    }
                    project.fireBuildFinished(caught);
                } catch (Exception e) {
                    close(true);
                    throw e;
                }
                close(true);
                return null;
            }
        });
        return future;
    }

    public String getClientId() {
        return clientId;
    }

    public JsonSwingJvmStats getCurrentStatus() {
        return currentStatus;
    }

    public interface WebSessionListener {

        void sendToWeb(Serializable o);

        void notifyClose();
    }
}
