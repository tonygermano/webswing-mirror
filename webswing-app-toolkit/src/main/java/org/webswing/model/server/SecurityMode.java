package org.webswing.model.server;

public enum SecurityMode {
	INHERITED,
	NONE,
	PROPERTY_FILE,
	JDBC,
	LDAP,
	ACTIVE_DIRECTORY,
	SAML2,
	CUSTOM
}
