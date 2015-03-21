package org.webswing.server.handler;

import java.io.File;

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

				// verify config file and convert to URI
				if (System.getProperty(Constants.CONFIG_FILE_PATH) != null) {
					File configFile = new File(System.getProperty(Constants.CONFIG_FILE_PATH));
					if (configFile.exists()) {
						System.setProperty(Constants.CONFIG_FILE_PATH, configFile.toURI().toString());
					} else {
						throw new IllegalStateException("Webswing configuration file " + configFile.getAbsolutePath() + " not found.");
					}
				} else {
					throw new IllegalStateException("Webswing configuration file path system property '" + Constants.CONFIG_FILE_PATH + "' not specified.");
				}

				// verify users file and convert to URI
				if (System.getProperty(Constants.USER_FILE_PATH) != null) {
					File usersFile = new File(System.getProperty(Constants.USER_FILE_PATH));
					if (usersFile.exists()) {
						System.setProperty(Constants.USER_FILE_PATH, usersFile.toURI().toString());
					} else {
						throw new IllegalStateException("Webswing users property file " + usersFile.getAbsolutePath() + " not found.");
					}
				} else {
					throw new IllegalStateException("Webswing users file path system property '" + Constants.USER_FILE_PATH + "' not specified.");
				}

				// verify war file and convert to URI
				if (System.getProperty(Constants.WAR_FILE_LOCATION) != null) {
					File warFile = new File(System.getProperty(Constants.WAR_FILE_LOCATION));
					if (warFile.exists()) {
						System.setProperty(Constants.WAR_FILE_LOCATION, warFile.toURI().toString());
					} else {
						throw new IllegalStateException("Webswing war file " + warFile.getAbsolutePath() + " not found.");
					}
				} else {
					throw new IllegalStateException("Webswing war file path system property '" + Constants.WAR_FILE_LOCATION + "' not specified.");
				}
			}
		} catch (Exception e) {
			log.error("StartupService failed to start due to following error", e);
		}
	}

	public void contextDestroyed(ServletContextEvent sce) {

	}

}
