package sk.viktor.ignored.common;

import java.io.Serializable;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;

import sk.viktor.ignored.model.c2s.JsonEvent;
import sk.viktor.ignored.model.s2c.JsonPaintRequest;
import sk.viktor.server.JmsService;
import sk.viktor.server.SwingServer;
import sk.viktor.util.Util;

public class ServerJvmConnection implements MessageListener {

    private Session session;
    private MessageProducer producer;
    private transient boolean readyToReceive = true;
    private PaintManager paintManager;

    public ServerJvmConnection(PaintManager pm) {
        try {
            this.paintManager = pm;
            String clientId = System.getProperty(SwingServer.SWING_START_SYS_PROP_CLIENT_ID);
            ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(JmsService.JMS_URL);
            Connection connection = connectionFactory.createConnection();
            connection.start();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Queue consumerDest = session.createQueue(clientId + JmsService.SERVER2SWING);
            Queue producerDest = session.createQueue(clientId + JmsService.SWING2SERVER);
            producer = session.createProducer(producerDest);
            session.createConsumer(consumerDest).setMessageListener(this);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
    
    public void sendShutdownNotification(){
        try {
            producer.send(session.createTextMessage(SwingServer.SWING_SHUTDOWN_NOTIFICATION));
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    public void sendJsonObject(Serializable o) {
        try {
            synchronized (this) {
                producer.send(session.createObjectMessage(o));
                if(o instanceof JsonPaintRequest){
                    readyToReceive = false;
                }
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
                    paintManager.dispatchEvent((JsonEvent) omsg.getObject());
                }
            } else if (msg instanceof TextMessage) {
                TextMessage tmsg = (TextMessage) msg;
                if (tmsg.getText().equals(SwingServer.PAINT_ACK_PREFIX)) {
                    if (Util.needPainting(paintManager.windows)) {
                        paintManager.doSendPaintRequest();
                    } else {
                        synchronized (this) {
                            this.readyToReceive = true;
                        }
                    }
                }else if(tmsg.getText().equals(SwingServer.SWING_KILL_SIGNAL)){
                    System.exit(0);
                }
            }
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    public boolean isReadyToReceive() {
        return readyToReceive;
    }
}
