package org.webswing.server.services.websocket;

import java.io.Serializable;

import javax.servlet.http.HttpServletRequest;

import org.atmosphere.cpr.AtmosphereResource;
import org.atmosphere.cpr.AtmosphereResourceEvent;
import org.atmosphere.cpr.FrameworkConfig;

public class WebSocketConnection {

	private AtmosphereResource resource;
	private AtmosphereResourceEvent lastEvent;

	public WebSocketConnection(AtmosphereResource r) {
		this.resource = r;
	}

	public void setEvent(AtmosphereResourceEvent event) {
		this.lastEvent = event;
	}

	public boolean isBinary() {
		return resource.forceBinaryWrite();
	}

	public void write(byte[] protoMessage) {
		resource.write(protoMessage);
	}

	public void write(String jsonMessage) {
		resource.write(jsonMessage);
	}

	public String uuid() {
		return resource.uuid();
	}

	public HttpServletRequest getRequest() {
		return resource.getRequest();
	}

	public Object getSecuritySubject() {
		return resource.getRequest().getAttribute(FrameworkConfig.SECURITY_SUBJECT);
	}

	public void broadcast(Serializable serializable) {
		for (AtmosphereResource r : this.resource.getBroadcaster().getAtmosphereResources()) {
			if (r.uuid().equals(this.resource.uuid())) {
				r.getBroadcaster().broadcast(serializable, r);
			}
		}
	}

}
