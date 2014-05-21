package org.webswing.common;

import java.io.Serializable;

public interface ServerConnectionIfc  {

    void sendJsonObject(Serializable jsonPaintRequest);

    void sendShutdownNotification();

}
