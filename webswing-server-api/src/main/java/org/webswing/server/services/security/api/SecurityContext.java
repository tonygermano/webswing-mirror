package org.webswing.server.services.security.api;

import java.io.File;
import java.net.URL;

public interface SecurityContext {

	File resolveFile(String name);

	URL getWebResource(String resource);

}
