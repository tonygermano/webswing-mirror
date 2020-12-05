package org.webswing.server.common.service.url;

import java.io.File;

public interface WebSocketUrlLoaderService {
	
	void init(File propertiesFile);
	
	void addListener(WebSocketUrlLoaderListener listener);
	
	void removeListener(WebSocketUrlLoaderListener listener);
	
}