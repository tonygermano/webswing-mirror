package org.webswing.model.c2s;

public class JsonEventKeyboard implements JsonEvent {

    /**
     * 
     */
    private static final long serialVersionUID = -642542361370871927L;
    public enum Type {
        keypress,
        keydown,
        keyup;
    }

    public String clientId;
    public Type type;
    public int character;
    public int keycode;
    public boolean alt;
    public boolean ctrl;
    public boolean shift;
    public boolean meta;
    public boolean altgr;

}
