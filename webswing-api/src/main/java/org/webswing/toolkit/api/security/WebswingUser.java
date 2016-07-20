package org.webswing.toolkit.api.security;

import java.io.Serializable;
import java.util.Map;

public interface WebswingUser{

	String getUserId();

	Map<String, Serializable> getUserAttributes();

}
