package org.webswing.server.services.recorder;

import org.webswing.server.services.swinginstance.SwingInstance;
import org.webswing.server.services.swingmanager.SwingInstanceManager;

public interface SessionRecorderService {
	SessionRecorderImpl create(SwingInstance i, SwingInstanceManager manager);

}
