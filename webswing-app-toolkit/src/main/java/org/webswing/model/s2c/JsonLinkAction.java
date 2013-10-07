package org.webswing.model.s2c;

import java.io.Serializable;


public class JsonLinkAction implements Serializable{
    
    public enum JsonLinkActionType{
        file, url, print;
    }
    
    private static final long serialVersionUID = 1738087636989561504L;
    public JsonLinkActionType action;
    public String url;
    
    public JsonLinkAction(JsonLinkActionType action, String url) {
        super();
        this.action = action;
        this.url = url;
    }

    
    
}
