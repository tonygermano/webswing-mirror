package sk.viktor.ignored.model.c2s;

public class JsonEventMouse  {

    public enum Type {
        mousemove,
        mousedown,
        mouseup;
    }

    public Type type;
    public String clientId;
    public int x;
    public int y;
    public int button;
    public String windowId;
}
