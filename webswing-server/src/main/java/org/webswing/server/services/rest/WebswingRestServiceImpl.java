package org.webswing.server.services.rest;

import javax.ws.rs.Path;

import org.webswing.server.util.GitRepositoryState;

@Path("webswing")
public class WebswingRestServiceImpl implements WebswingRestService {

	private static final String default_version = "unresolved";

	@Override
	public String getVersion() {
		String describe = GitRepositoryState.getInstance().getDescribe();
		if (describe == null) {
			return default_version;
		}
		return describe;
	}
}
