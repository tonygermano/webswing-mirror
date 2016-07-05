package org.webswing.server.services.websocket;

import java.io.Serializable;

import javax.servlet.http.HttpServletRequest;

import org.atmosphere.cpr.AtmosphereResource;
import org.webswing.server.base.UrlHandler;
import org.webswing.server.services.security.SecurityManagerService;
import org.webswing.server.services.security.api.WebswingUser;
import org.webswing.server.util.SecurityUtil;

public class WebSocketConnection {

	private AtmosphereResource resource;
	private UrlHandler handler;

	public WebSocketConnection(AtmosphereResource resource, UrlHandler handler) {
		this.resource = resource;
		this.handler = handler;
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
		return resource.getRequest().getAttribute(SecurityManagerService.SECURITY_SUBJECT);
	}

	public WebswingUser getUser() {
		return SecurityUtil.getUser(this);
	}

	public void broadcast(Serializable serializable) {
		for (AtmosphereResource r : this.resource.getBroadcaster().getAtmosphereResources()) {
			if (r.uuid().equals(this.resource.uuid())) {
				r.getBroadcaster().broadcast(serializable, r);
			}
		}
	}

	public UrlHandler getHandler() {
		return handler;
	}

}
