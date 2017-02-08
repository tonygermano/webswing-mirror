package org.webswing.server.services.config;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.apache.commons.lang.StringUtils;
import org.webswing.Constants;
import org.webswing.server.common.model.SecuredPathConfig;
import org.webswing.server.common.model.meta.ConfigContext;
import org.webswing.server.common.model.meta.MetaObject;
import org.webswing.server.common.util.CommonUtil;
import org.webswing.server.extension.ConfigurationProvider;
import org.webswing.server.extension.DefaultConfigurationProvider;
import org.webswing.server.extension.ExtensionClassLoader;
import org.webswing.server.model.exception.WsException;
import org.webswing.server.model.exception.WsInitException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Singleton
public class ConfigurationServiceImpl implements ConfigurationService {

	private final ExtensionClassLoader extensionLoader;
	private ConfigurationProvider provider;
	private List<ConfigurationChangeListener> changeListeners = new ArrayList<>();

	@Inject
	public ConfigurationServiceImpl(ExtensionClassLoader extensionLoader) {
		this.extensionLoader = extensionLoader;
	}

	@Override
	public void start() throws WsInitException {
		String providerClassName = System.getProperty(Constants.CONFIG_PROVIDER, DefaultConfigurationProvider.class.getName());
		try {
			Class<?> providerClass = extensionLoader.loadClass(providerClassName);
			provider = (ConfigurationProvider) providerClass.newInstance();
		} catch (Exception e) {
			throw new WsInitException("Could not instantiate configuration provider " + providerClassName, e);
		}
	}

	@Override
	public void stop() {
		synchronized (changeListeners) {
			changeListeners.clear();
		}
	}

	@Override
	public SecuredPathConfig getConfiguration(String path) {
		path = asPath(path);
		Map<String, Object> configuration = provider.getConfiguration(path);
		return provider.toSecuredPathConfig(path, configuration);
	}

	public List<String> getPaths() {
		return provider.getPaths();
	}

	@Override
	public void setConfiguration(String path, Map<String, Object> configuration) throws Exception {
		path = asPath(path);
		if (configuration == null) {
			configuration = provider.createDefaultConfiguration(path);
		}
		provider.validateConfiguration(path, configuration);
		Map<String, Object> old = provider.getConfiguration(path);
		SecuredPathConfig oldConfig = provider.toSecuredPathConfig(path, old);
		SecuredPathConfig newConfig = provider.toSecuredPathConfig(path, configuration);
		provider.saveConfiguration(path, configuration);
		notifyChange(path, oldConfig, newConfig);
	}

	@Override
	public void setSwingConfiguration(String path, Map<String, Object> configuration) throws Exception {
		path = asPath(path);
		provider.validateConfiguration(path, configuration);

		Map<String, Object> old = provider.getConfiguration(path);
		SecuredPathConfig oldConfig = provider.toSecuredPathConfig(path, old);
		if (oldConfig != null) {
			provider.saveSwingConfiguration(path, configuration);
			SecuredPathConfig newConfig = provider.toSecuredPathConfig(path, provider.getConfiguration(path));
			notifyChange(path, oldConfig, newConfig);
		} else {
			throw new WsException("No Application found for path '" + path + "'");
		}
	}

	@Override
	public void removeConfiguration(String path) throws Exception {
		provider.removeConfiguration(asPath(path));
	}

	public void registerChangeListener(ConfigurationChangeListener listener) {
		synchronized (changeListeners) {
			changeListeners.add(listener);
		}
	}

	public void removeChangeListener(ConfigurationChangeListener listener) {
		synchronized (changeListeners) {
			changeListeners.remove(listener);
		}
	}

	private void notifyChange(String path, SecuredPathConfig oldCfg, SecuredPathConfig newCfg) {
		synchronized (changeListeners) {
			for (ConfigurationChangeListener listener : changeListeners) {
				if (listener != null) {
					listener.notifyChange(new ConfigurationChangeEvent(asPath(path), oldCfg, newCfg));
				}
			}
		}
	}

	@Override
	public MetaObject describeConfiguration(String path, Map<String, Object> json, ConfigContext ctx) throws WsException {
		return provider.describeConfiguration(asPath(path), json, ctx, extensionLoader);
	}

	private String asPath(String path) {
		String p = CommonUtil.toPath(path);
		if (StringUtils.isBlank(p)) {
			p = "/";
		}
		return p;
	}

	@Override
	public boolean isMultiApplicationMode() {
		return provider.isMultiApplicationMode();
	}
}
