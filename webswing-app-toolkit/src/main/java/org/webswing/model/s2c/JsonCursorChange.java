package org.webswing.model.s2c;

import java.io.Serializable;


public class JsonCursorChange implements Serializable{
    private static final long serialVersionUID = -4672423078899844664L;
    public static final String DEFAULT_CURSOR = "default";
    public static final String HAND_CURSOR = "pointer";
    public static final String CROSSHAIR_CURSOR = "crosshair";
    public static final String MOVE_CURSOR = "move";
    public static final String TEXT_CURSOR = "text";
    public static final String WAIT_CURSOR = "progress";
    public static final String EW_RESIZE_CURSOR = "e-resize";
    public static final String NS_RESIZE_CURSOR = "n-resize";
    public static final String SLASH_RESIZE_CURSOR = "ne-resize";
    public static final String BACKSLASH_RESIZE_CURSOR = "se-resize";
    public static final String NOT_ALLOWED_CURSOR = "not-allowed";
    public String cursor;

    public JsonCursorChange(String cursor) {
        super();
        this.cursor = cursor;
    }

    @Override
    public String toString() {
        return "JsonCursorChange [cursor=" + cursor + "]";
    }
    
    
}
