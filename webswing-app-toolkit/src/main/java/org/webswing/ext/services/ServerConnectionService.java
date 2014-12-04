package org.webswing.ext.services;

import java.io.Serializable;

public interface ServerConnectionService  {

    void sendJsonObject(Serializable jsonPaintRequest);

    void sendShutdownNotification();

}
