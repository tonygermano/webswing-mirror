package org.webswing.server.extension;

import org.webswing.server.GlobalUrlHandler;
import org.webswing.server.base.PrimaryUrlHandler;
import org.webswing.server.base.UrlHandler;
import org.webswing.server.base.WebswingService;

import java.util.List;

public interface ExtensionService extends WebswingService{

	List<UrlHandler> createExtHandlers(PrimaryUrlHandler parent);
}
