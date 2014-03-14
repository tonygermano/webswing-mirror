package org.webswing.server;

import java.io.File;
import java.io.Serializable;
import java.util.concurrent.ScheduledFuture;

import javax.jms.Connection;
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
import org.atmosphere.cpr.AtmosphereResource;
import org.atmosphere.cpr.Broadcaster;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.webswing.Constants;
import org.webswing.model.c2s.JsonConnectionHandshake;
import org.webswing.server.model.SwingApplicationDescriptor;
import org.webswing.toolkit.WebToolkit;

public class SwingJvmConnection implements MessageListener, Runnable {

    private static final Logger log = LoggerFactory.getLogger(SwingJvmConnection.class);

    private static Connection connection;
    static {
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("vm://localhost");
        try {
            // Create a Connection
            connection = connectionFactory.createConnection();
            connection.start();
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    private Session session;
    private MessageProducer producer;
    private MessageConsumer consumer;
    private Broadcaster client;
    private String clientId;
    private ScheduledFuture<?> exitSchedule;
    private Integer screenWidth;
    private Integer screenHeight;

    public SwingJvmConnection(JsonConnectionHandshake handshake, SwingApplicationDescriptor appConfig, Broadcaster client) {
        this.client = client;
        this.clientId = handshake.clientId;
        this.screenWidth = handshake.desktopWidth;
        this.screenHeight = handshake.desktopHeight;
        try {
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Queue producerQueue = session.createQueue(clientId + Constants.SERVER2SWING);
            Queue consumerQueue = session.createQueue(clientId + Constants.SWING2SERVER);
            consumer = session.createConsumer(consumerQueue);
            consumer.setMessageListener(this);
            producer = session.createProducer(producerQueue);
            start(appConfig);
        } catch (JMSException e) {
            e.printStackTrace();
        }
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
                client.broadcast(((ObjectMessage) m).getObject());
            } else if (m instanceof TextMessage) {
                String text = ((TextMessage) m).getText();
                if (text.equals(Constants.SWING_SHUTDOWN_NOTIFICATION)) {
                    consumer.close();
                    producer.close();
                    session.close();
                    System.out.println("notifying browser shutdown");
                    client.broadcast(Constants.SWING_SHUTDOWN_NOTIFICATION);
                    //SwingServer.removeSwingClientApplication(clientId);
                    for (AtmosphereResource ar : client.getAtmosphereResources()) {
                        ar.close();
                    }
                    if (this.getExitSchedule() != null) {
                        this.getExitSchedule().cancel(false);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void start(final SwingApplicationDescriptor appConfig) {
        new Thread(new Runnable() {

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
                DefaultLogger logger = new DefaultLogger();
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
                    String debug = System.getProperty(Constants.SWING_DEBUG_FLAG).equals("true") ? " -Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,address=8000,server=y,suspend=y " : "";
                    javaTask.setJvmargs(bootCp + debug + " -noverify " + appConfig.getVmArgs());

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

                    Variable screenWidthVar = new Variable();
                    screenWidthVar.setKey(Constants.SWING_SCREEN_WIDTH);
                    screenWidthVar.setValue(((screenWidth == null || screenWidth < Constants.SWING_SCREEN_WIDTH_MIN) ? Constants.SWING_SCREEN_WIDTH_MIN : screenWidth)+"");
                    javaTask.addSysproperty(screenWidthVar);
                    
                    Variable screenHeigthVar = new Variable();
                    screenHeigthVar.setKey(Constants.SWING_SCREEN_HEIGHT);
                    screenHeigthVar.setValue(((screenHeight == null || screenHeight < Constants.SWING_SCREEN_HEIGHT_MIN) ? Constants.SWING_SCREEN_HEIGHT_MIN : screenHeight)+"");
                    javaTask.addSysproperty(screenHeigthVar);
            
                    javaTask.init();
                    javaTask.executeJava();
                } catch (BuildException e) {
                    e.printStackTrace();
                    caught = e;
                }
                project.fireBuildFinished(caught);
            }
        }).start();
    }

    public String getClientId() {
        return clientId;
    }

    public void run() {
        sendKill();
    }

    public void setExitTimer(ScheduledFuture<?> schedule) {
        this.exitSchedule = schedule;
    }

    protected ScheduledFuture<?> getExitSchedule() {
        return exitSchedule;
    }

    protected void setExitSchedule(ScheduledFuture<?> exitSchedule) {
        this.exitSchedule = exitSchedule;
    }

}
