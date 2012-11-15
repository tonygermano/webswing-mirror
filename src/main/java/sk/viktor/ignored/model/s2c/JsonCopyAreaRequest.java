package sk.viktor.ignored.model.s2c;

public class JsonCopyAreaRequest {

    public String type = "copyArea";
    public int x;
    public int y;
    public int dx;
    public int dy;
    public int width;
    public int height;
    public long seq;
    public String clientId;
    public JsonWindowInfo windowInfo;

    public JsonCopyAreaRequest(String clientId,long seq, int x, int y, int dx, int dy, int width, int height,JsonWindowInfo windowInfo) {
        super();
        this.seq = seq;
        this.clientId=clientId;
        this.x = x;
        this.y = y;
        this.dx = dx;
        this.dy = dy;
        this.width = width;
        this.height = height;
        this.windowInfo=windowInfo;
    }

}
