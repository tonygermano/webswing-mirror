package sk.viktor.server;

import java.io.File;
import java.io.PrintStream;
import java.io.Serializable;

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

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DefaultLogger;
import org.apache.tools.ant.DemuxOutputStream;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Java;
import org.apache.tools.ant.types.Environment.Variable;

import sk.viktor.SwingMain;

import com.corundumstudio.socketio.SocketIOClient;

public class WebSwingApplication implements MessageListener {

    private Session session;
    private MessageProducer producer;
    private MessageConsumer consumer;
    private SocketIOClient client;
    private String clientId;

    public WebSwingApplication(String clientId, Connection c, SocketIOClient client) {
        this.client = client;
        this.clientId = clientId;
        try {
            session = c.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Queue producerQueue = session.createQueue(clientId + JmsService.SERVER2SWING);
            Queue consumerQueue = session.createQueue(clientId + JmsService.SWING2SERVER);
            consumer = session.createConsumer(consumerQueue);
            consumer.setMessageListener(this);
            producer = session.createProducer(producerQueue);
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

    public void onMessage(Message m) {
        try {
            if (m instanceof ObjectMessage) {
                client.sendJsonObject(((ObjectMessage) m).getObject());
            }else if (m instanceof TextMessage){
                String text=((TextMessage) m).getText();
                if(text.equals(SwingServer.SWING_SHUTDOWN_NOTIFICATION)){
                    consumer.close();
                    producer.close();
                    session.close();
                    client.disconnect();
                    SwingServer.removeSwingClientApplication(clientId);
                }
            }
        } catch (JMSException e) {
            e.printStackTrace();
        }

    }

    public void start() {
        new Thread(new Runnable() {

            public void run() {
                Project project = new Project();
                project.setBaseDir(new File(System.getProperty("user.dir")));
                project.init();
                DefaultLogger logger = new DefaultLogger();
                project.addBuildListener(logger);
                logger.setOutputPrintStream(System.out);
                logger.setErrorPrintStream(System.err);
                logger.setMessageOutputLevel(Project.MSG_INFO);
                System.setOut(new PrintStream(new DemuxOutputStream(project, false)));
                System.setErr(new PrintStream(new DemuxOutputStream(project, true)));
                project.fireBuildStarted();
                Throwable caught = null;
                try {
                    Java javaTask = new Java();
                    javaTask.setTaskName(clientId);
                    javaTask.setProject(project);
                    javaTask.setFork(true);
                    javaTask.setCloneVm(true);
                    javaTask.setFailonerror(true);
                    javaTask.setClassname(SwingMain.class.getName());
                    javaTask.setJvmargs("-noverify ");
                    Variable clientIdVar = new Variable();
                    clientIdVar.setKey("clientId");
                    clientIdVar.setValue(clientId);
                    javaTask.addSysproperty(clientIdVar);
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

}
