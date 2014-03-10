package org.webswing.model.c2s;


public class JsonConnectionHandshake implements JsonEvent{

    private static final long serialVersionUID = -3865929274935490301L;
    public String clientId;
    public String sessionId;
    public Integer desktopWidth;
    public Integer desktopHeight;
}
