package org.webswing.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URI;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import javax.jms.IllegalStateException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.webswing.Constants;
import org.webswing.server.api.GlobalUrlHandler;
import org.webswing.server.api.base.WebswingService;
import org.webswing.server.api.services.application.AppPathHandlerFactory;
import org.webswing.server.api.services.application.impl.AppPathHandlerFactoryImpl;
import org.webswing.server.api.services.config.impl.DefaultConfigurationServiceImpl;
import org.webswing.server.api.services.files.FileTransferHandlerFactory;
import org.webswing.server.api.services.files.impl.FileTransferHandlerFactoryImpl;
import org.webswing.server.api.services.resources.ResourceHandlerFactory;
import org.webswing.server.api.services.resources.impl.DefaultResourceHandlerFactoryImpl;
import org.webswing.server.api.services.rest.RestHandlerFactory;
import org.webswing.server.api.services.rest.impl.RestHandlerFactoryImpl;
import org.webswing.server.api.services.security.login.LoginHandlerFactory;
import org.webswing.server.api.services.security.login.impl.LoginHandlerFactoryImpl;
import org.webswing.server.api.services.security.modules.SecurityModuleFactory;
import org.webswing.server.api.services.security.modules.impl.DefaultSecurityModuleFactoryImpl;
import org.webswing.server.api.services.sessionpool.SessionPoolHolderService;
import org.webswing.server.api.services.sessionpool.impl.SessionPoolHolderServiceImpl;
import org.webswing.server.api.services.startup.StartupService;
import org.webswing.server.api.services.startup.impl.DefaultStartupServiceImpl;
import org.webswing.server.api.services.stat.StatisticsLoggerService;
import org.webswing.server.api.services.stat.impl.DefaultStatisticsLoggerServiceImpl;
import org.webswing.server.api.services.swinginstance.SwingInstanceFactory;
import org.webswing.server.api.services.swinginstance.holder.SwingInstanceHolderFactory;
import org.webswing.server.api.services.swinginstance.holder.impl.DefaultSwingInstanceHolderFactoryImpl;
import org.webswing.server.api.services.swinginstance.impl.SwingInstanceFactoryImpl;
import org.webswing.server.api.services.websocket.WebSocketService;
import org.webswing.server.api.services.websocket.impl.DefaultWebSocketServiceImpl;
import org.webswing.server.api.services.websocket.util.AdminConsoleWebSocketConfigurator;
import org.webswing.server.api.services.websocket.util.ApplicationWebSocketConfigurator;
import org.webswing.server.api.services.websocket.util.BrowserWebSocketConfigurator;
import org.webswing.server.common.extension.ExtensionClassLoader;
import org.webswing.server.common.model.SecuredPathConfig;
import org.webswing.server.common.service.config.ConfigurationService;
import org.webswing.server.common.service.security.SecurityManagerService;
import org.webswing.server.common.service.security.impl.SecurityManagerServiceImpl;
import org.webswing.server.common.service.startup.Initializer;
import org.webswing.server.common.service.startup.impl.DefaultInitializer;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.Multibinder;

public class WebswingServerModule extends AbstractModule {
	private static final Logger log = LoggerFactory.getLogger(WebswingServerModule.class);

	private final Class<? extends StartupService> startupServiceClass = DefaultStartupServiceImpl.class;
	
	@Override
	protected void configure() {
		initializeDefaultSystemProperties();

		bind(Initializer.class).to(DefaultInitializer.class).asEagerSingleton();
		bind(ExtensionClassLoader.class).asEagerSingleton();
		bind(GlobalUrlHandler.class);

		Multibinder<WebswingService> serviceBinder = Multibinder.newSetBinder(binder(), WebswingService.class);

		bind(StartupService.class).to(startupServiceClass);
		serviceBinder.addBinding().to(DefaultWebSocketServiceImpl.class);
		serviceBinder.addBinding().to(DefaultConfigurationServiceImpl.class);
		serviceBinder.addBinding().to(SessionPoolHolderServiceImpl.class);

		bind(WebSocketService.class).to(DefaultWebSocketServiceImpl.class);
		bind(new TypeLiteral<ConfigurationService<SecuredPathConfig>>() {}).to(DefaultConfigurationServiceImpl.class);
		bind(FileTransferHandlerFactory.class).to(FileTransferHandlerFactoryImpl.class);
		bind(ResourceHandlerFactory.class).to(DefaultResourceHandlerFactoryImpl.class);
		bind(AppPathHandlerFactory.class).to(AppPathHandlerFactoryImpl.class);
		bind(SessionPoolHolderService.class).to(SessionPoolHolderServiceImpl.class);
		bind(SwingInstanceHolderFactory.class).to(DefaultSwingInstanceHolderFactoryImpl.class);
		bind(SwingInstanceFactory.class).to(SwingInstanceFactoryImpl.class);
		bind(StatisticsLoggerService.class).to(DefaultStatisticsLoggerServiceImpl.class);
		bind(RestHandlerFactory.class).to(RestHandlerFactoryImpl.class);

		bind(SecurityManagerService.class).to(SecurityManagerServiceImpl.class);
		bind(SecurityModuleFactory.class).to(DefaultSecurityModuleFactoryImpl.class);
		bind(LoginHandlerFactory.class).to(LoginHandlerFactoryImpl.class);
		
		requestStaticInjection(BrowserWebSocketConfigurator.class);
		requestStaticInjection(ApplicationWebSocketConfigurator.class);
		requestStaticInjection(AdminConsoleWebSocketConfigurator.class);
	}

	private void initializeDefaultSystemProperties() {
		try {
			String propFilePath = System.getProperty(Constants.PROPERTIES_FILE_PATH);
			File propFile = null;
			if (propFilePath != null) {
				propFile = new File(URI.create(propFilePath));
			} else {
				propFile = new File(new URI(DefaultInitializer.getValidURI(Constants.DEFAULT_PROPERTIES_FILE_NAME)));
			}
			
			Properties p = new Properties(System.getProperties());
			try (InputStream propFileStream = new FileInputStream(propFile)) {
				p.load(propFileStream);
			}
			
			// set the system properties
			for (Map.Entry<Object, Object> prop : p.entrySet()) {
				if (!System.getProperties().containsKey(prop.getKey())) {
					System.getProperties().put(prop.getKey(), prop.getValue());
				}
			}
			
			System.setProperty(Constants.WEBSWING_SERVER_ID, UUID.randomUUID().toString());
			
			log.info("Starting webswing server with id [" + System.getProperty(Constants.WEBSWING_SERVER_ID) + "]...");
			
			if (StringUtils.isBlank(System.getProperty(Constants.WEBSWING_CONNECTION_SECRET))) {
				log.error("Missing " + Constants.WEBSWING_CONNECTION_SECRET + " system property!");
				System.exit(-1);
			}
			
			if (StringUtils.equalsIgnoreCase(System.getProperty(Constants.WEBSWING_CONNECTION_SECRET), Constants.WEBSWING_CONNECTION_SECRET_DEFAULT)) {
				String msg = "Please change " + Constants.WEBSWING_CONNECTION_SECRET + " system property to a non-default value in production!";
				log.error(msg, new IllegalStateException(msg));
			}
		} catch (Exception e) {
			log.error("Exception occurred during initialization of System Properties", e);
		}
		
		if (System.getProperty(Constants.SERVER_WEBSOCKET_URL) == null) {
			throw new RuntimeException("Failed to initialized server! Missing " + Constants.SERVER_WEBSOCKET_URL + " property!");
		}
	}

}
