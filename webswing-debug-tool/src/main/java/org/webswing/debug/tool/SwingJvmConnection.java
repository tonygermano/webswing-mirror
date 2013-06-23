package org.webswing.debug.tool;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.Serializable;

import javax.imageio.ImageIO;
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
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.apache.commons.codec.binary.Base64;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DefaultLogger;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Java;
import org.apache.tools.ant.types.Environment.Variable;
import org.webswing.Configuration;
import org.webswing.Constants;
import org.webswing.debug.tool.ui.DebugFrame;
import org.webswing.ignored.model.s2c.JsonPaintRequest;

public class SwingJvmConnection implements MessageListener, Runnable {

    private Session session;
    private MessageProducer producer;
    private MessageConsumer consumer;
    private String clientId;
    
    private DebugFrame df;
    
    private JLabel label=new JLabel(new ImageIcon("test.png"));

    public SwingJvmConnection(String clientId, Connection c,DebugFrame df) {
        this.clientId = clientId;
        this.df=df;
        try {
            session = c.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Queue producerQueue = session.createQueue(clientId + Constants.SERVER2SWING);
            Queue consumerQueue = session.createQueue(clientId + Constants.SWING2SERVER);
            consumer = session.createConsumer(consumerQueue);
            consumer.setMessageListener(this);
            producer = session.createProducer(producerQueue);
            c.start();
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
                Object o=((ObjectMessage) m).getObject();
                if(o instanceof JsonPaintRequest){
                    JsonPaintRequest req = ((JsonPaintRequest) o);
                    try {
                        BufferedImage image= ImageIO.read(new ByteArrayInputStream(Base64.decodeBase64(req.b64images.get("test"))));
                        df.drawImage(image);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else if (m instanceof TextMessage) {
                String text = ((TextMessage) m).getText();
                if (text.equals(Constants.SWING_SHUTDOWN_NOTIFICATION)) {
                    consumer.close();
                    producer.close();
                    session.close();
                    System.out.println("notifying browser shutdown");

                }
            }
            System.out.println(m);
        } catch (JMSException e) {
            e.printStackTrace();
        }

    }

    public String getClientId() {
        return clientId;
    }

    public void run() {
        sendKill();
    }
 
}
