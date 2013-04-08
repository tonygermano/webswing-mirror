package org.webswing.extlib;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;
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
import javax.swing.SwingUtilities;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.commons.codec.binary.Base64;
import org.webswing.Constants;
import org.webswing.ignored.common.PaintManager;
import org.webswing.ignored.common.ServerJvmConnection;
import org.webswing.ignored.model.c2s.JsonEvent;
import org.webswing.ignored.model.s2c.JsonPaintRequest;

import com.objectplanet.image.PngEncoder;

/**
 * @author Viktor_Meszaros
 * This class is needed to achieve classpath isolation for swing application, all functionality dependent on external libs is implemented here.
 */
public class ExtLibImpl implements MessageListener, ServerJvmConnection {

    private static ExtLibImpl impl;
    private PngEncoder encoder;
    private Session session;
    private MessageProducer producer;
    private transient boolean readyToReceive = true;
    private PaintManager paintManager;

    public static ServerJvmConnection getInstance(PaintManager pm) {
        if (impl == null) {
            impl = new ExtLibImpl(pm);
        }
        return impl;

    }

    public ExtLibImpl(PaintManager pm) {
        try {
            this.paintManager = pm;
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

        try {
            encoder = new PngEncoder(PngEncoder.COLOR_TRUECOLOR_ALPHA, PngEncoder.BEST_COMPRESSION);
        } catch (Exception e) {
            System.out.println("Library for fast image encoding not found. Download the library from http://objectplanet.com/pngencoder/");
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
                if (o instanceof JsonPaintRequest) {
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
                if (tmsg.getText().equals(Constants.PAINT_ACK_PREFIX)) {
                    synchronized (this) {
                        this.readyToReceive = true;
                    }
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            paintManager.repaintIfNecessary();
                        }
                    });
                } else if (tmsg.getText().equals(Constants.SWING_KILL_SIGNAL)) {
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

    public Map<String, String> createEncodedPaintMap(Map<String, BufferedImage> windows) {
        Map<String, String> result = new HashMap<String, String>();
        for (String windowKey : windows.keySet()) {
            BufferedImage bi = windows.get(windowKey);
            result.put(windowKey, Base64.encodeBase64String(getPngImage(bi)));
        }
        return result;
    }

    public byte[] getPngImage(BufferedImage imageContent) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            if (encoder != null) {
                encoder.encode(imageContent, baos);
            } else {
                ImageOutputStream ios = ImageIO.createImageOutputStream(baos);
                ImageIO.write(imageContent, "png", ios);
            }
            byte[] result = baos.toByteArray();
            baos.close();
            return result;
        } catch (IOException e) {
            System.out.println("Writing image interupted:" + e.getMessage());
        }
        return null;
    }
}
