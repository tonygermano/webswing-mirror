package org.webswing.services.impl;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.webswing.Constants;
import org.webswing.ext.services.ServerConnectionService;
import org.webswing.model.MsgIn;
import org.webswing.model.SyncMsg;
import org.webswing.model.UserInputMsgIn;
import org.webswing.model.internal.ApiEventMsgInternal;
import org.webswing.model.internal.JvmStatsMsgInternal;
import org.webswing.model.jslink.JavaEvalRequestMsgIn;
import org.webswing.model.s2c.SimpleEventMsgOut;
import org.webswing.toolkit.api.WebswingMessagingApi;
import org.webswing.toolkit.jslink.WebJSObject;
import org.webswing.toolkit.util.DeamonThreadFactory;
import org.webswing.toolkit.util.Logger;
import org.webswing.toolkit.util.Util;

import javax.jms.*;
import java.io.IOException;
import java.io.Serializable;
import java.lang.IllegalStateException;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.Map;
import java.util.concurrent.*;

/**
 * @author Viktor_Meszaros This class is needed to achieve classpath isolation for swing application, all functionality dependent on external libs is implemented here.
 */
public class ServerConnectionServiceImpl implements MessageListener, ServerConnectionService {

	private static ServerConnectionServiceImpl impl;
	private static ActiveMQConnectionFactory connectionFactory;
	private static long syncTimeout = Long.getLong(Constants.SWING_START_SYS_PROP_SYNC_TIMEOUT, 3000);

	private String messageApiSharedTopicName;
	private Connection connection;
	private Session session;
	private MessageProducer producer;
	private MessageConsumer consumer;
	private long lastMessageTimestamp = System.currentTimeMillis();
	private long lastUserInputTimestamp = System.currentTimeMillis();
	private Runnable watchdog;
	private ScheduledExecutorService exitScheduler = Executors.newSingleThreadScheduledExecutor(DeamonThreadFactory.getInstance());
	private ExecutorService jmsSender = Executors.newSingleThreadExecutor(DeamonThreadFactory.getInstance());

	private Map<String, Object> syncCallResposeMap = new ConcurrentHashMap<String, Object>();
	private boolean closed;

	private MessageProducer mgsApiProducer;
	private MessageConsumer mgsApiConsumer;

	public static ServerConnectionServiceImpl getInstance() {
		if (impl == null) {
			impl = new ServerConnectionServiceImpl();
		}
		return impl;
	}

	public ServerConnectionServiceImpl() {
		messageApiSharedTopicName = System.getProperty(Constants.SWING_START_SYS_PROP_MSG_API_TOPIC,"");
		connectionFactory = new ActiveMQConnectionFactory(System.getProperty(Constants.JMS_URL));
		connectionFactory.setAlwaysSessionAsync(false);
		connectionFactory.setTrustAllPackages(true);
		watchdog = new Runnable() {
			private boolean terminated = false;

			@Override
			public void run() {
				int timeoutSec = Integer.parseInt(System.getProperty(Constants.SWING_SESSION_TIMEOUT_SEC, "300"));
				boolean timeoutIfInactive = Boolean.getBoolean(Constants.SWING_SESSION_TIMEOUT_IF_INACTIVE) && timeoutSec > 0;
				if (timeoutSec >= 0) {
					long lastTstp = timeoutIfInactive ? lastUserInputTimestamp : lastMessageTimestamp + 10000;/*+10000 is to compensate for 10s js heartbeat interval*/
					long diff = System.currentTimeMillis() - lastTstp;
					int timeoutMs = timeoutSec * 1000;
					timeoutMs = Math.max(1000, timeoutMs);
					if ((diff / 1000 > 10)) {
						String msg = timeoutIfInactive ? "User" : "Session";
						if ((diff / 1000) % 10 == 0) {
							Logger.warn(msg + " inactive for " + diff / 1000 + " seconds." + (terminated ? "[waiting for application to stop]" : ""));
						}
						if (!terminated && (timeoutMs - diff < 60000)) { //countdown message for the last 60 seconds
							sendObject(SimpleEventMsgOut.sessionTimeoutWarning.buildMsgOut());
						}
					}

					if (diff > timeoutMs) {
						if (!terminated) {//only call once
							terminated = true;
							//TODO check for deadlock once
							Logger.warn("Exiting application due to inactivity for " + diff / 1000 + " seconds.");
							sendObject(SimpleEventMsgOut.sessionTimedOutNotification.buildMsgOut());
							Util.getWebToolkit().exitSwing(1);
						}
					}
				}
				if (!terminated) {
					sendObject(getStats());
				}
			}
		};
	}

