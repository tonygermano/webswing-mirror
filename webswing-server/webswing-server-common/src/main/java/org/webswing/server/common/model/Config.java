package org.webswing.server.common.model;

import org.webswing.server.common.model.meta.ConfigType;

@ConfigType
public interface Config {
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
