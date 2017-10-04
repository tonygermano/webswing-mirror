package org.webswing.server.extension;

import org.webswing.server.base.PrimaryUrlHandler;
import org.webswing.server.base.UrlHandler;

import java.util.List;

public interface ExtensionProvider {
	List<UrlHandler> createDefaultConfiguration(PrimaryUrlHandler parent);
}
