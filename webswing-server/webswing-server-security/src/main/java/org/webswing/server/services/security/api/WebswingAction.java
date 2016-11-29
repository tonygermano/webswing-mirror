package org.webswing.server.services.security.api;

/**
 * Actions or permissions defined within Webswing server to execute actions.
 * Each action defines default roles mappings for each permission. These mappings can be 
 * customized by implementing {@link RolePermissionResolver} interface.
 */
public enum WebswingAction {

	//ADMIN CONSOLE
	rest_getPaths(Role.admin),
	rest_getAppInfo(Role.admin),
	rest_getSession(Role.admin),
	rest_sessionShutdown(Role.admin),
	rest_sessionShutdownForce(Role.admin),
	rest_getConfig(Role.admin),
	rest_setConfig(Role.admin),
	rest_startApp(Role.admin),
	rest_stopApp(Role.admin),
	rest_createApp(Role.admin),
	rest_removeApp(Role.admin),
	rest_viewLogs(Role.admin),
	rest_getApps(Role.authenticated),
	//Security 
	rest_getOneTimePassword(Role.admin),
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
		private static final String authenticated = AbstractWebswingUser.ROLE_AUTHENTICATED;
		private static final String admin = "admin";
	}

	public static class DefaultRolePermissionResolver implements RolePermissionResolver {

		@Override
		public String[] getRolesForPermission(String action) {
			try {
				return WebswingAction.valueOf(action).roles;
			} catch (Exception e) {
				return new String[] { Role.admin };
			}
		}

	}
}
