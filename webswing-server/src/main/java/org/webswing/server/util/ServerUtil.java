package org.webswing.server.util;

import java.awt.image.BufferedImage;
import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.ClassUtils;
import org.apache.commons.lang.text.StrSubstitutor;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.webswing.Constants;
import org.webswing.model.MsgOut;
import org.webswing.model.c2s.ConnectionHandshakeMsgIn;
import org.webswing.model.c2s.InputEventsFrameMsgIn;
import org.webswing.model.s2c.AppFrameMsgOut;
import org.webswing.model.s2c.ApplicationInfoMsg;
import org.webswing.model.server.SwingDescriptor;
import org.webswing.model.server.WebswingConfiguration;
import org.webswing.server.services.security.api.SecurityContext;
import org.webswing.server.services.websocket.WebSocketConnection;

import main.Main;

public class ServerUtil {
	public static final int bufferSize = 4 * 1024;
	private static final String DEFAULT = "default";
	private static final Logger log = LoggerFactory.getLogger(ServerUtil.class);
	private static final Map<String, byte[]> iconMap = new HashMap<String, byte[]>();
	private static final ObjectMapper mapper = new ObjectMapper();
	private static final ProtoMapper protoMapper = new ProtoMapper();

	static {
		mapper.setSerializationInclusion(Inclusion.NON_NULL);
	}

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
		Class<?>[] classes = new Class<?>[] { InputEventsFrameMsgIn.class };
		for (Class<?> c : classes) {
			try {
				o = mapper.readValue(s, c);
				break;
			} catch (IOException e) {
				log.error("Failed to decode json message:", e);
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
				log.error("Failed to decode proto message:", e);
			}
		}
		return o;
	}

	public static AppFrameMsgOut decodePlaybackProto(byte[] message) {
		try {
			return protoMapper.decodeProto(message, AppFrameMsgOut.class);
		} catch (IOException e) {
			log.error("Failed to decode proto message:", e);
			return null;
		}
	}

	public static ApplicationInfoMsg toApplicationInfoMsg(String pathPrefix, SwingDescriptor swingDesc, StrSubstitutor subs) {
		ApplicationInfoMsg app = new ApplicationInfoMsg();
		app.setName(swingDesc.getName());
		app.setAlwaysRestart(swingDesc.getSwingSessionTimeout() == 0);
		app.setUrl(pathPrefix + toPath(swingDesc.getPath()));
		File icon = resolveFile(swingDesc.getIcon(), swingDesc.getHomeDir(), subs);
		if (icon == null || !icon.isFile()) {
			app.setBase64Icon(loadImage(null));
		} else {
			app.setBase64Icon(loadImage(icon.getAbsolutePath()));
		}
		return app;
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

	public static File resolveFile(String name, String homeDir, StrSubstitutor external) {
		StrSubstitutor subs = external == null ? getConfigSubstitutor() : external;
		if (name == null) {
			return null;
		}
		name = subs.replace(name);
		homeDir = subs.replace(homeDir);
		File relativeToHomeInRoot = new File(Main.getRootDir(), homeDir + File.separator + name);
		if (relativeToHomeInRoot.exists()) {
			return relativeToHomeInRoot;
		}
		File relativeToHome = new File(homeDir + File.separator + name);
		if (relativeToHome.exists()) {
			return relativeToHome;
		}
		File absolute = new File(name);
		if (absolute.exists()) {
			return absolute;
		}
		return null;
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

	public static String getClientIp(WebSocketConnection r) {
		return r.getRequest().getRemoteAddr();
	}

	public static Map<String, String> getConfigSubstitutorMap(String user, String sessionId, String clientIp, String locale, String customArgs) {

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
		if (clientIp != null) {
			result.put(Constants.SESSION_IP_SUBSTITUTE, clientIp);
		}
		if (locale != null) {
			result.put(Constants.SESSION_LOCALE_SUBSTITUTE, locale);
		}
		if (customArgs != null) {
			result.put(Constants.SESSION_CUSTOMARGS_SUBSTITUTE, customArgs);
		}
		return result;
	}

	public static StrSubstitutor getConfigSubstitutor() {
		return getConfigSubstitutor(null, null, null, null, null);
	}

	public static StrSubstitutor getConfigSubstitutor(String user, String sessionId, String clientIp, String locale, String customArgs) {
		return new StrSubstitutor(getConfigSubstitutorMap(user, sessionId, clientIp, locale, customArgs));
	}

	public static boolean isFileLocked(File file) {
		if (file.exists()) {
			try {
				Path source = file.toPath();
				Path dest = file.toPath().resolveSibling(file.getName() + ".wstest");
				Files.move(source, dest);
				Files.move(dest, source);
				return false;
			} catch (IOException e) {
				return true;
			}
		}
		return false;
	}

	public static String resolveInstanceIdForMode(WebSocketConnection r, ConnectionHandshakeMsgIn h, SwingDescriptor conf) {
		if (h.isMirrored()) {
			return h.getClientId();
		} else {
			switch (conf.getSessionMode()) {
			case ALWAYS_NEW_SESSION:
				return h.getClientId() + h.getViewId();
			case CONTINUE_FOR_BROWSER:
				return h.getClientId();
			case CONTINUE_FOR_USER:
				return r.getUser().getUserId();
			default:
				return h.getClientId();
			}
		}
	}

	public static void transferStreams(InputStream is, OutputStream os) throws IOException {
		try {
			byte[] buf = new byte[bufferSize];
			int bytesRead;
			while ((bytesRead = is.read(buf)) != -1)
				os.write(buf, 0, bytesRead);
		} finally {
			is.close();
			os.close();
		}
	}

	public static File getConfigFile(boolean backup) {
		String configFile = System.getProperty(Constants.CONFIG_FILE_PATH);
		if (configFile == null) {
			String war = ServerUtil.getWarFileLocation();
			configFile = war.substring(0, war.lastIndexOf("/") + 1) + Constants.DEFAULT_CONFIG_FILE_NAME;
			System.setProperty(configFile, Constants.CONFIG_FILE_PATH);
		}
		configFile = backup ? configFile + ".backup" : configFile;
		File config = new File(URI.create(configFile));
		return config;
	}

	@SuppressWarnings("unchecked")
	public static <T> T instantiateConfig(Map<String, Object> c, final Class<T> clazz, final SecurityContext context) {
		if (c == null) {
			c = new HashMap<>();
		}
		final Map<String, Object> config = c;
		return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class[] { clazz }, new InvocationHandler() {

			@Override
			@SuppressWarnings("rawtypes")
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
				BeanInfo info = Introspector.getBeanInfo(method.getDeclaringClass());
				PropertyDescriptor[] pds = info.getPropertyDescriptors();
				if (method.getName().equals("getValueAs") && method.getParameters().length == 2 && args[0] instanceof String && args[1] instanceof Class) {
					String s = (String) args[0];
					Class c = (Class) args[1];
					Object o = config.get(s);
					Map<String, Object> subConfig = (Map<String, Object>) (o != null && o instanceof HashMap ? o : new HashMap<>());
					return instantiateConfig(subConfig, c, context);
				}
				for (PropertyDescriptor pd : pds) {
					if (pd.getReadMethod().equals(method)) {
						if (ClassUtils.isAssignable(SecurityContext.class, method.getReturnType(), false)) {
							return context;
						}
						Object value = config.get(pd.getName());
						if (value != null) {
							if (ClassUtils.isAssignable(value.getClass(), method.getReturnType(), true)) {
								if (value instanceof Map) {
									Type returnType = method.getGenericReturnType();
									if (returnType instanceof ParameterizedType) {
										Type[] generics = ((ParameterizedType) returnType).getActualTypeArguments();
										if (generics != null && generics[1] instanceof Class && ((Class) generics[1]).isInterface()) {
											Map valueMap = (Map) value;
											Map resultMap = new HashMap<>();
											for (Object key : valueMap.keySet()) {
												Object entryValue = valueMap.get(key);
												resultMap.put(key, instantiateConfig((Map<String, Object>) entryValue, (Class) generics[1], context));
											}
											return resultMap;
										}
									}
								}
								if (value instanceof List) {
									Type returnType = method.getGenericReturnType();
									if (returnType instanceof ParameterizedType) {
										Type[] generics = ((ParameterizedType) returnType).getActualTypeArguments();
										if (generics != null && generics[0] instanceof Class && ((Class) generics[0]).isInterface()) {
											List valuelist = (List) value;
											List resultList = new ArrayList<>();
											for (Object item : valuelist) {
												resultList.add(instantiateConfig((Map<String, Object>) item, (Class) generics[0], context));
											}
											return resultList;
										}
									}
								}
								return value;
							} else if (value instanceof Map && method.getReturnType().isInterface()) {
								return instantiateConfig((Map) value, method.getReturnType(), context);
							} else {
								log.error("Invalid SecurityModule configuration. Type of " + clazz.getName() + "." + pd.getName() + " is not " + method.getReturnType());
								return null;
							}
						}
					}
				}
				return null;
			}
		});
	}

	public static List<SwingDescriptor> getAllApps(WebswingConfiguration config) {
		ArrayList<SwingDescriptor> all = new ArrayList<SwingDescriptor>();
		all.addAll(config.getApplications());
		all.addAll(config.getApplets());
		return all;
	}

	public static URL getWebResource(String resource, ServletContext servletContext, File webFolder) {
		URL result = null;
		if (webFolder != null && webFolder.isDirectory()) {
			File file = new File(webFolder, resource);
			if (file.isFile()) {
				try {
					result = file.toURI().toURL();
				} catch (MalformedURLException e) {
					log.error("Failed to get file from webFolder.", e);
				}
			}
		}
		if (result == null) {
			try {
				result = servletContext.getResource(resource);
			} catch (MalformedURLException e) {
				log.error("Failed to get file from Web context path.", e);
			}
		}
		return result;
	}

	public static String generateClassPathString(Collection<String> classPathEntries) {
		String result = "";
		if (classPathEntries != null) {
			for (String cpe : classPathEntries) {
				result += cpe + ";";
			}
			result = result.length() > 0 ? result.substring(0, result.length() - 1) : result;
		}
		return result;
	}

	public static boolean isSubPath(String subpath, String path) {
		return path.equals(subpath) || path.startsWith(subpath + "/");
	}

	public static String toPath(String path) {
		String mapping = path == null ? "/" : path;
		mapping = mapping.startsWith("/") ? mapping : ("/" + mapping);
		mapping = mapping.endsWith("/") ? mapping.substring(0, mapping.length() - 1) : mapping;
		return mapping;
	}

}
