package org.webswing.toolkit.api;

import java.awt.Toolkit;

/**
 * Helper class to get API instance
 */
public class WebswingUtil {

	/**
	 * @return true if this instance is running inside Webswing app container
	 */
	public static boolean isWebswing() {
		Toolkit t = Toolkit.getDefaultToolkit();
		return t instanceof WebswingApiProvider;
	}

	/**
	 * @return Webswing API instance or null if not running in Webswing app container
	 */
	public static WebswingApi getWebswingApi() {
		if (isWebswing()) {
			return ((WebswingApiProvider) Toolkit.getDefaultToolkit()).get();
		} else {
			return null;
		}
	}

}
