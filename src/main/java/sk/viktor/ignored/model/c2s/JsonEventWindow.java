package sk.viktor.ignored.model.c2s;

public class JsonEventWindow implements JsonEvent {

    public enum Type {
        close;
    }
    public Type type;
    public String windowId;
    public String clientId;

}
