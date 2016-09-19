package org.webswing;

public interface Constants {

	// Web related constants
	public static final String CLIENT_ID_COOKIE = "webswingID";

	// swing startup properties
	public static final String SWING_START_SYS_PROP_CLIENT_ID = "webswing.clientId";
	public static final String SWING_START_SYS_PROP_JMS_ID = "webswing.jmsQueueId";
	public static final String SWING_START_SYS_PROP_MAIN_CLASS = "webswing.mainClass";
	public static final String SWING_START_SYS_PROP_CLASS_PATH = "webswing.classPath";
	public static final String SWING_START_SYS_PROP_ISOLATED_FS = "webswing.isolatedFs";
	public static final String SWING_START_SYS_PROP_TRANSFER_DIR = "webswing.transfer.dir";
	public static final String SWING_START_SYS_PROP_DIRECTDRAW = "webswing.directdraw";
	public static final String SWING_START_SYS_PROP_DIRECTDRAW_SUPPORTED = "webswing.directdraw.supported";
	public static final String SWING_START_SYS_PROP_ALLOW_DOWNLOAD = "webswing.allowDownload";
	public static final String SWING_START_SYS_PROP_ALLOW_AUTO_DOWNLOAD = "webswing.allowAutoDownload";
	public static final String SWING_START_SYS_PROP_ALLOW_UPLOAD = "webswing.allowUpload";
	public static final String SWING_START_SYS_PROP_ALLOW_AUTO_UPLOAD = "webswing.allowAutoUpload";
	public static final String SWING_START_SYS_PROP_ALLOW_DELETE = "webswing.allowDelete";
	public static final String SWING_START_SYS_PROP_ALLOW_JSLINK = "webswing.allowJsLink";
	public static final String SWING_SESSION_TIMEOUT_SEC = "webswing.sessionTimeoutAfterDisconectSec";
	public static final String SWING_START_SYS_PROP_SYNC_TIMEOUT = "webswing.syncCallTimeout";
	public static final String SWING_START_SYS_PROP_THEME = "webswing.theme";
	public static final String SWING_START_SYS_PROP_WAIT_FOR_EXIT = "webswing.waitForExit";

	// applet startup properties
	public static final String SWING_START_SYS_PROP_APPLET_CLASS = "webswing.appletClass";
	public static final String SWING_START_STS_PROP_APPLET_PARAM_PREFIX = "webswing.appletParam_";
	public static final String SWING_START_SYS_PROP_APPLET_DOCUMENT_BASE = "webswing.appletDocumentBase";

	// JMS queue names
	public static final String SWING2SERVER = "Swing2Server";
	public static final String SERVER2SWING = "Server2Swing";
	public static final String SERVER2SWING_SYNC = "Server2SwingSync";
	public static final String JMS_URL = "webswing.jmsUrl";
	public static final String JMS_URL_DEFAULT = "nio://127.0.0.1:34455";
	public static final String JMS_SERIALIZABLE_PACKAGES = "*";

	// server startup constants
	public static final String WAR_FILE_LOCATION = "webswing.warLocation";
	public static final String ROOT_DIR_URI = "webswing.rootDirUri";
	public static final String ROOT_DIR_PATH = "webswing.rootDir";
	public static final String TEMP_DIR_PATH_BASE = "webswing.tempDirBase";
	public static final String TEMP_DIR_PATH = "webswing.tempDirPath";
	public static final String CREATE_NEW_TEMP = "webswing.createNewTemp";
	public static final String CONFIG_FILE_PATH = "webswing.configFile";
	public static final String DEFAULT_CONFIG_FILE_NAME = "webswing.config";
	public static final String SERVER_HOST = "webswing.host";
	public static final String SERVER_PORT = "webswing.port";
	public static final String SERVER_EMBEDED_FLAG = "webswing.server.embeded";;
	public static final String JMS_OVERAL_MEM_LIMIT = "webswing.jmsOveralMemoryLimit";
	public static final String JMS_DEST_MEM_LIMIT = "webswing.jmsDestinationMemoryLimit";
	public static final String WEBSOCKET_MESSAGE_SIZE = "webswing.websocketMessageSizeLimit";
	public static final String WEBSOCKET_THREAD_POOL = "webswing.websocketThreadPoolLimit";

	public static final String FILE_SERVLET_WAIT_TIMEOUT = "webswing.fileServletWaitTimeout";

	// swing start related properties
	public static final String SWING_SCREEN_WIDTH = "webswing.screenWidth";
	public static final String SWING_SCREEN_HEIGHT = "webswing.screenHeight";
	public static final int SWING_SCREEN_WIDTH_MIN = 300;
	public static final int SWING_SCREEN_HEIGHT_MIN = 300;

	//webswing configuration variables
	public static final String USER_NAME_SUBSTITUTE = "user";
	public static final String SESSION_ID_SUBSTITUTE = "clientId";
	public static final String SESSION_IP_SUBSTITUTE = "clientIp";
	public static final String SESSION_LOCALE_SUBSTITUTE = "clientLocale";
	public static final String SESSION_CUSTOMARGS_SUBSTITUTE = "customArgs";
	public static final String HOME_FOLDER_SUBSTITUTE = "webswing.homeFolder";

	// http request header names
	public static final String HTTP_ATTR_RECORDING_FLAG = "X-webswing-recording";
	public static final String HTTP_ATTR_ARGS = "X-webswing-args";
	public static final String HTTP_ATTR_DEBUG_PORT = "X-webswing-debugPort";


}
