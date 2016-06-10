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
import org.webswing.model.server.SwingDescriptor.SessionMode;
import org.webswing.model.server.admin.Sessions;
import org.webswing.model.server.admin.SwingSession;
import org.webswing.server.util.ServerUtil;

public class SwingInstanceManager {

	private static SwingInstanceManager instance = new SwingInstanceManager();
	private static final Logger log = LoggerFactory.getLogger(SwingInstanceManager.class);

	private List<SwingSession> closedInstances = new ArrayList<SwingSession>();
	private Map<String, SwingInstance> swingInstances = new ConcurrentHashMap<String, SwingInstance>();
	private SwingInstanceChangeListener changeListener;

	private SwingInstanceManager() {
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

	public void connectSwingInstance(AtmosphereResource r, ConnectionHandshakeMsgIn h) {
		SwingInstance swingInstance = findInstance(r, h);
		if (swingInstance == null) {// start new swing app
			SwingDescriptor app;
			if (h.isApplet()) {
				app = ConfigurationManager.getInstance().getApplet(h.getApplicationName());
			} else {
				app = ConfigurationManager.getInstance().getApplication(h.getApplicationName());
			}
			if (app == null) {
				throw new RuntimeException((h.isApplet() ? "Applet " : "Application ") + h.getApplicationName() + " is not configured.");
			}
			if (ServerUtil.isUserAuthorized(r, app, h)) {
				if (!h.isMirrored()) {
					if (!reachedMaxConnections(app)) {
						swingInstance = new SwingInstance(resolveInstanceIdForMode(r, h, app),h, app, r);
						synchronized (this) {
							swingInstances.put(swingInstance.getInstanceId(), swingInstance);
						}
						notifySwingInstanceChanged();
					} else {
						ServerUtil.broadcastMessage(r, SimpleEventMsgOut.tooManyClientsNotification.buildMsgOut());
					}
				} else {
					ServerUtil.broadcastMessage(r, SimpleEventMsgOut.configurationError.buildMsgOut());
				}
			} else {
				log.error("Authorization error: User " + ServerUtil.getUserName(r) + " is not authorized to connect to application " + app.getName() + (h.isMirrored() ? " [Mirrored view only available for admin role]" : ""));
			}
		} else {
			if (h.isMirrored()) {// connect as mirror viewer
				if (ServerUtil.isUserAuthorized(r, swingInstance.getApplication(), h)) {
					notifySessionDisconnected(r.uuid());// disconnect possible running mirror sessions
					swingInstance.connectMirroredWebSession(r);
				} else {
					log.error("Authorization error: User " + ServerUtil.getUserName(r) + " is not authorized. [Mirrored view only available for admin role]");
				}
			} else {// continue old session?
				if (h.getSessionId() != null && h.getSessionId().equals(swingInstance.getSessionId())) {
					swingInstance.sendToSwing(r, h);
				} else {
					boolean result = swingInstance.connectPrimaryWebSession(r);
					if (result) {
						ServerUtil.broadcastMessage(r, SimpleEventMsgOut.continueOldSession.buildMsgOut());
						notifySwingInstanceChanged();
					} else {
						ServerUtil.broadcastMessage(r, SimpleEventMsgOut.applicationAlreadyRunning.buildMsgOut());
					}
				}
			}
		}
	}

	private SwingInstance findInstance(AtmosphereResource r, ConnectionHandshakeMsgIn h) {
		synchronized (this) {
			for (String instanceId : swingInstances.keySet()) {
				SwingInstance si = swingInstances.get(instanceId);
				String idForMode = resolveInstanceIdForMode(r, h, si.getApplication());
				if(idForMode.equals(instanceId)){
					return si;
				}
			}
		}
		return null;
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

	public void notifySwingClose(SwingInstance swingInstance) {
		synchronized (this) {
			closedInstances.add(ServerUtil.composeSwingInstanceStatus(swingInstance));
			swingInstances.remove(swingInstance.getInstanceId());
		}
		notifySwingInstanceChanged();
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
				i.disconnectPrimaryWebSession();
			} else if (i.getMirroredSessionId() != null && i.getMirroredSessionId().equals(uuid)) {
				i.disconnectMirroredWebSession();
			}
		}
	}

	public void notifySwingInstanceChanged() {
		if (changeListener != null) {
			changeListener.swingInstancesChanged();
		}
	}

	public void notifySwingInstanceStatsChanged() {
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
			if(client==null){
				for (SwingInstance si : getSwingInstanceSet()) {
					if(si.getClientId().equals(clientId)){
						client=si;
						break;
					}
				}
			}
			if (client != null) {
				return client.sendToSwing(r, o);
			}
		}
		return false;
	}

	public SwingInstance findInstance(String instanceId) {
		if (instanceId != null) {
			return swingInstances.get(instanceId);
		}
		return null;
	}

	public String resolveInstanceID(AtmosphereResource r, ConnectionHandshakeMsgIn h) {
		SwingDescriptor app;
		if (h.isApplet()) {
			app = ConfigurationManager.getInstance().getApplet(h.getApplicationName());
		} else {
			app = ConfigurationManager.getInstance().getApplication(h.getApplicationName());
		}
		if (app == null) {
			throw new RuntimeException((h.isApplet() ? "Applet " : "Application ") + h.getApplicationName() + " is not configured.");
		}
		return resolveInstanceIdForMode(r, h, app);
	}

	private String resolveInstanceIdForMode(AtmosphereResource r, ConnectionHandshakeMsgIn h, SwingDescriptor app) {
		switch ( app.getSessionMode()) {
		case ALWAYS_NEW_SESSION:
			return h.getClientId() + r.uuid();
		case CONTINUE_FOR_BROWSER:
			return h.getClientId();
		case CONTINUE_FOR_USER:
			return app.getName()+ServerUtil.getUserName(r);
		default:
			return h.getClientId();
		}
	}

}
