package org.webswing.model.s2c;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class JsonAppFrame implements Serializable {

    private static final long serialVersionUID = 6019708608380425820L;

    JsonLinkAction linkAction;
    List<JsonWindow> windows;

    public JsonLinkAction getLinkAction() {
        return linkAction;
    }

    public void setLinkAction(JsonLinkAction linkAction) {
        this.linkAction = linkAction;
    }

    public List<JsonWindow> getWindows() {
        return windows;
    }

    public void setWindows(List<JsonWindow> windows) {
        this.windows = windows;
    }

    public JsonWindow getOrCreateWindowById(String guid){
        if(windows!=null){
            for(JsonWindow w:windows){
                if(w.getId().equals(guid)){
                    return w;
                }
            }
        }{
            windows= new ArrayList<JsonWindow>();
        }
        JsonWindow window = new JsonWindow();
        window.setId(guid);
        windows.add(window);
        return window;
    }
    
}
