package org.webswing.server.services.startup;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.webswing.Constants;
import org.webswing.server.base.WsInitException;
import org.webswing.server.services.config.ConfigurationService;
import org.webswing.server.services.jms.JmsService;
import org.webswing.server.services.websocket.WebSocketService;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import main.Main;

@Singleton
public class StartupServiceImpl implements StartupService {
	private static final Logger log = LoggerFactory.getLogger(StartupService.class);
	private final JmsService jms;
	private final WebSocketService websocket;
	private final ConfigurationService config;

	@Inject
	public StartupServiceImpl(JmsService jms, WebSocketService websocket, ConfigurationService config) {
		this.jms = jms;
		this.websocket = websocket;
		this.config = config;
	}

	public void start() throws WsInitException {
		try {
			validateConfig();
			jms.start();
			websocket.start();
			config.start();
		} catch (WsInitException e) {
			throw e;
		} catch (Exception e) {
			throw new WsInitException("Failed to start Webswing. " + e.getMessage(), e);
		}
	}

	public void stop() {
		config.stop();
		websocket.stop();
		jms.stop();
	}

	private void validateConfig() {
		if (!Boolean.getBoolean(Constants.SERVER_EMBEDED_FLAG)) {
			// initialize temp folder (create/delete old content)
			File tempDir = Main.getTempDir();
			log.info("Webswing using Temp folder:" + tempDir.getAbsolutePath());

			// initialize root dir
			File root = Main.getRootDir();
			log.info("Webswing using Root folder:" + root.getAbsolutePath());

			// verify war file and convert to URI
			validatePropertyFilePath(Constants.WAR_FILE_LOCATION, null);

			// verify config file and convert to URI
			validatePropertyFilePath(Constants.CONFIG_FILE_PATH, Constants.DEFAULT_CONFIG_FILE_NAME);

		}
	}

	private void validatePropertyFilePath(String propertyName, String defaultValue) {
		try {
			String configFilePath = getValidURI(System.getProperty(propertyName, defaultValue));
			System.setProperty(propertyName, configFilePath);
			log.info("System property " + propertyName + " = " + configFilePath);
		} catch (FileNotFoundException e) {
			throw new IllegalStateException("Invalid system property " + propertyName + ": " + e.getMessage());
		}
	}

	private String getValidURI(String pathOrUri) throws FileNotFoundException {
		if (pathOrUri != null) {
			try {
				URI uri = URI.create(pathOrUri);
				if (new File(uri).exists()) {
					return pathOrUri;
				} else {
					throw new FileNotFoundException("File " + uri.toString() + "not found.");
				}
			} catch (IllegalArgumentException e) {
				File relativeConfigFile = new File(Main.getRootDir(), pathOrUri);
				File absoluteConfigFile = new File(pathOrUri);
				if (relativeConfigFile.exists()) {
					return relativeConfigFile.toURI().toString();
				} else if (absoluteConfigFile.exists()) {
					return absoluteConfigFile.toURI().toString();
				} else {
					throw new FileNotFoundException("File " + relativeConfigFile.getAbsolutePath() + " or " + absoluteConfigFile.getAbsolutePath() + " not found.");
				}
			}
		}
		throw new RuntimeException("Path not specified.");
	}

}
