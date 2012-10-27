package sk.viktor.ignored.model;

public class JsonPaintRequest {

    public String type = "paint";
    public String filename;
    public int x;
    public int y;
    public long seq;
    public JsonWindowInfo windowInfo;

    public JsonPaintRequest(long seq, String filename, int x, int y, JsonWindowInfo info) {
        super();
        this.seq = seq;
        this.filename = filename;
        this.x = x;
        this.y = y;
        this.windowInfo=info;
    }
}
