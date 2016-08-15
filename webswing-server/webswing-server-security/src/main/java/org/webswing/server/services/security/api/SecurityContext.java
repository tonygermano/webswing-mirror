package org.webswing.server.services.security.api;

import java.io.File;
import java.net.URL;

/**
 * Context for {@link WebswingSecurityModule} stored in {@link WebswingSecurityModuleConfig} that provides access to resources and Webswing specific services.
 */
public interface SecurityContext {

	/**
	 * Search for resource using in application's home folder if path is relative
	 * and check for existence. 
	 * @param name name of resource.
	 * @return existing file or null
	 */
	File resolveFile(String name);

	/**
	 * Returns URL of static web resource if exists. 
	 * @param resource resource path relative to web context
	 * @return URL or null 
	 */
	URL getWebResource(String resource);

	/**
	 * Replace variables in in form <code>${variableName}</code> with their respective value
	 * from environment variables or system properties
	 * @param string string with variables
	 * @return  string with variables replaced
	 */
	String replaceVariables(String string);

	/**
	 * Retrieves object from session if exists.
	 * @param attributeName name of session attribute
	 * @return object if present in session
	 */
	Object getFromSecuritySession(String attributeName);

	/**
	 * Stores object in session.
	 * 
	 * @param attributeName name of attribute
	 * @param value object to store
	 */
	void setToSecuritySession(String attributeName, Object value);

	/**
	 * @return the root URK path where the {@link WebswingSecurityModule} related to this context 
	 * is used.  
	 */
	String getSecuredPath();
}
