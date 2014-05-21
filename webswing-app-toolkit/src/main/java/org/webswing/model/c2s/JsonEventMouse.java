package org.webswing.model.c2s;

public class JsonEventMouse implements JsonEvent {

    /**
     * 
     */
    private static final long serialVersionUID = 2726469537317513925L;
    public enum Type {
        mousemove,
        mousedown,
        mouseup,
        mousewheel,
        dblclick;
    }

    public String clientId;
    public int x;
    public int y;
    public Type type;
    public int wheelDelta;
    public int button;
    public boolean ctrl;
    public boolean alt;
    public boolean shift;
    public boolean meta;
    
    
    @Override
    public String toString() {
        return "JsonEventMouse [clientId=" + clientId + ", x=" + x + ", y=" + y + ", type=" + type + ", wheelDelta=" + wheelDelta + ", button=" + button + ", ctrl=" + ctrl + ", alt=" + alt + ", shift=" + shift + ", meta=" + meta + "]";
    }
    
    
}
