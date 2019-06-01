package org.webswing.server.services.swingprocess;

import org.webswing.server.services.startup.StartupService;

public interface SwingProcessService extends StartupService {

	SwingProcess create(SwingProcessConfig config);
}
