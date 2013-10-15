package org.webswing.dispatch.update;

import org.webswing.model.s2c.JsonAppFrame;


public abstract class Update {
    
    /**
     * Updates the information in json message object. HAS TO BE FAST BECAUSE IT IS SYNCHRONIZED
     * @param req
     */
    public abstract void updateAppFrame(JsonAppFrame req);

}
