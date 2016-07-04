package org.webswing.server.services.swingmanager;

import org.webswing.model.server.SwingDescriptor;
import org.webswing.server.base.UrlHandler;

public interface SwingInstanceManagerService {

	SwingInstanceManager createApp(UrlHandler parent, SwingDescriptor config); 
}
