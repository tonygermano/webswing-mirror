package org.webswing.services.impl;

import java.io.Serializable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.jms.Connection;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.webswing.Constants;
import org.webswing.ext.services.ServerConnectionService;
import org.webswing.model.MsgIn;
import org.webswing.model.s2c.SimpleEventMsgOut;
import org.webswing.util.Logger;
import org.webswing.util.Util;

/**
 * @author Viktor_Meszaros This class is needed to achieve classpath isolation for swing application, all functionality dependent on external libs is implemented here.
 */
public class ServerConnectionServiceImpl implements MessageListener, ServerConnectionService {

	private static ServerConnectionServiceImpl impl;
	private static ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(Constants.JMS_URL);

	private Connection connection;
	private Session session;
	private MessageProducer producer;
	private long lastMessageTimestamp = System.currentTimeMillis();
	private Runnable watchdog;
	private ScheduledExecutorService exitScheduler = Executors.newSingleThreadScheduledExecutor();

	public static ServerConnectionServiceImpl getInstance() {
		if (impl == null) {
			impl = new ServerConnectionServiceImpl();
		}
		return impl;
	}

	public ServerConnectionServiceImpl() {
		watchdog = new Runnable() {

			@Override
			public void run() {
				long diff = System.currentTimeMillis() - lastMessageTimestamp;
				int timeout = Integer.parseInt(System.getProperty(Constants.SWING_SESSION_TIMEOUT_SEC, "300")) * 1000;
				if (diff / 1000 > 10) {
					Logger.info("Inactive for " + diff / 1000 + " seconds.");
				}
				if (diff > timeout) {
					Logger.info("Exiting swing application due to inactivity for " + diff / 1000 + " seconds.");
					System.exit(1);
				}
			}
		};
	}

	public void initialize() {
		try {
			String clientId = System.getProperty(Constants.SWING_START_SYS_PROP_CLIENT_ID);
			connection = connectionFactory.createConnection();
			connection.start();
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			Queue consumerDest = session.createQueue(clientId + Constants.SERVER2SWING);
			Queue producerDest = session.createQueue(clientId + Constants.SWING2SERVER);
			producer = session.createProducer(producerDest);
			session.createConsumer(consumerDest).setMessageListener(this);
			connection.setExceptionListener(new ExceptionListener() {

				@Override
				public void onException(JMSException paramJMSException) {
					Logger.warn("JMS clien connection error: " + paramJMSException.getMessage());
					try {
						producer.close();
						session.close();
						connection.close();
					} catch (JMSException e) {
						// do nothing, will try to reinitialize.
					}
					ServerConnectionServiceImpl.this.initialize();
				}
			});
			sendPidNotification();
		} catch (JMSException e) {
			Logger.error("Exiting swing application because could not connect to JMS:" + e.getMessage(), e);
			System.exit(1);
		}

		exitScheduler.scheduleWithFixedDelay(watchdog, 10, 10, TimeUnit.SECONDS);
	}

	@Override
	public void sendShutdownNotification() {
		try {
			producer.send(session.createObjectMessage(SimpleEventMsgOut.shutDownNotification));
		} catch (JMSException e) {
			Logger.error("ServerConnectionService.sendShutdownNotification", e);
		}
	}

	@SuppressWarnings("restriction")
	public void sendPidNotification() {
		try {
			java.lang.management.RuntimeMXBean runtime = java.lang.management.ManagementFactory.getRuntimeMXBean();
			java.lang.reflect.Field jvm = runtime.getClass().getDeclaredField("jvm");
			jvm.setAccessible(true);
			sun.management.VMManagement mgmt = (sun.management.VMManagement) jvm.get(runtime);
			java.lang.reflect.Method pid_method = mgmt.getClass().getDeclaredMethod("getProcessId");
			pid_method.setAccessible(true);
			int pid = (Integer) pid_method.invoke(mgmt);
			producer.send(session.createTextMessage(Constants.SWING_PID_NOTIFICATION + pid));
		} catch (Exception e) {
			Logger.error("ServerConnectionService.sendPidNotification", e);
		}
	}

	@Override
	public void sendJsonObject(Serializable o) {
		try {
			synchronized (this) {
				producer.send(session.createObjectMessage(o));
			}
		} catch (JMSException e) {
			Logger.error("ServerConnectionService.sendJsonObject", e);
		}
	}

	public void onMessage(Message msg) {
		try {
			lastMessageTimestamp = System.currentTimeMillis();
			if (msg instanceof ObjectMessage) {
				ObjectMessage omsg = (ObjectMessage) msg;
				if (omsg.getObject() instanceof MsgIn) {
					Util.getWebToolkit().getEventDispatcher().dispatchEvent((MsgIn) omsg.getObject());
				}
			}
		} catch (Exception e) {
			Logger.error("ServerConnectionService.onMessage", e);
		}
	}

}
