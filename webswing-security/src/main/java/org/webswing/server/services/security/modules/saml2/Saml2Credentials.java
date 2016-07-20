package org.webswing.server.services.security.modules.saml2;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.webswing.server.services.security.api.WebswingCredentials;

public class Saml2Credentials implements WebswingCredentials, Serializable {
	private static final long serialVersionUID = -4072080527374898315L;
	private String samlToken = null;
	private String name = null;
	private Map<String, Serializable> attributes = new HashMap<String, Serializable>();

	public Saml2Credentials(String samlToken, String name, Map<String, List<String>> map) {
		this.samlToken = samlToken;
		this.name = name;
		for (Entry<String, List<String>> e : map.entrySet()) {
			if (e.getValue() != null && e.getValue().size() == 1) {
				attributes.put(e.getKey(), e.getValue().get(0));
			} else {
				attributes.put(e.getKey(), new ArrayList<String>(e.getValue()));
			}
		}
	}

	public Object getCredentials() {
		return samlToken;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Map<String, Serializable> getAttributes() {
		return attributes;
	}

}
