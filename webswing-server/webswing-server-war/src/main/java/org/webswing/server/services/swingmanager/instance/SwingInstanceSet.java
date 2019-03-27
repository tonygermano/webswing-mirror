package org.webswing.server.services.swingmanager.instance;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.webswing.server.services.swinginstance.SwingInstance;

public class SwingInstanceSet {
	Set<SwingInstance> instances = new HashSet<SwingInstance>();

	public synchronized void add(SwingInstance swingInstance) {
		instances.add(swingInstance);
	}

	public boolean contains(SwingInstance swingInstance) {
		return instances.contains(swingInstance);
	}

	public synchronized void remove(String instanceId) {
		instances.remove(findByInstanceId(instanceId));
	}

	public int size() {
		return instances.size();
	}

	public synchronized SwingInstance findByConnectionId(String connectionId) {
		for (SwingInstance i : instances) {
			if (connectionId != null && (connectionId.equals(i.getConnectionId()) || connectionId.equals(i.getMirrorConnectionId()))) {
				return i;
			}
		}
		return null;
	}

	public synchronized SwingInstance findByInstanceId(String instanceId) {
		for (SwingInstance i : instances) {
			if (instanceId != null && instanceId.equals(i.getInstanceId())) {
				return i;
			}
		}
		return null;
	}

	public SwingInstance findByOwnerId(String ownerId) {
		for (SwingInstance i : instances) {
			if (ownerId != null && ownerId.equals(i.getOwnerId())) {
				return i;
			}
		}
		return null;
	}

	public synchronized List<SwingInstance> getAllInstances() {
		return new ArrayList<>(instances);
	}


}
