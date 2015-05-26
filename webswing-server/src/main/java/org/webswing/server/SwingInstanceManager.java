package org.webswing.server;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.atmosphere.cpr.AtmosphereResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.webswing.model.MsgIn;
import org.webswing.model.admin.s2c.AdminConsoleFrameMsgOut;
import org.webswing.model.admin.s2c.SwingSessionMsg;
import org.webswing.model.c2s.ConnectionHandshakeMsgIn;
import org.webswing.model.s2c.SimpleEventMsgOut;
import org.webswing.model.server.SwingDescriptor;
import org.webswing.server.stats.PerformanceStatsMonitor;
import org.webswing.server.stats.jmx.WebswingMonitoringMXBeanImpl;
import org.webswing.server.util.ServerUtil;

public class SwingInstanceManager {

	private static SwingInstanceManager instance = new SwingInstanceManager();
	private static final Logger log = LoggerFactory.getLogger(SwingInstanceManager.class);

	private List<SwingSessionMsg> closedInstances = new ArrayList<SwingSessionMsg>();
	private Map<String, SwingInstance> swingInstances = new ConcurrentHashMap<String, SwingInstance>();
	private SwingInstanceChangeListener changeListener;

	private SwingInstanceManager() {
		new PerformanceStatsMonitor();
		MBeanServer platformMBeanServer = ManagementFactory.getPlatformMBeanServer();
		try {
			ObjectName mxbeanName = new ObjectName("org.webswing:type=WebswingMonitoring");
			try {
				platformMBeanServer.unregisterMBean(mxbeanName);
			} catch (Exception e) {
				// do nothing
			}
			platformMBeanServer.registerMBean(new WebswingMonitoringMXBeanImpl(), mxbeanName);
		} catch (Exception e) {
			log.error("Failed to register webswing monitoring jmx mbean", e);
		}
	}

	public static SwingInstanceManager getInstance() {
		return instance;
	}

	public synchronized Set<SwingInstance> getSwingInstanceSet() {
		Set<SwingInstance> set = new HashSet<SwingInstance>(swingInstances.values());
		return set;
	}

	public void connectSwingInstance(AtmosphereResource resource, ConnectionHandshakeMsgIn h) {
		SwingDescriptor app;
		if (h.isApplet()) {
			app = ConfigurationManager.getInstance().getApplet(h.getApplicationName());
		} else {
			app = ConfigurationManager.getInstance().getApplication(h.getApplicationName());
		}
		if (app == null) {
			if (h.isApplet()) {
				throw new RuntimeException("Applet " + h.getApplicationName() + " is not configured.");
			} else {
				throw new RuntimeException("Application " + h.getApplicationName() + " is not configured.");
			}
		}
		if (ServerUtil.isUserAuthorized(resource, app, h)) {
			SwingInstance swingInstance = swingInstances.get(h.getClientId());
			if (swingInstance == null) {// start new swing app
				if (app != null && !h.isMirrored()) {
					if (!reachedMaxConnections(app)) {
						swingInstance = new SwingInstance(h, app, resource);
						synchronized (this) {
							swingInstances.put(h.getClientId(), swingInstance);
						}
						notifySwingChangeChange();
					} else {
						ServerUtil.broadcastMessage(resource, SimpleEventMsgOut.tooManyClientsNotification.buildMsgOut());
					}
				} else {
					ServerUtil.broadcastMessage(resource, SimpleEventMsgOut.configurationError.buildMsgOut());
				}
			} else {
				if (h.isMirrored()) {// connect as mirror viewer
					notifySessionDisconnected(resource.uuid());// disconnect possible running mirror sessions
					boolean result = swingInstance.registerMirroredWebSession(resource);
					if (!result) {
						ServerUtil.broadcastMessage(resource, SimpleEventMsgOut.applicationAlreadyRunning.buildMsgOut());
					}
				} else {// continue old session?
					if (h.getSessionId() != null && h.getSessionId().equals(swingInstance.getSessionId())) {
						swingInstance.sendToSwing(resource, h);
					} else {
						boolean result = swingInstance.registerPrimaryWebSession(resource);
						if (result) {
							ServerUtil.broadcastMessage(resource, SimpleEventMsgOut.continueOldSession.buildMsgOut());
							notifySwingChangeChange();
						} else {
							ServerUtil.broadcastMessage(resource, SimpleEventMsgOut.applicationAlreadyRunning.buildMsgOut());
						}
					}
				}
			}
		} else {
			log.error("Authorization error: User " + ServerUtil.getUserName(resource) + " is not authorized to connect to application " + app.getName() + (h.isMirrored() ? " [Mirrored view only available for admin role]" : ""));
		}
	}

	private boolean reachedMaxConnections(SwingDescriptor app) {
		if (app.getMaxClients() < 0) {
			return false;
		} else if (app.getMaxClients() == 0) {
			return true;
		} else {
			int count = 0;
			for (SwingInstance si : getSwingInstanceSet()) {
				if (app.getName().equals(si.getApplicationName()) && si.isRunning()) {
					count++;
				}
			}
			if (count < app.getMaxClients()) {
				return false;
			} else {
				return true;
			}
		}
	}

	public synchronized void notifySwingClose(SwingInstance swingInstance) {
		closedInstances.add(ServerUtil.composeSwingInstanceStatus(swingInstance));
		swingInstances.remove(swingInstance.getClientId());
		notifySwingChangeChange();
	}

	public synchronized AdminConsoleFrameMsgOut extractStatus() {
		AdminConsoleFrameMsgOut result = new AdminConsoleFrameMsgOut();
		for (SwingInstance si : getSwingInstanceSet()) {
			result.getSessions().add(ServerUtil.composeSwingInstanceStatus(si));
		}
		result.setClosedSessions(closedInstances);
		return result;
	}

	public void notifySessionDisconnected(String uuid) {
		Set<SwingInstance> set = getSwingInstanceSet();
		for (SwingInstance i : set) {
			if (i.getSessionId() != null && i.getSessionId().equals(uuid)) {
				i.registerPrimaryWebSession(null);
			} else if (i.getMirroredSessionId() != null && i.getMirroredSessionId().equals(uuid)) {
				i.registerMirroredWebSession(null);
			}
		}
	}

	public void notifySwingChangeChange() {
		if (changeListener != null) {
			changeListener.swingInstancesChanged();
		}
	}

	public void notifySwingChangeStats() {
		if (changeListener != null) {
			changeListener.swingInstancesChangedStats();
		}
	}

	public void setChangeListener(SwingInstanceChangeListener changeListener) {
		this.changeListener = changeListener;
	}

	public interface SwingInstanceChangeListener {

		void swingInstancesChanged();

		void swingInstancesChangedStats();
	}

	public boolean sendMessageToSwing(AtmosphereResource r, String clientId, MsgIn o) {
		SwingInstance client = swingInstances.get(clientId);
		if (client != null) {
			return client.sendToSwing(r, o);
		} else {
			return false;
		}
	}

	public SwingInstance findInstance(String clientId) {
		return swingInstances.get(clientId);
	}

}
