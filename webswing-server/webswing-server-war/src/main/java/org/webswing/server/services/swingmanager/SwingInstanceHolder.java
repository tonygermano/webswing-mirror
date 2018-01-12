package org.webswing.server.services.swingmanager;

import java.util.List;

import org.webswing.server.services.swinginstance.SwingInstance;

public interface SwingInstanceHolder {

	SwingInstance findInstanceBySessionId(String uuid);

	SwingInstance findInstanceByClientId(String clientId);

	SwingInstance findInstanceByInstanceId(String instanceId);

	List<SwingInstance> getAllInstances();

	List<SwingInstance> getAllClosedInstances();
	
	List<SwingInstanceManager> getApplications();

}
