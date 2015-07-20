package org.webswing.server;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.atmosphere.cpr.AtmosphereResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.webswing.model.MsgIn;
import org.webswing.model.c2s.ConnectionHandshakeMsgIn;
import org.webswing.model.s2c.SimpleEventMsgOut;
import org.webswing.model.server.SwingDescriptor;
import org.webswing.model.server.admin.Sessions;
import org.webswing.model.server.admin.SwingSession;
import org.webswing.server.stats.PerformanceStatsMonitor;
import org.webswing.server.util.ServerUtil;

public class SwingInstanceManager {

	private static SwingInstanceManager instance = new SwingInstanceManager();
	private static final Logger log = LoggerFactory.getLogger(SwingInstanceManager.class);

	private List<SwingSession> closedInstances = new ArrayList<SwingSession>();
	private Map<String, SwingInstance> swingInstances = new ConcurrentHashMap<String, SwingInstance>();
	private SwingInstanceChangeListener changeListener;

	private SwingInstanceManager() {
		new PerformanceStatsMonitor();
	}

	public static SwingInstanceManager getInstance() {
		return instance;
	}

	public synchronized Set<SwingInstance> getSwingInstanceSet() {
		Set<SwingInstance> set;
		synchronized (this) {
			set = new HashSet<SwingInstance>(swingInstances.values());
		}
		return set;
	}

	public void connectSwingInstance(AtmosphereResource resource, ConnectionHandshakeMsgIn h) {
		SwingInstance swingInstance = swingInstances.get(h.getClientId());
		if (swingInstance == null) {// start new swing app
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
				log.error("Authorization error: User " + ServerUtil.getUserName(resource) + " is not authorized to connect to application " + app.getName() + (h.isMirrored() ? " [Mirrored view only available for admin role]" : ""));
			}
		} else {
			if (h.isMirrored()) {// connect as mirror viewer
				if (ServerUtil.isUserAuthorized(resource, swingInstance.getApplication(), h)) {
					notifySessionDisconnected(resource.uuid());// disconnect possible running mirror sessions
					boolean result = swingInstance.registerMirroredWebSession(resource);
					if (!result) {
						ServerUtil.broadcastMessage(resource, SimpleEventMsgOut.applicationAlreadyRunning.buildMsgOut());
					}
				} else {
					log.error("Authorization error: User " + ServerUtil.getUserName(resource) + " is not authorized. [Mirrored view only available for admin role]");
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
	}

	private boolean reachedMaxConnections(SwingDescriptor app) {
		if (app.getMaxClients() < 0) {
			return false;
		} else if (app.getMaxClients() == 0) {
			return true;
		} else {
			int count = 0;
			for (SwingInstance si : getSwingInstanceSet()) {
				if (app.getName().equals(si.getApplication().getName()) && si.isRunning()) {
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
		synchronized (this) {
			swingInstances.remove(swingInstance.getClientId());
		}
		notifySwingChangeChange();
	}

	public synchronized Sessions getSessions() {
		Sessions result = new Sessions();
		for (SwingInstance si : getSwingInstanceSet()) {
			result.getSessions().add(ServerUtil.composeSwingInstanceStatus(si));
		}
		result.setClosedSessions(closedInstances);
		return result;
	}

	public synchronized SwingSession getSession(String id) {
		for (SwingInstance si : getSwingInstanceSet()) {
			if (si.getClientId().equals(id)) {
				return ServerUtil.composeSwingInstanceStatus(si);
			}
		}
		return null;
	}

	public void killSession(String id) {
		for (SwingInstance si : getSwingInstanceSet()) {
			if (si.getClientId().equals(id)) {
				si.kill();
			}
		}
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
		if (clientId != null) {
			SwingInstance client = swingInstances.get(clientId);
			if (client != null) {
				return client.sendToSwing(r, o);
			}
		}
		return false;
	}

	public SwingInstance findInstance(String clientId) {
		if (clientId != null) {
			return swingInstances.get(clientId);
		}
		return null;
	}

}
