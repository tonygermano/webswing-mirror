package org.webswing.server.util;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;
import javax.servlet.http.HttpServletRequest;

import main.Main;

import org.apache.commons.lang.text.StrSubstitutor;
import org.apache.shiro.realm.text.PropertiesRealm;
import org.apache.shiro.subject.Subject;
import org.atmosphere.cpr.AtmosphereResource;
import org.atmosphere.cpr.FrameworkConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.webswing.Constants;
import org.webswing.model.MsgOut;
import org.webswing.model.admin.c2s.ApplyConfigurationMsgIn;
import org.webswing.model.admin.s2c.AdminConsoleFrameMsgOut;
import org.webswing.model.admin.s2c.MessageMsg;
import org.webswing.model.admin.s2c.MessageMsg.Type;
import org.webswing.model.admin.s2c.SwingSessionMsg;
import org.webswing.model.c2s.ConnectionHandshakeMsgIn;
import org.webswing.model.c2s.InputEventsFrameMsgIn;
import org.webswing.model.s2c.ApplicationInfoMsg;
import org.webswing.model.server.SwingApplicationDescriptor;
import org.webswing.model.server.WebswingConfiguration;
import org.webswing.server.ConfigurationManager;
import org.webswing.server.SwingInstance;
import org.webswing.server.handler.FileServlet;
import org.webswing.server.handler.LoginServlet;
import org.webswing.server.model.EncodedMessage;

public class ServerUtil {

	private static final String DEFAULT = "default";
	private static final Logger log = LoggerFactory.getLogger(ServerUtil.class);
	private static final Map<String, byte[]> iconMap = new HashMap<String, byte[]>();
	private static final ObjectMapper mapper = new ObjectMapper();
	private static final ProtoMapper protoMapper = new ProtoMapper();

	public static String encode2Json(MsgOut m) {
		try {
			return mapper.writeValueAsString(m);
		} catch (IOException e) {
			log.error("Json encoding object failed: " + m, e);
			return null;
		}
	}

	public static byte[] encode2Proto(MsgOut m) {
		try {
			return protoMapper.encodeProto(m);
		} catch (IOException e) {
			log.error("Proto encoding object failed: " + m, e);
			return null;
		}
	}

	public static Object decodeJson(String s) {
		Object o = null;
		Class<?>[] classes = new Class<?>[] { InputEventsFrameMsgIn.class, ApplyConfigurationMsgIn.class };
		for (Class<?> c : classes) {
			try {
				o = mapper.readValue(s, c);
				break;
			} catch (IOException e) {
				// do nothing
			}
		}
		return o;
	}

	public static Object decodeProto(byte[] message) {
		Object o = null;
		Class<?>[] classes = new Class<?>[] { InputEventsFrameMsgIn.class };
		for (Class<?> c : classes) {
			try {
				o = protoMapper.decodeProto(message, c);
				break;
			} catch (IOException e) {
				// do nothing
			}
		}
		return o;
	}

	public static List<ApplicationInfoMsg> createApplicationInfoMsg(AtmosphereResource r, boolean includeAdminApp) {
		Map<String, SwingApplicationDescriptor> applications = ConfigurationManager.getInstance().getApplications();
		List<ApplicationInfoMsg> apps = new ArrayList<ApplicationInfoMsg>();
		StrSubstitutor subs = getConfigSubstitutorMap(getUserName(r), null);
		if (applications.size() == 0) {
			return null;
		} else {
			for (String name : applications.keySet()) {
				SwingApplicationDescriptor descriptor = applications.get(name);
				if (isUserAuthorizedForApplication(r, descriptor)) {
					ApplicationInfoMsg app = new ApplicationInfoMsg();
					app.setName(name);
					String icon = subs.replace(descriptor.getIcon());
					String homeDir = subs.replace(descriptor.getHomeDir());
					if (icon == null) {
						app.setBase64Icon(loadImage(null));
					} else {
						if (new File(icon).exists()) {
							app.setBase64Icon(loadImage(icon));
						} else {
							if (new File(homeDir + File.separator + icon).exists()) {
								app.setBase64Icon(loadImage(homeDir + File.separator + icon));
							} else if (new File(Main.getRootDir(), homeDir + File.separator + icon).exists()) {
								app.setBase64Icon(loadImage(new File(Main.getRootDir(), homeDir + File.separator + icon).getAbsolutePath()));
							} else {
								log.error("Icon loading failed. File " + icon + " or " + homeDir + File.separator + icon + " does not exist.");
								app.setBase64Icon(loadImage(null));
							}
						}
					}
					apps.add(app);
				}
			}
			if (includeAdminApp) {
				ApplicationInfoMsg adminConsole = new ApplicationInfoMsg();
				adminConsole.setName(Constants.ADMIN_CONSOLE_APP_NAME);
				apps.add(adminConsole);
			}
		}
		return apps;
	}

