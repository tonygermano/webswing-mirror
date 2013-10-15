package org.webswing.dispatch.update;

import java.awt.Point;
import java.awt.Rectangle;

import org.webswing.model.s2c.JsonAppFrame;
import org.webswing.model.s2c.JsonWindow;
import org.webswing.model.s2c.JsonWindowPartialContent;
import org.webswing.toolkit.WebWindowPeer;
import org.webswing.util.Util;

public class UpdateGraphics extends Update {

    Rectangle dirtyArea;
    String windowId;
    boolean resize;

    public UpdateGraphics(Rectangle dirtyArea, String windowId) {
        super();
        this.dirtyArea = dirtyArea;
        this.windowId = windowId;
    }

    public UpdateGraphics(Rectangle dirtyArea, String windowId, boolean resize) {
        super();
        this.dirtyArea = dirtyArea;
        this.windowId = windowId;
    }

    @Override
    public void updateAppFrame(JsonAppFrame req) {
        WebWindowPeer ww = Util.findWindowPeerById(windowId);
        if (ww != null) {
            JsonWindow window = req.getOrCreateWindowById(windowId);
            Point location = ww.getLocationOnScreen();
            window.setPosX(location.x);
            window.setPosY(location.y);
            window.setWidth(ww.getBounds().width);
            window.setHeight(ww.getBounds().height);
            JsonWindowPartialContent content = window.getContent() == null ? new JsonWindowPartialContent() : window.getContent();
            if (content.getPositionX() == null || resize) {
                content.setPositionX(dirtyArea.x);
            } else {
                Math.min(content.getPositionX(), dirtyArea.x);
            }
            if (content.getPositionY() == null || resize) {
                content.setPositionY(dirtyArea.y);
            } else {
                Math.min(content.getPositionY(), dirtyArea.y);
            }
            if (content.getWidth() == null || resize) {
                content.setWidth(dirtyArea.width);
            } else {
                Math.max(content.getWidth(), dirtyArea.width);
            }
            if (content.getHeight() == null || resize) {
                content.setHeight(dirtyArea.height);
            } else {
                Math.max(content.getHeight(), dirtyArea.height);
            }
            window.setContent(content);
        }
    }

}
