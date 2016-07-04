package org.webswing.server.services.swingmanager;

import java.util.Set;

import org.eclipse.jetty.util.ConcurrentHashSet;
import org.webswing.model.c2s.ConnectionHandshakeMsgIn;
import org.webswing.server.services.swinginstance.SwingInstance;
import org.webswing.server.services.websocket.WebSocketConnection;
import org.webswing.server.util.ServerUtil;

public class SwingInstanceSet {
	Set<SwingInstance> instances = new ConcurrentHashSet<SwingInstance>();

	public void add(SwingInstance swingInstance) {
		instances.add(swingInstance);
	}

	public boolean contains(SwingInstance swingInstance) {
		return instances.contains(swingInstance);
	}

	public void remove(String instanceId) {
		instances.remove(findByInstanceId(instanceId));
	}

	public int size() {
		return instances.size();
	}

	public SwingInstance findBySessionId(String sessionId) {
		for (SwingInstance i : instances) {
			if (sessionId != null && (sessionId.equals(i.getSessionId()) || sessionId.equals(i.getMirrorSessionId()))) {
				return i;
			}
		}
		return null;
	}

	public SwingInstance findByClientId(String clientId) {
		for (SwingInstance i : instances) {
			if (clientId != null && clientId.equals(i.getClientId())) {
				return i;
			}
		}
		return null;
	}

	public SwingInstance findByInstanceId(ConnectionHandshakeMsgIn h, WebSocketConnection r) {
		for (SwingInstance i : instances) {
			String idForMode = ServerUtil.resolveInstanceIdForMode(r, h, i.getAppConfig());
			if (idForMode != null && idForMode.equals(i.getInstanceId())) {
				return i;
			}
		}
		return null;
	}

	public SwingInstance findByInstanceId(String instanceId) {
		for (SwingInstance i : instances) {
			if (instanceId != null && instanceId.equals(i.getInstanceId())) {
				return i;
			}
		}
		return null;
	}

}
