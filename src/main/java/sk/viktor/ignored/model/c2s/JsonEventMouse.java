package sk.viktor.ignored.model.c2s;

public class JsonEventMouse implements JsonEvent {

    public enum Type {
        mousemove,
        mousedown,
        mouseup,
        mousewheel,
        dblclick;
    }

    public Type type;
    public String clientId;
    public int x;
    public int y;
    public int button;
    public int wheelDelta;
    public String windowId;
    public boolean ctrl;
    public boolean alt;
    public boolean shift;
    public boolean meta;
}
