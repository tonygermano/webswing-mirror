package org.webswing.services.impl.connection.impl;

import jakarta.websocket.DeploymentException;
import org.glassfish.tyrus.client.ClientManager;
import org.glassfish.tyrus.client.ClientProperties;
import org.glassfish.tyrus.container.jdk.client.JdkClientContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;

public class WebSocketClient {
	private static final Logger log = LoggerFactory.getLogger(WebSocketClient.class);

	private static ClientManager client;

	public static ClientManager getClient(){
		if(client==null){
			client= ClientManager.createClient(JdkClientContainer.class.getName());
			client.getProperties().put(ClientProperties.SHARED_CONTAINER_IDLE_TIMEOUT, 0);
		}
		return client;
	}

	public void connectToServer(Object annotatedConnectionHndler, String path) throws IOException {
		try {
			getClient().connectToServer(annotatedConnectionHndler, URI.create(path));
		} catch (DeploymentException e) {
			throw new IOException("Failed to connect to websocket",e);
		}
	}

}
