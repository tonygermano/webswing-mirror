package org.webswing.server.services.security.api;

public enum WebswingAction {

	//REST
	rest_getServerSettings(Role.admin),
	rest_getSession(Role.admin),
	rest_sessionShutdown(Role.admin),
	rest_sessionShutdownForce(Role.admin),
	rest_getConfig(Role.admin),
	rest_setConfig(Role.admin),
	rest_getConfigVariables(Role.admin),
	rest_getDefaultApplicationConfig(Role.admin),
	rest_getDefaultAppletConfig(Role.admin),
	rest_getApps(Role.authenticated),
	rest_getVersion(Role.anonym),
	//websocket
	websocket_connect(Role.authenticated),
	websocket_startRecordingPlayback(Role.admin),
	websocket_startSwingApplication(Role.authenticated),
	websocket_startMirrorView(Role.admin),
	//file handler
	file_download(Role.authenticated),
	file_upload(Role.authenticated);

	private String[] roles;

	private WebswingAction(String... roles) {

		this.roles = roles;
	}

	private class Role {
		private static final String anonym = WebswingUser.anonymUserName;
		private static final String authenticated = WebswingUser.authenticated;
		private static final String admin = "admin";
	}

	public static class DefaultRolePermissionResolver implements RolePermissionResolver {

		@Override
		public String[] getRolesForPermission(WebswingAction action) {
			return action.roles;
		}

	}
}
