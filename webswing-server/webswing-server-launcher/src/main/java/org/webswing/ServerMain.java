package org.webswing;

import org.eclipse.jetty.server.*;
import org.eclipse.jetty.server.handler.ErrorHandler;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.webapp.WebAppContext;
import org.webswing.toolkit.util.Logger;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.lang.management.ManagementFactory;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class ServerMain {

	static Server server;

	public static void main(String[] args) throws Exception {
		Configuration config = ConfigurationImpl.parse(args);
		System.out.println(config.toString());
		System.setProperty(Constants.SERVER_EMBEDED_FLAG, "true");
		System.setProperty(Constants.SERVER_PORT, config.getHttpPort());
		System.setProperty(Constants.SERVER_HOST, config.getHost());
		System.setProperty(Constants.SERVER_CONTEXT_PATH, config.getContextPath());
		boolean isHttpsOnly = config.isHttps() && !config.isHttp();
		System.setProperty(Constants.HTTPS_ONLY, System.getProperty(Constants.HTTPS_ONLY, ""+isHttpsOnly));
		if (config.getConfigFile() != null) {
			File configFile = new File(config.getConfigFile());
			if (configFile.exists()) {
				System.setProperty(Constants.CONFIG_FILE_PATH, configFile.toURI().toString());
			} else {
				Logger.error("Webswing configuration file " + config.getConfigFile() + " not found. Using default location.");
			}
		}

		server = new Server();

		List<Connector> connectors = new ArrayList<Connector>();
		if (config.isHttp()) {
			HttpConfiguration http_config = new HttpConfiguration();
			http_config.setSendServerVersion(false);
			if (config.isHttps()) {
				http_config.setSecurePort(Integer.parseInt(config.getHttpsPort()));
			}
			ServerConnector http = new ServerConnector(server, new HttpConnectionFactory(http_config));
			http.setPort(Integer.parseInt(config.getHttpPort()));
			http.setHost(config.getHost());
			connectors.add(http);
		}
		if (config.isHttps()) {
			if (config.isHttps() && config.getTruststore() != null && !config.getTruststore().isEmpty() && config.getKeystore() != null && config.getKeystore().isEmpty()) {
				Logger.error("SSL configuration is invalid. Please specify the location of truststore and keystore files.");
			} else {
				File keyStoreFile = config.resolveConfigFile(config.getKeystore());
				File trustStoreFile = config.resolveConfigFile(config.getTruststore());
				if (!trustStoreFile.exists()) {
					Logger.error("SSL configuration is invalid. Truststore file " + trustStoreFile.getAbsolutePath() + " does not exist.");
				} else if (!keyStoreFile.exists()) {
					Logger.error("SSL configuration is invalid. Keystore file " + keyStoreFile.getAbsolutePath() + " does not exist.");
				} else {
					SslContextFactory sslContextFactory = new SslContextFactory();
					sslContextFactory.setKeyStorePath(keyStoreFile.getAbsolutePath());
					sslContextFactory.setKeyStorePassword(config.getKeystorePassword());
					sslContextFactory.setTrustStorePath(trustStoreFile.getAbsolutePath());
					sslContextFactory.setTrustStorePassword(config.getTruststorePassword());
					sslContextFactory.setNeedClientAuth(config.isClientAuthEnabled());

					HttpConfiguration https_config = new HttpConfiguration();
					https_config.setSendServerVersion(false);

					SecureRequestCustomizer src = new SecureRequestCustomizer();
					https_config.addCustomizer(src);

					ServerConnector https = new ServerConnector(server, sslContextFactory, new HttpConnectionFactory(https_config));
					https.setPort(Integer.parseInt(config.getHttpsPort()));
					https.setHost(config.getHost());
					connectors.add(https);
				}
			}
		}

		server.setConnectors(connectors.toArray(new Connector[connectors.size()]));

		WebAppContext webapp = new WebAppContext();
		webapp.setContextPath(System.getProperty(Constants.SERVER_CONTEXT_PATH,"/"));
		webapp.setWar(System.getProperty(Constants.WAR_FILE_LOCATION));
		webapp.setTempDirectory(new File(URI.create(System.getProperty(Constants.TEMP_DIR_PATH))));
		webapp.setPersistTempDirectory(true);
		webapp.setAttribute("org.eclipse.jetty.server.webapp.WebInfIncludeJarPattern", "");
		webapp.setThrowUnavailableOnStartupException(true);
		server.setHandler(webapp);

		ErrorHandler errorHandler = new ErrorHandler(){//custom event handler to hide running jetty version (pen test)
			@Override
			protected void writeErrorPageBody(HttpServletRequest request, Writer writer, int code, String message, boolean showStacks) throws IOException {
				String uri = request.getRequestURI();
				this.writeErrorPageMessage(request, writer, code, message, uri);
			}
		};
		webapp.setErrorHandler(errorHandler);

		try {
			server.start();
			server.join();
		} catch (Exception e) {
			Logger.error("Webswing Server initialization failed. Stopping the server.",e);
			server.stop();
		}
		server=null;
	}


	public static void stopServer(){
		if(server!=null){
			try {
				server.stop();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
