package org.webswing.server.extension;

import org.apache.shiro.cache.CacheManager;

public interface EnterpriseExtensionService extends ExtensionService {

	CacheManager getSecurityCacheManager();
}
