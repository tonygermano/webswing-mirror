package org.webswing.server.services.security.api;

/**
 * Configuration and context holder for {@link WebswingSecurityModule} implementations.
 * This interface is dynamically instantiated by Webswing to provide easy access to JSON configuration
 * values stored in webswing.config file.
 * {@link WebswingSecurityModule} implementations should extend this interface with their
 * own specific java beans getters, to read configuration options from JSON format.  
 */
public interface WebswingSecurityModuleConfig {
	/**
	 * Context of  for {@link WebswingSecurityModule}. Used to resolve web resources or local files.
	 * @return the Context implementation
	 */
	SecurityContext getContext();

	/**
	 * Instantiates a dynamic object of type <code>clazz</code> as a view of JSON object stored under Json
	 * property <code>name</name>.
	 * 
	 * @param name name of Json property 
	 * @param clazz interface type to be created
	 * @return instance of <code>clazz</code>
	 */
	<T> T getValueAs(String name, Class<T> clazz);
}
