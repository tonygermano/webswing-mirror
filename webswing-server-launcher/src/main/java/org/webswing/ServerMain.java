package org.webswing;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;

public class ServerMain {

    public static void main(String[] args) throws Exception {
        Configuration config = ConfigurationImpl.parse(args);
        System.setProperty(Constants.SERVER_PORT, config.getPort());
        System.setProperty(Constants.SERVER_HOST, config.getHost());

        Server server = new Server(Integer.parseInt(config.getPort()));
        WebAppContext webapp = new WebAppContext();
        webapp.setContextPath("/");
        webapp.setWar(System.getProperty(Constants.WAR_FILE_LOCATION));

        server.setHandler(webapp);

        server.start();
        server.join();

    }

}
