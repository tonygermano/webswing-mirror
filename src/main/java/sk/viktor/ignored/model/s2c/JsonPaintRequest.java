package sk.viktor.ignored.model.s2c;

public class JsonPaintRequest {

    public String type = "paint";
    public String filename;
    public int x;
    public int y;
    public long seq;
    public String clientId;
    public JsonWindowInfo windowInfo;

    public JsonPaintRequest(String clientId,long seq, String filename, int x, int y, JsonWindowInfo info) {
        super();
        this.clientId=clientId;
        this.seq = seq;
        this.filename = filename;
        this.x = x;
        this.y = y;
        this.windowInfo=info;
    }
}
