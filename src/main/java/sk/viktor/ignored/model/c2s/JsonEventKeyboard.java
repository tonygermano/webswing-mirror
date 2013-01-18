package sk.viktor.ignored.model.c2s;

public class JsonEventKeyboard implements JsonEvent {

    public enum Type {
        keypress,
        keydown,
        keyup;
    }

    public String windowId;
    public String clientId;
    public boolean alt;
    public boolean shift;
    public boolean ctrl;
    public boolean altgr;
    public boolean meta;
    public int keycode;
    public int character;
    public Type type;

}
