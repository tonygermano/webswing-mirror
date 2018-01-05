package org.webswing.server.extension;

import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.cache.MemoryConstrainedCacheManager;
import org.webswing.server.base.PrimaryUrlHandler;
import org.webswing.server.base.UrlHandler;

import java.util.ArrayList;
import java.util.List;

public class DefaultExtensionProvider implements ExtensionProvider {

	private final ExtensionDependencies dependencies;

	public DefaultExtensionProvider(ExtensionDependencies dependencies){
		this.dependencies = dependencies;
	}

	public ExtensionDependencies getDependencies() {
		return dependencies;
	}

	@Override
	public List<UrlHandler> createExtensionHandlers(PrimaryUrlHandler parent) {
		return new ArrayList<>();
	}

}
