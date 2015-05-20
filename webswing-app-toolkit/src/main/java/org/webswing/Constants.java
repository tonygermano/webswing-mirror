package org.webswing;

public interface Constants {

	// Web related constants
	public static final String CLIENT_ID_COOKIE = "webswingID";

	// JMS messages internal
	public static final String SWING_PID_NOTIFICATION = "swingPID";

	// swing startup properties
	public static final String SWING_START_SYS_PROP_CLIENT_ID = "webswing.clientId";
	public static final String SWING_START_SYS_PROP_MAIN_CLASS = "webswing.mainClass";
	public static final String SWING_START_SYS_PROP_CLASS_PATH = "webswing.classPath";
	public static final String SWING_START_SYS_PROP_ISOLATED_FS = "webswing.isolatedFs";
	public static final String SWING_START_SYS_PROP_DIRECTDRAW = "webswing.directdraw";
	public static final String SWING_START_SYS_PROP_DIRECTDRAW_SUPPORTED = "webswing.directdraw.supported";
	public static final String SWING_START_SYS_PROP_ALLOW_DOWNLOAD = "webswing.allowDownload";
	public static final String SWING_START_SYS_PROP_ALLOW_UPLOAD = "webswing.allowUpload";
	public static final String SWING_START_SYS_PROP_ALLOW_DELETE = "webswing.allowDelete";
	public static final String SWING_SESSION_TIMEOUT_SEC = "webswing.sessionTimeoutAfterDisconectSec";

	// JMS queue names
	public static final String SWING2SERVER = "Swing2Server";
	public static final String SERVER2SWING = "Server2Swing";
	public static final String JMS_URL = "webswing.jmsUrl";
	public static final String JMS_URL_DEFAULT = "nio://127.0.0.1:34455";

	// server startup constants
	public static final String WAR_FILE_LOCATION = "webswing.warLocation";
	public static final String ROOT_DIR_PATH = "webswing.rootDir";
	public static final String TEMP_DIR_PATH_BASE = "webswing.tempDirBase";
	public static final String TEMP_DIR_PATH = "webswing.tempDirPath";
	public static final String CREATE_NEW_TEMP = "webswing.createNewTemp";
	public static final String CONFIG_FILE_PATH = "webswing.configFile";
	public static final String DEFAULT_CONFIG_FILE_NAME = "webswing.config";
	public static final String USER_FILE_PATH = "webswing.usersFilePath";
	public static final String SERVER_HOST = "webswing.host";
	public static final String SERVER_PORT = "webswing.port";
	public static final String SERVER_EMBEDED_FLAG = "webswing.server.embeded";;
	public static final String DEFAULT_USER_FILE_NAME = "user.properties";
	public static final String ALLOWED_CORS_ORIGINS = "webswing.corsOrigins";
	public static final String JMS_OVERAL_MEM_LIMIT = "webswing.jmsOveralMemoryLimit";
	public static final String JMS_DEST_MEM_LIMIT = "webswing.jmsDestinationMemoryLimit";

	// swing start related properties
	public static final String SWING_SCREEN_WIDTH = "webswing.screenWidth";
	public static final String SWING_SCREEN_HEIGHT = "webswing.screenHeight";
	public static final int SWING_SCREEN_WIDTH_MIN = 300;
	public static final int SWING_SCREEN_HEIGHT_MIN = 300;
	public static final String USER_NAME_SUBSTITUTE = "user";
	public static final String SESSION_ID_SUBSTITUTE = "sessionId";

	// http request header names
	public static final String HTTP_ATTR_RECORDING_FLAG = "X-webswing-recording";
	public static final String HTTP_ATTR_ARGS = "X-webswing-args";
	public static final String HTTP_ATTR_DEBUG_PORT = "X-webswing-debugPort";

	// admin console constants
	public static final String ADMIN_ROLE = "admin";
	public static final String ADMIN_CONSOLE_APP_NAME = "adminConsoleApplicationName";

}
