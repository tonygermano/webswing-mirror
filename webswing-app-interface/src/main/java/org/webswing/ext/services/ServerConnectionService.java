package org.webswing.ext.services;

import java.io.Serializable;

import javax.jms.Connection;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.webswing.Constants;
import org.webswing.classloader.SwingClassloader;
import org.webswing.common.ServerConnectionIfc;
import org.webswing.model.c2s.JsonEvent;
import org.webswing.util.Util;

/**
 * @author Viktor_Meszaros
 * This class is needed to achieve classpath isolation for swing application, all functionality dependent on external libs is implemented here.
 */
public class ServerConnectionService implements MessageListener, ServerConnectionIfc {

    private static ServerConnectionService impl;

    private Session session;
    private MessageProducer producer;

    public static ServerConnectionIfc getInstance() {
        if (impl == null) {
            impl = new ServerConnectionService();
        }
        return impl;

    }

    public ServerConnectionService() {
        try {
            String clientId = System.getProperty(Constants.SWING_START_SYS_PROP_CLIENT_ID);
            ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(Constants.JMS_URL);
            Connection connection = connectionFactory.createConnection();
            connection.start();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Queue consumerDest = session.createQueue(clientId + Constants.SERVER2SWING);
            Queue producerDest = session.createQueue(clientId + Constants.SWING2SERVER);
            producer = session.createProducer(producerDest);
            session.createConsumer(consumerDest).setMessageListener(this);
            connection.setExceptionListener(new ExceptionListener() {

                public void onException(JMSException e) {
                    System.out.println("Exiting application for inactivity. ");
                    Runtime.getRuntime().removeShutdownHook(SwingClassloader.notifyExitThread);
                    System.exit(1);
                }
            });
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    public void sendShutdownNotification() {
        try {
            producer.send(session.createTextMessage(Constants.SWING_SHUTDOWN_NOTIFICATION));
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    public void sendJsonObject(Serializable o) {
        try {
            synchronized (this) {
                producer.send(session.createObjectMessage(o));
            }
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    public void onMessage(Message msg) {
        try {
            if (msg instanceof ObjectMessage) {
                ObjectMessage omsg = (ObjectMessage) msg;
                if (omsg.getObject() instanceof JsonEvent) {
                   Util.getWebToolkit().getEventDispatcher().dispatchEvent((JsonEvent) omsg.getObject());
                }
            } else if (msg instanceof TextMessage) {
                TextMessage tmsg = (TextMessage) msg;
                Util.getWebToolkit().getEventDispatcher().dispatchMessage( tmsg.getText());
            }
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }


}
