package org.webswing.server.common.model.security;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.webswing.server.common.service.security.WebswingLoginSessionTokenClaim;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class WebswingLoginSessionTokenClaimProto implements Serializable {
	
	private static final Logger log = LoggerFactory.getLogger(WebswingLoginSessionTokenClaimProto.class);

	private static final long serialVersionUID = 5671635886503020239L;
	
	private List<MapProto> attributes;

	public WebswingLoginSessionTokenClaimProto() {
	}

	public WebswingLoginSessionTokenClaimProto(WebswingLoginSessionTokenClaim tokenClaim) {
		super();

		if (tokenClaim.getAttributes() != null) {
			ObjectMapper mapper = new ObjectMapper();
			
			this.attributes = new ArrayList<>();
			for (Entry<String, Object> entry : tokenClaim.getAttributes().entrySet()) {
				try {
					attributes.add(new MapProto(entry.getKey(), mapper.writeValueAsBytes(entry.getValue())));
				} catch (JsonProcessingException e) {
					log.error("Could not serialize user attribute [" + entry.getKey() + "]!", e);
				}
			}
		}
	}

	public List<MapProto> getAttributes() {
		return attributes;
	}

	public void setAttributes(List<MapProto> attributes) {
		this.attributes = attributes;
	}

}
