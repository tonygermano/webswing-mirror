package org.webswing.server.services.swingmanager.instance;

import java.util.List;

import org.webswing.server.services.swinginstance.SwingInstance;

public class SwingInstanceHolderImpl implements SwingInstanceHolder{

	private SwingInstanceSet runningInstances = new SwingInstanceSet();
	private SwingInstanceSet closedInstances = new SwingInstanceSet();
	
	@Override
	public SwingInstance findInstanceByConnectionId(String uuid) {
		return runningInstances.findByConnectionId(uuid);
	}

	@Override
	public SwingInstance findInstanceByOwnerId(String ownerId) {
		return runningInstances.findByOwnerId(ownerId);
	}

	@Override
	public SwingInstance findInstanceByInstanceId(String instanceId) {
		return runningInstances.findByInstanceId(instanceId);
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
	public void add(SwingInstance swingInstance) {
		runningInstances.add(swingInstance);		
	}

	@Override
	public void remove(SwingInstance swingInstance) {
		if (!closedInstances.contains(swingInstance)) {
			closedInstances.add(swingInstance);
		}
		runningInstances.remove(swingInstance.getInstanceId());		
	}

	@Override
	public int getRunningInstacesCount() {
		return runningInstances.size();
	}

}
