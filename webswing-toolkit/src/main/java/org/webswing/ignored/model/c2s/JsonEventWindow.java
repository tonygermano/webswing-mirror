package org.webswing.ignored.model.c2s;

public class JsonEventWindow implements JsonEvent {

    /**
     * 
     */
    private static final long serialVersionUID = 7003646402287165875L;
    public enum Type {
        close, resize;
    }
    public Type type;
    public String windowId;
    public String clientId;
    public int newWidth;
    public int newHeight;
    
}
