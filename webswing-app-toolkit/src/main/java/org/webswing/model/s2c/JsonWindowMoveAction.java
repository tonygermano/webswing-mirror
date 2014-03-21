package org.webswing.model.s2c;

import java.io.Serializable;


public class JsonWindowMoveAction implements Serializable {
    private static final long serialVersionUID = 8034971938226651461L;
    public int sx;
    public int sy;
    public int dx;
    public int dy;
    public int width;
    public int height;
    public JsonWindowMoveAction(int sx, int sy, int dx, int dy, int width, int height) {
        super();
        this.sx = sx;
        this.sy = sy;
        this.dx = dx;
        this.dy = dy;
        this.width = width;
        this.height = height;
    }
}