	public static boolean isUserAuthorized(AtmosphereResource r, SwingApplicationDescriptor app, ConnectionHandshakeMsgIn h) {

		// mirror view
		if (h.isMirrored()) {
			if (isUserinRole(r, Constants.ADMIN_ROLE)) {
				return true;
			}
			return false;
		}
		// regular
		return isUserAuthorizedForApplication(r, app);
	}

	public static boolean isUserAuthorizedForApplication(AtmosphereResource r, SwingApplicationDescriptor app) {
		if ((app.isAuthentication() || app.isAuthorization()) && isUserAnonymous(r)) {
			return false;
		}
		if (app.isAuthorization()) {
			if (isUserinRole(r, app.getName()) || isUserinRole(r, Constants.ADMIN_ROLE)) {
				return true;
			}
			return false;
		} else {
			return true;
		}
	}

	private static byte[] loadImage(String icon) {
		try {
			if (icon == null) {
				if (iconMap.containsKey(DEFAULT)) {
					return iconMap.get(DEFAULT);
				} else {
					BufferedImage defaultIcon = ImageIO.read(ServerUtil.class.getClassLoader().getResourceAsStream("images/java.png"));
					byte[] b64icon = getPngImage(defaultIcon);
					iconMap.put(DEFAULT, b64icon);
					return b64icon;
				}
			} else {
				if (iconMap.containsKey(icon)) {
					return iconMap.get(icon);
				} else {
					BufferedImage defaultIcon = ImageIO.read(new File(icon));
					byte[] b64icon = getPngImage(defaultIcon);
					iconMap.put(icon, b64icon);
					return b64icon;
				}
			}
		} catch (IOException e) {
			log.error("Failed to load image " + icon, e);
			return null;
		}
	}

