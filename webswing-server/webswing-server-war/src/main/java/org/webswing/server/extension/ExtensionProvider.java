package org.webswing.server.extension;

import org.apache.shiro.cache.CacheManager;
import org.webswing.server.base.PrimaryUrlHandler;
import org.webswing.server.base.UrlHandler;

import java.util.List;

public interface ExtensionProvider {
	List<UrlHandler> createExtensionHandlers(PrimaryUrlHandler parent);

	CacheManager getCacheManager();
}
