package org.webswing.security.modules.saml2;

import java.io.Serializable;
import java.util.*;
import java.util.Map.Entry;

import org.joda.time.DateTime;
import org.pac4j.saml.profile.SAML2Profile;
import org.webswing.server.services.security.api.AbstractWebswingUser;

public class Saml2User extends AbstractWebswingUser {
	private final SAML2Profile profile;
	private final String name ;
	private final String userAttributeName;
	private final String rolesAttributeName;
	private Map<String, Serializable> attributes = new HashMap<String, Serializable>();

	public Saml2User(SAML2Profile profile, String name, Map<String, Object> map, String userAttributeName, String rolesAttributeName) {
		this.profile = profile;
		this.name = name;
		this.userAttributeName = userAttributeName;
		this.rolesAttributeName = rolesAttributeName;
		for (Entry<String, Object> e : map.entrySet()) {
			if (e.getValue() != null && e instanceof List && ((List) e.getValue()).size() == 1) {
				attributes.put(e.getKey(), (Serializable) ((List) e.getValue()).get(0));
			} else if (e.getValue() instanceof List){
				attributes.put(e.getKey(), new ArrayList<String>((List) e.getValue()));
			}else if (e.getValue() instanceof DateTime){
				attributes.put(e.getKey(), ((DateTime) e.getValue()).toDate());
			}else if (e.getValue() instanceof Serializable){
				attributes.put(e.getKey(), ((Serializable) e.getValue()));
			}else{
				attributes.put(e.getKey(), e.getValue()==null?null:e.getValue().toString());
			}
		}
	}

	@Override
	public String getUserId() {
		if(userAttributeName!=null){
			Serializable user = attributes.get(userAttributeName);
			if(user!=null && user instanceof String){
				return (String) user;
			}
		}
		return name;
	}

	@Override
	public Map<String, Serializable> getUserAttributes() {
		return attributes;
	}

	@Override
	public boolean hasRole(String role) {
		if(rolesAttributeName!=null){
			Serializable roles = attributes.get(rolesAttributeName);
			if(roles!=null && roles instanceof String){
				return roles.equals(role);
			}
			if(roles!=null && roles instanceof Collection){
				return ((Collection) roles).contains(role);
			}
		}
		return false;
	}

	public SAML2Profile getProfile() {
		return profile;
	}
}
