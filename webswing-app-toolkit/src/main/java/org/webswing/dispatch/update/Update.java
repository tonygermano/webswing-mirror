package org.webswing.dispatch.update;

import org.webswing.model.s2c.JsonAppFrame;


public abstract class Update {
    
    public void prepareUpdate(){
        
    }
    
    public abstract void updateAppFrame(JsonAppFrame req);

}