	private static byte[] getPngImage(BufferedImage imageContent) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageOutputStream ios = ImageIO.createImageOutputStream(baos);
			ImageIO.write(imageContent, "png", ios);
			byte[] result = baos.toByteArray();
			baos.close();
			return result;
		} catch (IOException e) {
			log.error("Writing image interupted:" + e.getMessage(), e);
		}
		return null;
	}

	public static String getUserPropsFileName() {
		String userFile = System.getProperty(Constants.USER_FILE_PATH);
		if (userFile == null) {
			String war = ServerUtil.getWarFileLocation();
			userFile = war.substring(0, war.lastIndexOf("/") + 1) + Constants.DEFAULT_USER_FILE_NAME;
			System.setProperty(userFile, Constants.USER_FILE_PATH);
		}
		return userFile;
	}

	public static String getWarFileLocation() {
		String warFile = System.getProperty(Constants.WAR_FILE_LOCATION);
		if (warFile == null) {
			ProtectionDomain domain = Main.class.getProtectionDomain();
			URL location = domain.getCodeSource().getLocation();
			String locationString = location.toExternalForm();
			if (locationString.endsWith("/WEB-INF/classes/")) {
				locationString = locationString.substring(0, locationString.length() - "/WEB-INF/classes/".length());
			}
			System.setProperty(Constants.WAR_FILE_LOCATION, locationString);
			return locationString;
		}
		return warFile;
	}

	public static SwingSessionMsg composeSwingInstanceStatus(SwingInstance si) {
		SwingSessionMsg result = new SwingSessionMsg();
		result.setId(si.getClientId());
		result.setApplication(si.getApplicationName());
		result.setConnected(si.getSessionId() != null);
		if (!result.getConnected()) {
			result.setDisconnectedSince(si.getDisconnectedSince());
		}
		result.setStartedAt(si.getStartedAt());
		result.setUser(si.getUser());
		result.setEndedAt(si.getEndedAt());
		result.setState(si.getStats());
		return result;
	}

	public static String getUserName(AtmosphereResource resource) {
		Subject sub = (Subject) resource.getRequest().getAttribute(FrameworkConfig.SECURITY_SUBJECT);
		if (sub != null) {
			return sub.getPrincipal() + "";
		}
		return null;
	}

	public static boolean isUserinRole(AtmosphereResource resource, String role) {
		Subject sub = (Subject) resource.getRequest().getAttribute(FrameworkConfig.SECURITY_SUBJECT);
		if (sub != null) {
			return sub.hasRole(role);
		}
		return false;
	}

	public static boolean isUserAnonymous(AtmosphereResource resource) {
		if (LoginServlet.anonymUserName.equals(getUserName(resource))) {
			return true;
		}
		return false;
	}

	public static boolean validateConfigFile(byte[] content) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		mapper.readValue(content, WebswingConfiguration.class);
		return true;
	}

	public static boolean validateUserFile(byte[] content) throws IOException {
		PropertiesRealm r = new PropertiesRealm();
		String tmpFileName = FileServlet.registerFile(content, UUID.randomUUID().toString(), 10, TimeUnit.SECONDS, "");
		r.setResourcePath(tmpFileName);
		r.init();
		return true;
	}

	public static String composeAdminErrorReply(Exception e) {
		return createJsonMessageFrame(Type.danger, e.getMessage());
	}

	public static String composeAdminSuccessReply(String s) {
		return createJsonMessageFrame(Type.success, s);
	}

	private static String createJsonMessageFrame(Type t, String text) {
		AdminConsoleFrameMsgOut response = new AdminConsoleFrameMsgOut();
		MessageMsg message = new MessageMsg();
		message.setType(t);
		message.setText(text);
		message.setTime(new Date());
		response.setMessage(message);
		return encode2Json(response);
	}

	public static boolean isRecording(HttpServletRequest r) {
		String recording = (String) r.getHeader(Constants.HTTP_ATTR_RECORDING_FLAG);
		return Boolean.parseBoolean(recording);
	}

	public static String getCustomArgs(HttpServletRequest r) {
		String args = (String) r.getHeader(Constants.HTTP_ATTR_ARGS);
		return args != null ? args : "";
	}

	public static int getDebugPort(HttpServletRequest r) {
		String recording = (String) r.getHeader(Constants.HTTP_ATTR_DEBUG_PORT);
		try {
			return Integer.parseInt(recording);
		} catch (NumberFormatException e) {
			return 0;
		}
	}

	public static StrSubstitutor getConfigSubstitutorMap(String user, String sessionId) {

		Map<String, String> result = new HashMap<String, String>();
		result.putAll(System.getenv());
		for (final String name : System.getProperties().stringPropertyNames()) {
			result.put(name, System.getProperties().getProperty(name));
		}
		if (user != null) {
			result.put(Constants.USER_NAME_SUBSTITUTE, user);
		}
		if (sessionId != null) {
			result.put(Constants.SESSION_ID_SUBSTITUTE, sessionId);
		}

		return new StrSubstitutor(result);
	}

	public static void broadcastMessage(AtmosphereResource r, EncodedMessage o) {
		for (AtmosphereResource resource : r.getBroadcaster().getAtmosphereResources()) {
			if (resource.uuid().equals(r.uuid())) {
				resource.getBroadcaster().broadcast(resource.forceBinaryWrite() ? o.getProtoMessage() : o.getJsonMessage(), resource);
			}
		}
	}

	public static void broadcastMessage(AtmosphereResource r, MsgOut o) {
		broadcastMessage(r, new EncodedMessage(o));
	}

}
