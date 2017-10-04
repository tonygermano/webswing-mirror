package org.webswing.server.extension;

import org.webswing.Constants;
import org.webswing.server.base.PrimaryUrlHandler;
import org.webswing.server.base.UrlHandler;
import org.webswing.server.model.exception.WsInitException;
import org.webswing.server.services.config.ConfigurationProvider;
import org.webswing.server.services.config.ConfigurationUpdateHandler;
import org.webswing.server.services.config.DefaultConfigurationProvider;
import org.webswing.server.services.swingprocess.SwingProcessService;
import sun.misc.ExtensionDependency;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

@Singleton
public class ExtensionServiceImpl implements ExtensionService, ExtensionDependencies {

	private final ExtensionClassLoader extensionLoader;
	private final SwingProcessService processService;
	private ExtensionProvider provider;

	@Inject
	public ExtensionServiceImpl(ExtensionClassLoader extensionLoader, SwingProcessService processService) {
		this.extensionLoader = extensionLoader;
		this.processService = processService;
	}

	@Override
	public void start() throws WsInitException {
		String providerClassName = System.getProperty(Constants.EXTENSION_PROVIDER, DefaultExtensionProvider.class.getName());
		try {
			Class<?> providerClass = extensionLoader.loadClass(providerClassName);
			try {
				Constructor<?> constructor = providerClass.getDeclaredConstructor(ExtensionDependencies.class);
				provider = (ExtensionProvider) constructor.newInstance(this);
			} catch (NoSuchMethodException e) {
				provider = (ExtensionProvider) providerClass.newInstance();
			}
		} catch (Exception e) {
			throw new WsInitException("Could not instantiate extension provider " + providerClassName, e);
		}
	}

	@Override
	public void stop() {
	}

	@Override
	public List<UrlHandler> createExtHandlers(PrimaryUrlHandler parent) {
		return provider.createDefaultConfiguration(parent);
	}

	@Override
	public SwingProcessService getProcessService() {
		return processService;
	}
}
