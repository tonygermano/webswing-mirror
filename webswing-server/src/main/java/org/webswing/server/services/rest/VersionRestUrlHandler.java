package org.webswing.server.services.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import org.webswing.server.base.UrlHandler;
import org.webswing.server.util.GitRepositoryState;

public class VersionRestUrlHandler extends AbstractRestUrlHandler {
	private static final String default_version = "unresolved";

	public VersionRestUrlHandler(UrlHandler parent) {
		super(parent);
	}

	@GET
	@Path("/version")
	public String getVersion() {
		String describe = GitRepositoryState.getInstance().getDescribe();
		if (describe == null) {
			return default_version;
		}
		return describe;
	}

}
