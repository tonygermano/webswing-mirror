package org.webswing.server.services.swingmanager;

import java.util.List;

import org.webswing.model.c2s.ConnectionHandshakeMsgIn;
import org.webswing.server.services.swinginstance.SwingInstance;
import org.webswing.server.services.websocket.WebSocketConnection;

public class SwingInstanceHolderImpl implements SwingInstanceHolderProvider{

	private SwingInstanceSet runningInstances = new SwingInstanceSet();
	private SwingInstanceSet closedInstances = new SwingInstanceSet();
	
	@Override
	public SwingInstance findInstanceBySessionId(String uuid) {
		return runningInstances.findBySessionId(uuid);
	}

	@Override
	public SwingInstance findInstanceByInstanceId(String instanceId) {
		return runningInstances.findByInstanceId(instanceId);
	}

	@Override
	public SwingInstance findInstanceByClientId(String clientId) {
		return runningInstances.findByClientId(clientId);
	}

	@Override
	public List<SwingInstance> getAllInstances() {
		return runningInstances.getAllInstances();
	}

	@Override
	public List<SwingInstance> getAllClosedInstances() {
		return closedInstances.getAllInstances();
	}

	@Override
	public List<SwingInstanceManager> getApplications() {
		throw new UnsupportedOperationException();
	}

	@Override
	public SwingInstance findInstanceByInstanceId(ConnectionHandshakeMsgIn handshake, WebSocketConnection r) {
		return runningInstances.findByInstanceId(handshake, r);
	}

	@Override
	public void add(SwingInstance swingInstance) {
		runningInstances.add(swingInstance);		
	}

	@Override
	public int getRunningInstancesCount() {
		return runningInstances.size();
	}

	@Override
	public void remove(SwingInstance swingInstance) {
		if (!closedInstances.contains(swingInstance)) {
			closedInstances.add(swingInstance);
		}
		runningInstances.remove(swingInstance.getInstanceId());		
	}

}
