package org.webswing.dispatch.update;

import org.webswing.model.s2c.JsonAppFrame;
import org.webswing.model.s2c.JsonWindow;


public class UpdateBounds extends Update {

    private String id;
    private int x,y,w,h;
    
    public UpdateBounds(String windowId, int x, int y, int w, int h) {
        super();
        this.id=windowId;
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
    }

    @Override
    public void updateAppFrame(JsonAppFrame f) {
        JsonWindow window = f.getOrCreateWindowById(id);
        window.setPosX(x);
        window.setPosY(y);
        window.setWidth(w);
        window.setHeight(h);
    }

}
