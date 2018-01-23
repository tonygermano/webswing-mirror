package org.webswing.server.services.swingmanager.instance;

import java.util.List;

import org.webswing.server.services.swinginstance.SwingInstance;

public interface SwingInstanceHolder {

	SwingInstance findInstanceByConnectionId(String uuid);

	SwingInstance findInstanceByOwnerId(String ownerId);

	SwingInstance findInstanceByInstanceId(String instanceId);

	List<SwingInstance> getAllInstances();

	List<SwingInstance> getAllClosedInstances();

	void add(SwingInstance swingInstance);

	void remove(SwingInstance swingInstance);
	
	int getRunningInstacesCount();
}
