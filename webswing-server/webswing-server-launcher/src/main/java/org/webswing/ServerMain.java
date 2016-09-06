package org.webswing;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jetty.jmx.MBeanContainer;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.server.ssl.SslSelectChannelConnector;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.webapp.WebAppContext;
import org.webswing.toolkit.util.Logger;

public class ServerMain {

	public static void main(String[] args) throws Exception {
		Configuration config = ConfigurationImpl.parse(args);
		System.out.println(config.toString());
		System.setProperty(Constants.SERVER_EMBEDED_FLAG, "true");
		System.setProperty(Constants.SERVER_PORT, config.getHttpPort());
		System.setProperty(Constants.SERVER_HOST, config.getHost());
		if (config.getConfigFile() != null) {
			File configFile = new File(config.getConfigFile());
			if (configFile.exists()) {
				System.setProperty(Constants.CONFIG_FILE_PATH, configFile.toURI().toString());
			} else {
				Logger.error("Webswing configuration file " + config.getConfigFile() + " not found. Using default location.");
			}
		}

		Server server = new Server();
		List<Connector> connectors = new ArrayList<Connector>();
		if (config.isHttp()) {
			Connector conHttp = new SelectChannelConnector();
			conHttp.setPort(Integer.parseInt(config.getHttpPort()));
			conHttp.setHost(config.getHost());
			connectors.add(conHttp);
		}
		if (config.isHttps()) {
			if (config.isHttps() && config.getTruststore() != null && !config.getTruststore().isEmpty() && config.getKeystore() != null && config.getKeystore().isEmpty()) {
				Logger.error("SSL configuration is invalid. Please specify the location of truststore and keystore files.");
			} else {
				if (!new File(config.getTruststore()).exists()) {
					Logger.error("SSL configuration is invalid. Truststore file " + new File(config.getTruststore()).getAbsolutePath() + " does not exist.");
				} else if (!new File(config.getKeystore()).exists()) {
					Logger.error("SSL configuration is invalid. Keystore file " + new File(config.getKeystore()).getAbsolutePath() + " does not exist.");
				} else {
					SslContextFactory sslContextFactory = new SslContextFactory(config.getKeystore());
					sslContextFactory.setKeyStorePassword(config.getKeystorePassword());
					sslContextFactory.setTrustStore(config.getTruststore());
					sslContextFactory.setTrustStorePassword(config.getTruststorePassword());
					sslContextFactory.setNeedClientAuth(false);
					sslContextFactory.addExcludeProtocols("SSLv3", "SSLv2Hello");
					SslSelectChannelConnector conSSL = new SslSelectChannelConnector(sslContextFactory);
					conSSL.setPort(Integer.parseInt(config.getHttpsPort()));
					conSSL.setHost(config.getHost());
					connectors.add(conSSL);
				}
			}
		}

		server.setConnectors(connectors.toArray(new Connector[connectors.size()]));

		// enable jmx
		MBeanContainer mbcontainer = new MBeanContainer(ManagementFactory.getPlatformMBeanServer());
		server.getContainer().addEventListener(mbcontainer);
		server.addBean(mbcontainer);

		// mbcontainer.addBean(Log.getLog());

		WebAppContext webapp = new WebAppContext();
		webapp.setContextPath("/");
		webapp.setWar(System.getProperty(Constants.WAR_FILE_LOCATION));
		webapp.setTempDirectory(new File(URI.create(System.getProperty(Constants.TEMP_DIR_PATH))));
		webapp.setAttribute("org.eclipse.jetty.server.webapp.WebInfIncludeJarPattern", "");
		server.setHandler(webapp);
		server.start();
		server.join();
	}
}
