package org.webswing.server.services.recorder;

import org.webswing.server.services.swinginstance.SwingInstance;
import org.webswing.server.services.swingmanager.SwingInstanceManager;

public class SessionRecorderServiceImpl implements SessionRecorderService {

	public SessionRecorderImpl create(SwingInstance i, SwingInstanceManager manager){
		return new SessionRecorderImpl(i,manager);
	}
}
