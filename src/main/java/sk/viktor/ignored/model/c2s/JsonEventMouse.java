package sk.viktor.ignored.model.c2s;

public class JsonEventMouse implements JsonEvent {

    public enum Type {
        mousemove,
        mousedown,
        mouseup,
        mousewheel;
    }

    public Type type;
    public String clientId;
    public int x;
    public int y;
    public int button;
    public int wheelDelta;
    public String windowId;
}
