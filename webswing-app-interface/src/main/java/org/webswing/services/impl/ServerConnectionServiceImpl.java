package org.webswing.services.impl;

import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.lang.management.LockInfo;
import java.lang.management.ManagementFactory;
import java.lang.management.MonitorInfo;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.net.URI;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.Topic;
import javax.swing.SwingUtilities;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.commons.io.FileUtils;
import org.webswing.Constants;
import org.webswing.ext.services.ServerConnectionService;
import org.webswing.model.MsgIn;
import org.webswing.model.SyncMsg;
import org.webswing.model.UserInputMsgIn;
import org.webswing.model.c2s.SimpleEventMsgIn;
import org.webswing.model.c2s.SimpleEventMsgIn.SimpleEventType;
import org.webswing.model.internal.ApiEventMsgInternal;
import org.webswing.model.internal.JvmStatsMsgInternal;
import org.webswing.model.internal.ThreadDumpMsgInternal;
import org.webswing.model.internal.ThreadDumpRequestMsgInternal;
import org.webswing.model.jslink.JavaEvalRequestMsgIn;
import org.webswing.model.s2c.AccessibilityMsg;
import org.webswing.model.s2c.SimpleEventMsgOut;
import org.webswing.toolkit.api.WebswingMessagingApi;
import org.webswing.toolkit.api.WebswingUtil;
import org.webswing.toolkit.api.lifecycle.ShutdownReason;
import org.webswing.toolkit.jslink.WebJSObject;
import org.webswing.toolkit.util.CpuMonitor;
import org.webswing.toolkit.util.DeamonThreadFactory;
import org.webswing.toolkit.util.Logger;
import org.webswing.toolkit.util.Util;
import org.webswing.util.AccessibilityUtil;
import org.webswing.util.CheckSerializedSize;

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
	private AtomicLong lastMessageTimestamp = new AtomicLong(System.currentTimeMillis());
	private AtomicLong lastUserInputTimestamp = new AtomicLong(System.currentTimeMillis());
	private Runnable watchdog;
	private ScheduledExecutorService exitScheduler = Executors.newSingleThreadScheduledExecutor(DeamonThreadFactory.getInstance("Webswing Shutdown scheduler"));
	private ExecutorService jmsSender = Executors.newSingleThreadExecutor(DeamonThreadFactory.getInstance("Webswing JMS Sender"));

	private Map<String, Object> syncCallResposeMap = new ConcurrentHashMap<String, Object>();
	private boolean closed;

	private MessageProducer mgsApiProducer;
	private MessageConsumer mgsApiConsumer;

	private final Object delayedShutdownScheduleLock = new Object();
	private ScheduledFuture<?> delayedShutdownFuture;
	private boolean schedulingShutdown;
	private AtomicBoolean terminated = new AtomicBoolean(false);
	
	private Boolean statisticsLoggingEnabled; // unset if null -> take default value from config (system property)

	public static ServerConnectionServiceImpl getInstance() {
		if (impl == null) {
			impl = new ServerConnectionServiceImpl();
		}
		return impl;
	}

	public ServerConnectionServiceImpl() {
		messageApiSharedTopicName = System.getProperty(Constants.SWING_START_SYS_PROP_MSG_API_TOPIC, "");
		connectionFactory = new ActiveMQConnectionFactory(System.getProperty(Constants.JMS_URL));
		connectionFactory.setAlwaysSessionAsync(false);
		connectionFactory.setTrustAllPackages(true);
		watchdog = new Runnable() {
			boolean exitDumpCreated = false;
			AtomicInteger pingEventDispatchThread = new AtomicInteger(0);

			@Override
			public void run() {
				try {
					int timeoutSec = Integer.parseInt(System.getProperty(Constants.SWING_SESSION_TIMEOUT_SEC, "300"));
					synchronized (delayedShutdownScheduleLock) {
						if ( delayedShutdownFuture!=null && !delayedShutdownFuture.isDone()) {
							if (delayedShutdownFuture.getDelay(TimeUnit.SECONDS) % 60 == 0) {
								Logger.warn("Application shutdown expected in " + delayedShutdownFuture.getDelay(TimeUnit.SECONDS) + " secongs");
							}
							timeoutSec = -1; //if shutdown is scheduled, we ignore the timeout setting
						}
					}

					// IE halts js execution when filechooser is open and no heartbeat messages are sent causing timeout(see https://bitbucket.org/webswing/webswing-home/issues/18)
					if (timeoutSec >= 0 && Util.getWebToolkit().getPaintDispatcher() != null && Util.getWebToolkit().getPaintDispatcher().getFileChooserDialog() != null && WebswingUtil.getWebswingApi().getPrimaryUser() != null) {
						int minTimeoutSecIfFileChooserActive = Integer.parseInt(System.getProperty(Constants.SWING_SESSION_TIMEOUT_SEC_IF_FILECHOOSER_ACTIVE, "1800"));
						timeoutSec = Math.max(timeoutSec, minTimeoutSecIfFileChooserActive);
					}

					boolean timeoutIfInactive = Boolean.getBoolean(Constants.SWING_SESSION_TIMEOUT_IF_INACTIVE) && timeoutSec > 0;
					if (timeoutSec >= 0) {
						long lastTstp = timeoutIfInactive ? lastUserInputTimestamp.get() : lastMessageTimestamp.get() + 10000;/*+10000 is to compensate for 10s js heartbeat interval*/
						long diff = System.currentTimeMillis() - lastTstp;
						int timeoutMs = timeoutSec * 1000;
						timeoutMs = Math.max(1000, timeoutMs);
						if ((diff / 1000 > 10)) {
							String msg = timeoutIfInactive ? "User" : "Session";
							if ((diff / 1000) % 60 == 0) {
								Logger.warn(msg + " inactive for " + diff / 1000 + " seconds." + (terminated.get() ? "[waiting for application to stop]" : ""));
							}
							if (!terminated.get() && (timeoutMs - diff < 60000)) { //countdown message for the last 60 seconds
								sendObject(SimpleEventMsgOut.sessionTimeoutWarning.buildMsgOut());
							}
							Integer waitForExit = Integer.getInteger(Constants.SWING_START_SYS_PROP_WAIT_FOR_EXIT, 30000);
							if (terminated.get() && !exitDumpCreated && diff > (timeoutMs + waitForExit - 5000)) {
								sendObject(getThreadDumpMsg("Before-Kill Thread Dump"));
								Logger.warn("Application did not exit gracefully within " + waitForExit / 1000 + " seconds. Application will be forcefully terminated. Thread dump has been generated.");
								exitDumpCreated=true;
							}

							if (terminated.get() && diff > (timeoutMs + waitForExit + 10000)) {// if still alive 10 seconds after server should have killed this process
								Logger.warn("Application has not been forcefully terminated by server. Exiting Manually.");
								System.exit(1);
							}
						}

						if (diff > timeoutMs) {
							if (!terminated.get()) {//only call once
								scheduleShutdown(ShutdownReason.Inactivity,() -> {
									Logger.warn("Exiting application due to inactivity for " + diff / 1000 + " seconds.");
									sendObject(SimpleEventMsgOut.sessionTimedOutNotification.buildMsgOut());
								});
							}
						}
					}
					if (!terminated.get()) {
						sendObject(getStats(pingEventDispatchThread.get()));
						if (pingEventDispatchThread.getAndIncrement() == 10) {//if value is not reset after 10 seconds - EDT is stuck
							Logger.warn("Application is not responding for 10 seconds. Thread dump generated . ");
							sendObject(getThreadDumpMsg("Application not responding"));
						} else {
							SwingUtilities.invokeLater(new Runnable() {
								@Override
								public void run() {
									pingEventDispatchThread.set(0);
								}
							});
						}
					}

				} catch (Throwable e) {
					Logger.error("Exception in webswing shutdown scheduler", e);
				}
			}
		};
		
		AccessibilityUtil.registerAccessibilityListeners();
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
						if (msgtype != null && Util.getWebToolkit().messageApiHasListenerForClass(msgtype)) {
							Util.getWebToolkit().messageApiProcessMessage(((ObjectMessage) message).getObject());
						}
					} catch (Exception e) {
						Logger.error("Failed to process message", e);
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

	@Override
	public void scheduleShutdown(ShutdownReason reason) {
		scheduleShutdown(reason,()->{});
	}

	private void scheduleShutdown(ShutdownReason reason,Runnable shutdownlogic) {
		synchronized (delayedShutdownScheduleLock) {
			try {
				schedulingShutdown = true;
				if (delayedShutdownFuture == null || delayedShutdownFuture.isDone()) {
					int delaySeconds = Util.getWebToolkit().executeOnBeforeShutdownListeners(reason);
					delayedShutdownFuture = exitScheduler.schedule(() -> {
						shutdownlogic.run();
						terminated.set(true);
						Util.getWebToolkit().exitSwing(0);
					}, delaySeconds, TimeUnit.SECONDS);
					Logger.info("(" + reason + ") Application Shutdown scheduled. (delayed by " + delayedShutdownFuture.getDelay(TimeUnit.SECONDS) + " seconds).");
				} else {
					Logger.info("(" + reason + ") Application Shutdown request ignored. Delayed shutdown already scheduled in " + delayedShutdownFuture.getDelay(TimeUnit.SECONDS) + " seconds.");
				}
			}finally{
				schedulingShutdown=false;
			}
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
		synchronized (delayedShutdownScheduleLock) {
			if (schedulingShutdown) {
				//if resetInactivityTimers called from onBeforeShutdown listener, we need to defer execution, because the shutdown timer is not yet created.
				exitScheduler.schedule(() -> resetInactivityTimers(), 1, TimeUnit.MILLISECONDS);
			} else {
				lastMessageTimestamp.getAndSet(System.currentTimeMillis());
				lastUserInputTimestamp.getAndSet(System.currentTimeMillis());
				if (delayedShutdownFuture != null && !delayedShutdownFuture.isDone()) {
					Logger.warn("Cancelling Delayed Application Shutdown expected in " + delayedShutdownFuture.getDelay(TimeUnit.SECONDS) + " seconds.");
					delayedShutdownFuture.cancel(false);
				}
			}
		}
	}
	
	private boolean isStatisticsLoggingEnabled() {
		if (statisticsLoggingEnabled != null) {
			return statisticsLoggingEnabled;
		}
		return Boolean.getBoolean(Constants.SWING_START_SYS_PROP_STATISTICS_LOGGING_ENABLED);
	}
	
	private void sendJmsMessage(final Serializable o) throws JMSException {
		try {
			jmsSender.submit(new Callable<Object>() {

				@Override
				public Object call() throws Exception {
					producer.send(session.createObjectMessage(o), DeliveryMode.NON_PERSISTENT, 4, 3000);
					return null;
				}
			}).get(4, TimeUnit.SECONDS);
		} catch (IllegalStateException e) {
			Logger.warn("ServerConnectionService.sendJmsMessage: " + e.getMessage());
		} catch (InterruptedException e) {
			Logger.error("ServerConnectionService.sendJmsMessage: Sending frame interrupted.", e);
		} catch (ExecutionException e) {
			if (e.getCause() instanceof JMSException) {
				throw (JMSException) e.getCause();
			} else {
				Logger.error("ServerConnectionService.sendJmsMessage", e);
				throw new JMSException(e.getMessage());
			}
		} catch (TimeoutException e) {
			Logger.error("ServerConnectionService.sendJmsMessage: Sending frame timed out. (frame size:" + CheckSerializedSize.getSerializedSize(o) + ")", e);
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

	@Override
	public AccessibilityMsg getAccessibilityInfo() {
		return AccessibilityUtil.getAccessibilityInfo();
	}
	
	@Override
	public AccessibilityMsg getAccessibilityInfo(Component c, int x, int y) {
		return AccessibilityUtil.getAccessibilityInfo(c, x, y);
	}
	
	public void onMessage(Message msg) {
		try {
			lastMessageTimestamp.getAndSet(System.currentTimeMillis());
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
						lastUserInputTimestamp.getAndSet(System.currentTimeMillis());
					}
					if (omsg.getObject() instanceof SimpleEventMsgIn) {
						SimpleEventMsgIn simpleEvent = (SimpleEventMsgIn) omsg.getObject();
						if (simpleEvent.getType() == SimpleEventType.enableStatisticsLogging) {
							statisticsLoggingEnabled = true;
						} else if (simpleEvent.getType() == SimpleEventType.disableStatisticsLogging) {
							statisticsLoggingEnabled = false;
						}
					}
					if (Util.getWebToolkit().getEventDispatcher() != null) {//ignore events if WebToolkit is not ready yet
						Util.getWebToolkit().getEventDispatcher().dispatchEvent((MsgIn) omsg.getObject());
					}
				} else if (omsg.getObject() instanceof ApiEventMsgInternal) {
					Util.getWebToolkit().processApiEvent((ApiEventMsgInternal) omsg.getObject());
				} else if (omsg.getObject() instanceof ThreadDumpRequestMsgInternal) {
					exitScheduler.execute(new Runnable() {
						@Override
						public void run() {
							sendObject(getThreadDumpMsg("User requested thread dump"));
						}
					});
				}
			}
		} catch (Exception e) {
			Logger.error("ServerConnectionService.onMessage", e);
		}
	}

	private JvmStatsMsgInternal getStats(int edtPing) {
		JvmStatsMsgInternal result = new JvmStatsMsgInternal();
		
		if (isStatisticsLoggingEnabled()) {
			int mb = 1024 * 1024;
			Runtime runtime = Runtime.getRuntime();
			result.setHeapSize(runtime.maxMemory() / mb);
			result.setHeapSizeUsed((runtime.totalMemory() - runtime.freeMemory()) / mb);
			result.setCpuUsage(CpuMonitor.getCpuUtilization());
		}
		
		result.setEdtPingSeconds(edtPing);
		return result;
	}

	private ThreadDumpMsgInternal getThreadDumpMsg(String reason) {
		ThreadDumpMsgInternal msg = new ThreadDumpMsgInternal();
		msg.setTimestamp(System.currentTimeMillis());
		msg.setReason(reason);
		msg.setDump(saveThreadDump(reason));
		return msg;
	}

	public static String saveThreadDump(String reason) {
		final StringBuilder dump = new StringBuilder();
		final ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
		final ThreadInfo[] threadInfos = threadMXBean.getThreadInfo(threadMXBean.getAllThreadIds(), true, true);
		for (ThreadInfo threadInfo : threadInfos) {
			if(threadInfo!=null) {
				dump.append(threadInfoToString(threadInfo));
				dump.append("\n");
			}
		}
		dump.toString();

		String tempDir = System.getProperty(Constants.TEMP_DIR_PATH);
		String clientId = System.getProperty(Constants.SWING_START_SYS_PROP_CLIENT_ID);
		String timestamp = new SimpleDateFormat("yy.MM.dd.HH.mm.ss").format(new Date());
		try {
			File file = new File(URI.create(tempDir + "ThreadDump-" + timestamp + "-" + URLEncoder.encode(clientId + "-" + reason, "UTF-8") + ".txt"));
			FileUtils.write(file, dump.toString());
			return file.getAbsolutePath();
		} catch (Exception e) {
			Logger.error("Failed to save thread dump. ", e);
		}
		return null;
	}

	private static String threadInfoToString(ThreadInfo ti) {
		StringBuilder sb = new StringBuilder("\"" + ti.getThreadName() + "\"" + " Id=" + ti.getThreadId() + " " + ti.getThreadState());
		if (ti.getLockName() != null) {
			sb.append(" on " + ti.getLockName());
		}
		if (ti.getLockOwnerName() != null) {
			sb.append(" owned by \"" + ti.getLockOwnerName() + "\" Id=" + ti.getLockOwnerId());
		}
		if (ti.isSuspended()) {
			sb.append(" (suspended)");
		}
		if (ti.isInNative()) {
			sb.append(" (in native)");
		}
		sb.append('\n');
		int i = 0;
		StackTraceElement[] stackTrace = ti.getStackTrace();
		for (; i < stackTrace.length; i++) {
			StackTraceElement ste = stackTrace[i];
			sb.append("\tat " + ste.toString());
			sb.append('\n');
			if (i == 0 && ti.getLockInfo() != null) {
				Thread.State ts = ti.getThreadState();
				switch (ts) {
				case BLOCKED:
					sb.append("\t-  blocked on " + ti.getLockInfo());
					sb.append('\n');
					break;
				case WAITING:
					sb.append("\t-  waiting on " + ti.getLockInfo());
					sb.append('\n');
					break;
				case TIMED_WAITING:
					sb.append("\t-  waiting on " + ti.getLockInfo());
					sb.append('\n');
					break;
				default:
				}
			}

			MonitorInfo[] lockedMonitors = ti.getLockedMonitors();
			for (MonitorInfo mi : lockedMonitors) {
				if (mi.getLockedStackDepth() == i) {
					sb.append("\t-  locked " + mi);
					sb.append('\n');
				}
			}
		}
		if (i < stackTrace.length) {
			sb.append("\t...");
			sb.append('\n');
		}

		LockInfo[] locks = ti.getLockedSynchronizers();
		if (locks.length > 0) {
			sb.append("\n\tNumber of locked synchronizers = " + locks.length);
			sb.append('\n');
			for (LockInfo li : locks) {
				sb.append("\t- " + li);
				sb.append('\n');
			}
		}
		sb.append('\n');
		return sb.toString();
	}

}
