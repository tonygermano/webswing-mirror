package org.webswing.dispatch.update;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import org.webswing.model.s2c.JsonAppFrame;
import org.webswing.model.s2c.JsonWindowPartialContent;
import org.webswing.model.s2c.JsonWindow;
import org.webswing.toolkit.WebWindowPeer;
import org.webswing.util.Util;


public class UpdateGraphics extends Update {

    BufferedImage img; 
    String base64img;
    Rectangle dirtyArea;
    String windowId;
    
    public UpdateGraphics(BufferedImage deepCopy, Rectangle dirtyArea, String windowId) {
        super();
        this.img = deepCopy;
        this.dirtyArea = dirtyArea;
        this.windowId = windowId;
    }


    @Override
    public void prepareUpdate() {
        base64img=Util.getWebToolkit().getPaintDispatcher().getImageService().encodeImage(img);
        this.img=null;
    }
    
    @Override
    public void updateAppFrame(JsonAppFrame req) {
        JsonWindow window = req.getOrCreateWindowById(windowId);
        JsonWindowPartialContent content=new JsonWindowPartialContent();
        WebWindowPeer ww = Util.findWindowPeerById(windowId);
        window.setPosX(ww.getBounds().x);
        window.setPosY(ww.getBounds().y);
        window.setWidth(ww.getBounds().width);
        window.setHeight(ww.getBounds().height);
        content.setBase64Content(base64img);
        content.setPositionX(dirtyArea.x);
        content.setPositionY(dirtyArea.y);
        window.setContent(content);
    }

}
