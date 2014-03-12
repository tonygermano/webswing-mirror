package org.webswing;


import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;


public class ServerMain {
    
    public static void main(String[] args) throws Exception {
        Configuration config =  ConfigurationImpl.parse(args);


        Server server = new Server(Integer.parseInt(config.getPort()));
        
        WebAppContext webapp  = new WebAppContext();
        webapp.setContextPath("/");
        //webapp.setWar("f:\\DATA\\Workspaces\\play\\WebSwingServer2.0.git\\webswing\\webswing-server\\target\\webswing-server.war");
        webapp.setWar(System.getProperty(Constants.WAR_FILE_LOCATION));
        server.setHandler(webapp);
 
        server.start();
        server.join();

    }

}
