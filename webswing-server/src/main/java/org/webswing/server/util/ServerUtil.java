package org.webswing.server.util;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.webswing.model.s2c.JsonApplication;
import org.webswing.server.model.SwingApplicationDescriptor;

public class ServerUtil {

    private static final String DEFAULT = "default";
    private static final Logger log = LoggerFactory.getLogger(ServerUtil.class);
    private static final Map<String, String> iconMap = new HashMap<String, String>();


    public static List<JsonApplication> createApplicationJsonInfo(Map<String, SwingApplicationDescriptor> applications) {
        List<JsonApplication> apps = new ArrayList<JsonApplication>();
        if (applications.size() == 0) {
            return null;
        } else {
            for (String name : applications.keySet()) {
                JsonApplication app = new JsonApplication();
                app.name = name;
                SwingApplicationDescriptor descriptor = applications.get(name);
                if (descriptor.getIcon() == null) {
                    app.base64Icon=loadImage(null);
                } else {
                    app.base64Icon=loadImage(descriptor.getHomeDir()+File.separator+descriptor.getIcon());
                }
                apps.add(app);
            }
        }
        return apps;
    }

    private static String loadImage(String icon) {
        try {
            if(icon==null){
                if(iconMap.containsKey(DEFAULT)){
                    return iconMap.get(DEFAULT);
                }else{
                    BufferedImage defaultIcon = ImageIO.read(ServerUtil.class.getClassLoader().getResourceAsStream("images/java.png"));
                    String b64icon=Base64.encodeBase64String(getPngImage(defaultIcon));
                    iconMap.put(DEFAULT, b64icon);
                    return b64icon;
                }
            }else{
                if(iconMap.containsKey(icon)){
                    return iconMap.get(icon);
                }else{
                    BufferedImage defaultIcon = ImageIO.read(new File(icon));
                    String b64icon=Base64.encodeBase64String(getPngImage(defaultIcon));
                    iconMap.put(icon, b64icon);
                    return b64icon;
                }
            }
        } catch (IOException e) {
            log.error("Failed to load image "+icon,e);
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
            log.error("Writing image interupted:" + e.getMessage(),e);
        }
        return null;
    }
}
