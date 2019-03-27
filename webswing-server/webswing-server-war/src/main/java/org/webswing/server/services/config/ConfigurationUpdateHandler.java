package org.webswing.server.services.config;

import org.webswing.server.common.model.SecuredPathConfig;

/**
 * Created by vikto on 26-May-17.
 */
public interface ConfigurationUpdateHandler {
	void notifyConfigChanged(String path, SecuredPathConfig newCfg);

	void notifyConfigDeleted(String path);

}
