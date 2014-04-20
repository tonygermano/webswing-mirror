package org.webswing.model.s2c;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class JsonAppFrame implements Serializable {

    private static final long serialVersionUID = 6019708608380425820L;

    private String type="app";
    public List<JsonApplication> applications;
    public JsonLinkAction linkAction;
    public JsonWindowMoveAction moveAction;
    public JsonCursorChange cursorChange;
    public String user;
    public JsonCopyEvent copyEvent;
    public List<JsonWindow> windows;

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
        }else{
            windows= new ArrayList<JsonWindow>();
        }
        JsonWindow window = new JsonWindow();
        window.setId(guid);
        windows.add(window);
        return window;
    }

    @Override
    public String toString() {
        return "JsonAppFrame [applications=" + applications + ", linkAction=" + linkAction + ", moveAction=" + moveAction + ", cursorChange=" + cursorChange + ", copyEvent=" + copyEvent + ", windows=" + windows + "]";
    }

    
    public String getType() {
        return type;
    }
    
    
    
}
