package org.webswing.model.c2s;

public class JsonEventPaste implements JsonEvent {

    private static final long serialVersionUID = -2857821597134135941L;
    public String clientId;
    public String content;

    @Override
    public String toString() {
        return "JsonEventPaste [clientId=" + clientId + ", content=" + content + "]";
    }

}
