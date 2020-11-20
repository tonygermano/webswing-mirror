package org.webswing.server.common.service.url;

import java.util.Set;

public interface WebSocketUrlLoader {

	void initialize();
	
	Set<String> reload();
	
}
