package org.webswing.server;

import java.io.Serializable;
import java.util.UUID;
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

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQDestination;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.webswing.Constants;
import org.webswing.model.MsgInternal;
import org.webswing.model.MsgOut;
import org.webswing.model.internal.ExitMsgInternal;
import org.webswing.model.internal.JvmStatsMsgInternal;
import org.webswing.model.internal.OpenFileResultMsgInternal;
import org.webswing.model.internal.PrinterJobResultMsgInternal;
import org.webswing.model.s2c.AppFrameMsgOut;
import org.webswing.model.s2c.LinkActionMsg;
import org.webswing.model.s2c.LinkActionMsg.LinkActionType;
import org.webswing.model.s2c.SimpleEventMsgOut;
import org.webswing.model.server.admin.SwingJvmStats;
import org.webswing.server.handler.FileServlet;

public class SwingJvmConnection implements MessageListener {

	private static final Logger log = LoggerFactory.getLogger(SwingJvmConnection.class);
	private static ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("vm://localhost");

	private Connection connection;
	private Session session;
	private Queue producerQueue;
	private Queue consumerQueue;
	private MessageProducer producer;
	private MessageConsumer consumer;
	private WebSessionListener webListener;
	private SwingJvmStats latest = new SwingJvmStats();

	private boolean jmsOpen = false;

	public SwingJvmConnection(String clientId, WebSessionListener webListener) throws JMSException {
		this.webListener = webListener;
		initialize(clientId);
	}

	private void initialize(final String clientId) throws JMSException {
		synchronized (this) {
			connection = connectionFactory.createConnection();
			connection.start();
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			producerQueue = session.createQueue(clientId + Constants.SERVER2SWING);
			consumerQueue = session.createQueue(clientId + Constants.SWING2SERVER);
			consumer = session.createConsumer(consumerQueue);
			consumer.setMessageListener(this);
			producer = session.createProducer(producerQueue);
			jmsOpen = true;
			connection.setExceptionListener(new ExceptionListener() {

				@Override
				public void onException(JMSException paramJMSException) {
					log.warn("JMS encountered an exception: " + paramJMSException.getMessage());
					try {
						synchronized (SwingJvmConnection.this) {
							consumer.close();
							producer.close();
							session.close();
							connection.close();
							jmsOpen = false;
						}
					} catch (JMSException e) {
						log.error("SwingJvmConnection:initialize1", e);
					}
					try {
						SwingJvmConnection.this.initialize(clientId);
					} catch (JMSException e) {
						log.error("SwingJvmConnection:initialize2", e);

					}
				}
			});
		}
	}

	public synchronized void send(Serializable o) {
		if (jmsOpen) {
			try {
				if (o instanceof String) {
					producer.send(session.createTextMessage((String) o));
				} else {
					producer.send(session.createObjectMessage(o));
				}
			} catch (JMSException e) {
				log.debug("SwingJvmConnection:send", e);
			}
		}
	}

	public void onMessage(Message m) {
		try {
			if (m instanceof ObjectMessage) {
				Serializable o = ((ObjectMessage) m).getObject();
				if (o instanceof MsgInternal) {
					if (o instanceof PrinterJobResultMsgInternal) {
						PrinterJobResultMsgInternal pj = (PrinterJobResultMsgInternal) o;
						boolean success = FileServlet.registerFile(pj.getPdfFile(), pj.getId(), 30, TimeUnit.MINUTES, webListener.getUser(), webListener.getInstanceId(), false, null);
						if (success) {
							AppFrameMsgOut f = new AppFrameMsgOut();
							LinkActionMsg linkAction = new LinkActionMsg(LinkActionType.print, pj.getId());
							f.setLinkAction(linkAction);
							webListener.sendToWeb(f);
						}
					} else if (o instanceof OpenFileResultMsgInternal) {
						OpenFileResultMsgInternal fr = (OpenFileResultMsgInternal) o;
						String id = UUID.randomUUID().toString();
						boolean success = FileServlet.registerFile(fr.getFile(), id, 30, TimeUnit.MINUTES, webListener.getUser(), webListener.getInstanceId(), fr.isWaitForFile(), fr.getOverwriteDetails());
						if (success) {
							AppFrameMsgOut f = new AppFrameMsgOut();
							LinkActionMsg linkAction = new LinkActionMsg(LinkActionType.file, id);
							f.setLinkAction(linkAction);
							webListener.sendToWeb(f);
						}
					} else if (o instanceof JvmStatsMsgInternal) {
						JvmStatsMsgInternal s = (JvmStatsMsgInternal) o;
						latest.setHeapSize(s.getHeapSize());
						latest.setHeapSizeUsed(s.getHeapSizeUsed());
					} else if (o instanceof ExitMsgInternal) {
						close();
						ExitMsgInternal e = (ExitMsgInternal) o;
						webListener.kill(e.getWaitForExit());
					}
				} else if (o instanceof MsgOut) {
					webListener.sendToWeb((MsgOut) o);
				}
			}
		} catch (Exception e) {
			log.error("SwingJvmConnection:onMessage", e);

		}

	}

	public void close() {
		try {
			synchronized (this) {
				try {
					consumer.close();
					producer.close();
					session.close();
				} finally {
					((ActiveMQConnection) connection).destroyDestination((ActiveMQDestination) consumerQueue);
					((ActiveMQConnection) connection).destroyDestination((ActiveMQDestination) consumerQueue);
					webListener.sendToWeb(SimpleEventMsgOut.shutDownNotification.buildMsgOut());
					connection.close();
					jmsOpen = false;
				}
			}
		} catch (Exception e) {
			log.error("SwingJvmConnection:close", e);
		}
		webListener.notifyExiting();
	}

	public SwingJvmStats getLatest() {
		return latest;
	}

	public interface WebSessionListener {

		String getInstanceId();

		String getUser();

		void sendToWeb(MsgOut o);

		void notifyExiting();

		void kill(int waitMs);
	}
}
