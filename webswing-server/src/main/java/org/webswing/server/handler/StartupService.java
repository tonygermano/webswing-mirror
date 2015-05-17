package org.webswing.server.handler;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URI;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import main.Main;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.webswing.Constants;

public class StartupService implements ServletContextListener {
	private static final Logger log = LoggerFactory.getLogger(StartupService.class);

	public void contextInitialized(ServletContextEvent event) {
		try {
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

				// verify users file and convert to URI
				validatePropertyFilePath(Constants.USER_FILE_PATH, Constants.DEFAULT_USER_FILE_NAME);

			}
		} catch (Exception e) {
			log.error("StartupService failed to start due to following error", e);
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

	public void contextDestroyed(ServletContextEvent sce) {

	}

}
