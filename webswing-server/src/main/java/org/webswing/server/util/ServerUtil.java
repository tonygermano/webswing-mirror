package org.webswing.server.util;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;

import main.Main;

import org.apache.commons.codec.binary.Base64;
import org.apache.shiro.realm.text.PropertiesRealm;
import org.apache.shiro.subject.Subject;
import org.atmosphere.cpr.AtmosphereResource;
import org.atmosphere.cpr.FrameworkConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.webswing.Constants;
import org.webswing.model.admin.s2c.JsonAdminConsoleFrame;
import org.webswing.model.admin.s2c.JsonSwingSession;
import org.webswing.model.s2c.JsonApplication;
import org.webswing.model.server.SwingApplicationDescriptor;
import org.webswing.model.server.WebswingConfiguration;
import org.webswing.server.SwingInstance;
import org.webswing.server.handler.FileServlet;

public class ServerUtil {

    private static final String DEFAULT = "default";
    private static final Logger log = LoggerFactory.getLogger(ServerUtil.class);
    private static final Map<String, String> iconMap = new HashMap<String, String>();

    public static List<JsonApplication> createApplicationJsonInfo(AtmosphereResource r, Map<String, SwingApplicationDescriptor> applications) {
        List<JsonApplication> apps = new ArrayList<JsonApplication>();
        if (applications.size() == 0) {
            return null;
        } else {
            for (String name : applications.keySet()) {
                SwingApplicationDescriptor descriptor = applications.get(name);
                if(!isUserAuthorizedForApplication(r,descriptor)){
                    continue;
                }
                JsonApplication app = new JsonApplication();
                app.name = name;
                if (descriptor.getIcon() == null) {
                    app.base64Icon = loadImage(null);
                } else {
                    if (new File(descriptor.getIcon()).exists()) {
                        app.base64Icon = loadImage(descriptor.getIcon());
                    } else if (new File(descriptor.getHomeDir() + File.separator + descriptor.getIcon()).exists()) {
                        app.base64Icon = loadImage(descriptor.getHomeDir() + File.separator + descriptor.getIcon());
                    } else {
                        log.error("Icon loading failed. File " + descriptor.getIcon() + " or " + descriptor.getHomeDir() + File.separator + descriptor.getIcon() + " does not exist.");
                        app.base64Icon = loadImage(null);
                    }
                }
                apps.add(app);
            }
        }
        return apps;
    }

    public static boolean isUserAuthorizedForApplication(AtmosphereResource r, SwingApplicationDescriptor app) {
        if (app.isAuthorization()) {
            if (isUserinRole(r, app.getName()) || isUserinRole(r, Constants.ADMIN_ROLE)) {
                return true;
            }
            return false;
        } else {
            return true;
        }
    }

    private static String loadImage(String icon) {
        try {
            if (icon == null) {
                if (iconMap.containsKey(DEFAULT)) {
                    return iconMap.get(DEFAULT);
                } else {
                    BufferedImage defaultIcon = ImageIO.read(ServerUtil.class.getClassLoader().getResourceAsStream("images/java.png"));
                    String b64icon = Base64.encodeBase64String(getPngImage(defaultIcon));
                    iconMap.put(DEFAULT, b64icon);
                    return b64icon;
                }
            } else {
                if (iconMap.containsKey(icon)) {
                    return iconMap.get(icon);
                } else {
                    BufferedImage defaultIcon = ImageIO.read(new File(icon));
                    String b64icon = Base64.encodeBase64String(getPngImage(defaultIcon));
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
            userFile = war.substring(6, war.lastIndexOf("/") + 1) + Constants.DEFAULT_USER_FILE_NAME;
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

    public static JsonSwingSession composeSwingInstanceStatus(SwingInstance si) {
        JsonSwingSession result = new JsonSwingSession();
        result.setId(si.getClientId());
        result.setApplication(si.getApplicationName());
        result.setConnected(si.getSessionId() != null);
        if (!result.getConnected()) {
            result.setDisconnectedSince(si.getDisconnectedSince());
        }
        result.setStartedAt(si.getStartedAt());
        result.setUser(si.getUser());
        result.setState(si.getStats());
        result.setEndedAt(si.getEndedAt());
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
    
    public static boolean validateConfigFile(byte[] content) throws Exception{
        ObjectMapper mapper = new ObjectMapper();
        mapper.readValue(content, WebswingConfiguration.class);
        return true;
    }
    
    public static boolean validateUserFile(byte[] content) throws IOException {
        PropertiesRealm r = new PropertiesRealm();
        String tmpFileName=FileServlet.registerFile(content, UUID.randomUUID().toString(), 10, TimeUnit.SECONDS, "");
        r.setResourcePath(tmpFileName);
        r.init();
        return true;
    }

    public static Serializable composeAdminErrorReply(Exception e) {
        JsonAdminConsoleFrame response = new JsonAdminConsoleFrame();
        response.setErrorMessage(e.getMessage());
        return response;
    }
}
