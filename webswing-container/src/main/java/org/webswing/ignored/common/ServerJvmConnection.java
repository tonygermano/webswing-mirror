package org.webswing.ignored.common;

import java.awt.Window;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.util.Map;

public interface ServerJvmConnection  {

    boolean isReadyToReceive();

    void sendJsonObject(Serializable jsonPaintRequest);

    void sendShutdownNotification();

    Map<String, String> createEncodedPaintMap(Map<String, Window> copy);

    byte[] getPngImage(BufferedImage subimage);


}
