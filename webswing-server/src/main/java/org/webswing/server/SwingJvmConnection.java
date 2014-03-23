package org.webswing.server;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;
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
import org.apache.tools.ant.listener.TimestampedLogger;
import org.apache.tools.ant.taskdefs.Java;
import org.apache.tools.ant.types.Environment.Variable;
import org.atmosphere.cpr.AtmosphereResource;
import org.atmosphere.cpr.AtmosphereResourceEvent;
import org.atmosphere.cpr.AtmosphereResourceEventListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.webswing.Constants;
import org.webswing.model.c2s.JsonConnectionHandshake;
import org.webswing.model.s2c.JsonAppFrame;
import org.webswing.model.s2c.JsonLinkAction;
import org.webswing.model.s2c.OpenFileResult;
import org.webswing.model.s2c.PrinterJobResult;
import org.webswing.model.s2c.JsonLinkAction.JsonLinkActionType;
import org.webswing.server.model.SwingApplicationDescriptor;
import org.webswing.toolkit.WebToolkit;

public class SwingJvmConnection implements MessageListener {

    private static final Logger log = LoggerFactory.getLogger(SwingJvmConnection.class);
    private static ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("vm://localhost");
    private static Map<String, Map<String, Future<?>>> runningSwingApps = new HashMap<String, Map<String, Future<?>>>();//map of users per application

    private Connection connection;
    private Session session;
    private MessageProducer producer;
    private MessageConsumer consumer;
    private AtmosphereResource client;
    private String applicationName;
    private String clientId;
    private String sessionId;
    private Integer screenWidth;
    private Integer screenHeight;
    private boolean newAppStarted = false;
    private ExecutorService swingAppExecutor = Executors.newSingleThreadExecutor();
    private boolean initialized=false;

    public SwingJvmConnection(JsonConnectionHandshake handshake, String appName, SwingApplicationDescriptor appConfig, AtmosphereResource client) {
        this.client = client;
        this.applicationName = appName;
        client.addEventListener(new AtmosphereResourceEventListenerAdapter() {

            @Override
            public void onDisconnect(AtmosphereResourceEvent event) {
                close(true);
            }
        });
        this.clientId = handshake.clientId;
        this.sessionId = handshake.sessionId;
        this.screenWidth = handshake.desktopWidth;
        this.screenHeight = handshake.desktopHeight;
        try {
            Future<?> app = getUserMap().get(clientId);
            if (app == null || app.isDone() || app.isCancelled()) {
                if (reachedMaxConnections(appConfig.getMaxClients())) {
                    client.getBroadcaster().broadcast(Constants.TOO_MANY_CLIENTS_NOTIFICATION, client);
                } else {
                    initialize();
                    getUserMap().put(clientId, start(appConfig));
                    newAppStarted = true;
                }
            }else{
                initialize();
            }
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    private boolean reachedMaxConnections(int maxClients) {
        int count = 0;
        for (Iterator<Entry<String, Future<?>>> i = getUserMap().entrySet().iterator(); i.hasNext();) {
            Entry<String, Future<?>> entry = i.next();
            if (entry.getValue().isCancelled() || entry.getValue().isDone()) {
                i.remove();
            } else {
                count += 1;
            }
        }
        if (count < maxClients) {
            return false;
        } else {
            return true;
        }
    }

    private Map<String, Future<?>> getUserMap() {
        if (!runningSwingApps.containsKey(applicationName)) {
            runningSwingApps.put(applicationName, new HashMap<String, Future<?>>());
        }
        return runningSwingApps.get(applicationName);
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
        initialized=true;
    }

    public void send(Serializable o) {
        try {
            producer.send(session.createObjectMessage(o));
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    public void sendMsg(String s) {
        try {
            producer.send(session.createTextMessage(s));
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    public void sendKill() {
        try {
            producer.send(session.createTextMessage(Constants.SWING_KILL_SIGNAL));
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    public void onMessage(Message m) {
        try {
            if (m instanceof ObjectMessage) {
                if(((ObjectMessage) m).getObject() instanceof PrinterJobResult){
                    PrinterJobResult pj=(PrinterJobResult) ((ObjectMessage) m).getObject();
                    FileServlet.registerFile(pj.getPdf(), pj.getId(), 30, TimeUnit.MINUTES, pj.getClientId());
                    JsonAppFrame f = new JsonAppFrame();
                    JsonLinkAction linkAction = new JsonLinkAction(JsonLinkActionType.print, pj.getId());
                    f.setLinkAction(linkAction);
                    client.getBroadcaster().broadcast(f, client);
                }
                if(((ObjectMessage) m).getObject() instanceof OpenFileResult){
                    OpenFileResult fr= (OpenFileResult) ((ObjectMessage) m).getObject();
                    String id = UUID.randomUUID().toString();
                    FileServlet.registerFile(fr.getF(), id, 30, TimeUnit.MINUTES, fr.getClientId());
                    JsonAppFrame f = new JsonAppFrame();
                    JsonLinkAction linkAction = new JsonLinkAction(JsonLinkActionType.file,id);
                    f.setLinkAction(linkAction);
                    client.getBroadcaster().broadcast(f, client);
                }
                client.getBroadcaster().broadcast(((ObjectMessage) m).getObject(), client);
            } else if (m instanceof TextMessage) {
                String text = ((TextMessage) m).getText();
                if (text.equals(Constants.SWING_SHUTDOWN_NOTIFICATION)) {
                    close(true);
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
            client.getBroadcaster().broadcast(Constants.SWING_SHUTDOWN_NOTIFICATION, client);
        }
        SwingAsyncManagedService.clients.remove(clientId);
    }

    public Future<?> start(final SwingApplicationDescriptor appConfig) {
        Future<?> future = swingAppExecutor.submit(new Runnable() {

            public void run() {
                Project project = new Project();
                //set home directory
                String dirString;
                if (appConfig.isHomeDirPerSession()) {
                    dirString = appConfig.getHomeDir() + File.separator + clientId;
                } else {
                    dirString = appConfig.getHomeDir();
                }
                File homeDir = new File(dirString);
                if (!homeDir.exists()) {
                    homeDir.mkdirs();
                }
                project.setBaseDir(homeDir);
                //setup logging
                project.init();
                DefaultLogger logger = new TimestampedLogger();
                project.addBuildListener(logger);
                logger.setOutputPrintStream(System.out);
                logger.setErrorPrintStream(System.err);
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
                    javaTask.setJar(new File(System.getProperty(Constants.WAR_FILE_LOCATION).substring(6)));
                    javaTask.setArgs(appConfig.getArgs());
                    String webSwingToolkitJarPath = WebToolkit.class.getProtectionDomain().getCodeSource().getLocation().toExternalForm().substring(6);
                    String bootCp = "-Xbootclasspath/a:" + webSwingToolkitJarPath;
                    String debug = System.getProperty(Constants.SWING_DEBUG_FLAG, "").equals("true") ? " -Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,address=8000,server=y,suspend=y " : "";
                    String aaFonts = System.getProperty(Constants.SWING_AA_FONT, "true").equals("true") ? " -Dawt.useSystemAAFontSettings=on -Dswing.aatext=true " : "";
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
                    e.printStackTrace();
                    caught = e;
                }
                project.fireBuildFinished(caught);
            }
        });
        return future;
    }

    public String getClientId() {
        return clientId;
    }

    protected boolean isNewAppStarted() {
        return newAppStarted;
    }

    public String getSessionId() {
        return sessionId;
    }
    
    public boolean isInitialized() {
        return initialized;
    }

}
