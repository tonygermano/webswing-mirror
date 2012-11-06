package sk.viktor.ignored.event.model;

public class JsonEventMouse implements JsonEvent {

    public enum Type {

        mousemove,
        mousedrag,
        mousedown,
        mouseup;
    }

    public Type type;
    public int x;
    public int y;
    public String windowId;

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public String getWindowId() {
        return windowId;
    }

    public Type getType() {
        return type;
    }
}