	public void initialize() {
		try {
			String jmsQueueId = System.getProperty(Constants.SWING_START_SYS_PROP_JMS_ID);
			connection = connectionFactory.createConnection();
			connection.start();
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			Queue consumerDest = session.createQueue(jmsQueueId + Constants.SERVER2SWING);
			Queue producerDest = session.createQueue(jmsQueueId + Constants.SWING2SERVER);
			producer = session.createProducer(producerDest);
			consumer = session.createConsumer(consumerDest);
			consumer.setMessageListener(this);

			final Topic msgApisharedTopic = session.createTopic(messageApiSharedTopicName);
			mgsApiProducer = session.createProducer(msgApisharedTopic);
			mgsApiConsumer = session.createConsumer(msgApisharedTopic);
			mgsApiConsumer.setMessageListener(new MessageListener() {
				@Override
				public void onMessage(Message message) {
					try {
						String msgtype = message.getStringProperty(WebswingMessagingApi.MSG_API_TYPE);
						if(msgtype!=null && Util.getWebToolkit().messageApiHasListenerForClass(msgtype)){
							Util.getWebToolkit().messageApiProcessMessage(((ObjectMessage)message).getObject());
						}
					} catch (Exception e) {
						Logger.error("Failed to process message",e);
					}
				}
			});

			connection.setExceptionListener(new ExceptionListener() {

				@Override
				public void onException(JMSException paramJMSException) {
					Logger.warn("JMS clien connection error: " + paramJMSException.getMessage());
					try {
						closeJMS();
					} catch (JMSException e) {
						// do nothing, will try to reinitialize.
					}
					ServerConnectionServiceImpl.this.initialize();
				}
			});

		} catch (JMSException e) {
			Logger.error("Exiting application because could not connect to JMS:" + e.getMessage(), e);
			System.exit(1);
		}

		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				ServerConnectionServiceImpl.this.disconnect();
			}
		});

		exitScheduler.scheduleWithFixedDelay(watchdog, 1, 1, TimeUnit.SECONDS);
	}

	public void messageApiPublish(Serializable o) throws IOException {
		try {
			Message m = session.createObjectMessage(o);
			m.setStringProperty(WebswingMessagingApi.MSG_API_TYPE, o.getClass().getCanonicalName());
			mgsApiProducer.send(m);
		} catch (Exception e) {
			Logger.error("Failed to send message: ", e);
			throw new IOException("Failed to send message: " + e.getMessage());
		}
	}

	public synchronized void disconnect() {
		try {
			closeJMS();
		} catch (JMSException e) {
			Logger.info("Disconnecting from JMS server failed.", e.getMessage());
		} finally {
			closed = true;
		}
	}

	private void closeJMS() throws JMSException {
		producer.close();
		consumer.close();
		mgsApiProducer.close();
		mgsApiConsumer.close();
		session.close();
		connection.close();
	}

	@Override
	public void resetInactivityTimers() {
		lastMessageTimestamp = System.currentTimeMillis();
		lastUserInputTimestamp = System.currentTimeMillis();
	}

	private void sendJmsMessage(final Serializable o) throws JMSException {
		try {
			jmsSender.submit(new Callable<Object>() {

				@Override
				public Object call() throws Exception {
					producer.send(session.createObjectMessage(o));
					return null;
				}
			}).get();
		} catch (IllegalStateException e) {
			Logger.warn("ServerConnectionService.sendJmsMessage: " + e.getMessage());
		} catch (InterruptedException e) {
		} catch (ExecutionException e) {
			if (e.getCause() instanceof JMSException) {
				throw (JMSException) e.getCause();
			} else {
				Logger.error("ServerConnectionService.sendJmsMessage", e);
				throw new JMSException(e.getMessage());
			}
		}
	}

	@Override
	public void sendObject(Serializable o) {
		if (!closed) {
			try {
				sendJmsMessage(o);
			} catch (JMSException e) {
				Logger.error("ServerConnectionService.sendJsonObject", e);
			}
		}
	}

	@Override
	public Object sendObjectSync(Serializable o, String correlationId) throws TimeoutException, IOException {
		if (!closed) {
			try {
				Object syncObject = new Object();
				syncCallResposeMap.put(correlationId, syncObject);
				sendJmsMessage(o);
				Object result = null;
				try {
					synchronized (syncObject) {
						syncObject.wait(syncTimeout);
					}
				} catch (InterruptedException e) {
				}

				result = syncCallResposeMap.get(correlationId);
				syncCallResposeMap.remove(correlationId);
				if (result == syncObject) {
					throw new TimeoutException("Call timed out after " + syncTimeout + " ms. Call id " + correlationId);
				}
				return result;

			} catch (JMSException e) {
				Logger.error("ServerConnectionService.sendJsonObject", e);
				throw new IOException(e.getMessage());
			}
		} else {
			throw new IOException("Failed to send request. JMS was disconnected.");
		}
	}

	public void onMessage(Message msg) {
		try {
			lastMessageTimestamp = System.currentTimeMillis();
			if (msg instanceof ObjectMessage) {
				ObjectMessage omsg = (ObjectMessage) msg;
				try {
					omsg.getObject();
				} catch (Exception jMSException) {
					Logger.error("Failed to read message from JMS", jMSException);
				}
				if (omsg.getObject() instanceof JavaEvalRequestMsgIn) {
					JavaEvalRequestMsgIn javaReq = (JavaEvalRequestMsgIn) omsg.getObject();
					WebJSObject.evaluateJava(javaReq);
				} else if (omsg.getObject() instanceof SyncMsg) {
					SyncMsg syncmsg = (SyncMsg) omsg.getObject();
					String correlationId = syncmsg.getCorrelationId();
					if (syncCallResposeMap.containsKey(correlationId)) {
						Object syncObject = syncCallResposeMap.get(correlationId);
						syncCallResposeMap.put(correlationId, omsg.getObject());
						synchronized (syncObject) {
							syncObject.notifyAll();
						}
					} else {
						Logger.warn("No thread waiting for sync-ed message with id ", correlationId);
					}
				} else if (omsg.getObject() instanceof MsgIn) {
					if (omsg.getObject() instanceof UserInputMsgIn) {
						lastUserInputTimestamp = System.currentTimeMillis();
					}
					if (Util.getWebToolkit().getEventDispatcher() != null) {//ignore events if WebToolkit is not ready yet
						Util.getWebToolkit().getEventDispatcher().dispatchEvent((MsgIn) omsg.getObject());
					}
				} else if (omsg.getObject() instanceof ApiEventMsgInternal) {
					Util.getWebToolkit().processApiEvent((ApiEventMsgInternal) omsg.getObject());
				}
			}
		} catch (Exception e) {
			Logger.error("ServerConnectionService.onMessage", e);
		}
	}

	private JvmStatsMsgInternal getStats() {
		JvmStatsMsgInternal result = new JvmStatsMsgInternal();
		int mb = 1024 * 1024;
		Runtime runtime = Runtime.getRuntime();
		result.setHeapSize(runtime.maxMemory() / mb);
		result.setHeapSizeUsed((runtime.totalMemory() - runtime.freeMemory()) / mb);
		result.setCpuUsage(CpuMonitor.getCpuUtilization());
		return result;
	}

	private static class CpuMonitor {
		static long previousCPUTime = 0;
		static long previousTime = 0;

		static {
			getCpuUtilization();
		}

		static double getCpuUtilization() {
			long currentCpuTime = 0;
			ThreadMXBean tmbean = ManagementFactory.getThreadMXBean();
			long[] tids = tmbean.getAllThreadIds();
			ThreadInfo[] tinfos = tmbean.getThreadInfo(tids);

			for (int i = 0; i < tids.length; i++) {
				long cpuTime = tmbean.getThreadCpuTime(tids[i]);
				if (cpuTime != -1 && tinfos[i] != null) {
					currentCpuTime += cpuTime;
				}
			}
			long cpuTimeDelta = currentCpuTime - previousCPUTime;
			long timeDelta = System.currentTimeMillis() - previousTime;
			previousCPUTime = currentCpuTime;
			previousTime = System.currentTimeMillis();
			int processors = Runtime.getRuntime().availableProcessors();
			if (timeDelta == 0 || processors == 0) {
				return 0;
			}
			double cpuUsage = (double) TimeUnit.NANOSECONDS.toMillis(cpuTimeDelta) / (double) timeDelta;
			cpuUsage = cpuUsage / processors;
			return Math.max(0, cpuUsage) * 100;
		}
	}

}
