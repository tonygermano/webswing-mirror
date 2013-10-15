package org.webswing.debug.tool;

import java.awt.AWTEvent;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
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

import org.apache.commons.codec.binary.Base64;
import org.webswing.Constants;
import org.webswing.debug.tool.ui.DebugFrame;
import org.webswing.model.c2s.JsonEventMouse;
import org.webswing.model.c2s.JsonEventMouse.Type;
import org.webswing.model.s2c.JsonAppFrame;
import org.webswing.model.s2c.JsonWindow;

public class SwingJvmConnection implements MessageListener, Runnable, AWTEventListener {

    private Session session;
    private MessageProducer producer;
    private MessageConsumer consumer;
    private String clientId;

    private DebugFrame df;

    public SwingJvmConnection(String clientId, Connection c, DebugFrame df) {
        this.clientId = clientId;
        this.df = df;
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
        Toolkit.getDefaultToolkit().addAWTEventListener(this, AWTEvent.MOUSE_EVENT_MASK);
        Toolkit.getDefaultToolkit().addAWTEventListener(this, AWTEvent.MOUSE_MOTION_EVENT_MASK);
        Toolkit.getDefaultToolkit().addAWTEventListener(this, AWTEvent.MOUSE_WHEEL_EVENT_MASK);
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
                Object o = ((ObjectMessage) m).getObject();
                if (o instanceof JsonAppFrame) {
                    JsonAppFrame req = ((JsonAppFrame) o);
                    for (JsonWindow window : req.getWindows()) {
                        if (window.getContent() != null) {
                            BufferedImage image = ImageIO.read(new ByteArrayInputStream(Base64.decodeBase64(window.getContent().getBase64Content())));
                            df.drawImage(image, window.getPosX() + window.getContent().getPositionX(), window.getPosY() + window.getContent().getPositionY());
                        }
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
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public String getClientId() {
        return clientId;
    }

    public void run() {
        sendKill();
    }

    public void eventDispatched(AWTEvent event) {
        if (event instanceof MouseEvent) {
            Point pos = df.getImagePanelPosition();
            MouseEvent me = (MouseEvent) event;
            JsonEventMouse msg = new JsonEventMouse();
            msg.clientId = "debug";
            msg.x = me.getX() - pos.x;
            msg.y = me.getY() - pos.y;
            switch (me.getButton()) {
                case MouseEvent.BUTTON1:
                    msg.button = 1;
                    break;
                case MouseEvent.BUTTON2:
                    msg.button = 2;
                    break;
                case MouseEvent.BUTTON3:
                    msg.button = 3;
                    break;
                case MouseEvent.NOBUTTON:
                    msg.button = 0;
                    break;
            }
            if (me.isAltDown()) {
                msg.alt = true;
            }
            if (me.isControlDown()) {
                msg.ctrl = true;
            }
            if (me.isShiftDown()) {
                msg.shift = true;
            }
            if (me.isMetaDown()) {
                msg.meta = true;
            }
            switch (me.getID()) {
                case MouseEvent.MOUSE_PRESSED:
                    msg.type = Type.mousedown;
                    break;
                case MouseEvent.MOUSE_RELEASED:
                    msg.type = Type.mouseup;
                    break;
                case MouseEvent.MOUSE_DRAGGED:
                    msg.button = 1;
                case MouseEvent.MOUSE_MOVED:
                    msg.type = Type.mousemove;
                    break;
                case MouseEvent.MOUSE_CLICKED:
                    if (me.getClickCount() == 2) {
                        msg.type = Type.dblclick;
                        break;
                    } else {
                        return;
                    }
                case MouseEvent.MOUSE_WHEEL:
                    msg.type = Type.mousewheel;
                    msg.wheelDelta = ((MouseWheelEvent) me).getWheelRotation();
                    break;
                default:
                    return;
            }
            send(msg);
        }

    }

}
