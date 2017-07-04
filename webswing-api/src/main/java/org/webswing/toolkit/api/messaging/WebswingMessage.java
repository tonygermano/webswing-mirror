package org.webswing.toolkit.api.messaging;

/**
 * Wrapper class for the message object
 * @param <T>
 */
public interface WebswingMessage<T> {

	/**
	 * @return the actual deserialize object received from topic.
	 */
	T getMessage();

}
