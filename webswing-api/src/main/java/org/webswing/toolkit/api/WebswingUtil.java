package org.webswing.toolkit.api;

import java.awt.Toolkit;

public class WebswingUtil {

	public static boolean isWebswing() {
		Toolkit t = Toolkit.getDefaultToolkit();
		return t instanceof WebswingApiProvider;
	}

	public static WebswingApi getWebswingApi() {
		if (isWebswing()) {
			return ((WebswingApiProvider) Toolkit.getDefaultToolkit()).get();
		} else {
			return null;
		}
	}

}
