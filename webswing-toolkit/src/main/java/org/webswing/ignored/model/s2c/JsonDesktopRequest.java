package org.webswing.ignored.model.s2c;

import java.io.Serializable;


public class JsonDesktopRequest implements Serializable{
    
    public enum DesktopAction{
        file, url, print;
    }
    
    private static final long serialVersionUID = 1738087636989561504L;
    public String clazz=JsonDesktopRequest.class.getCanonicalName();
    public DesktopAction action;
    public String url;
    
    public JsonDesktopRequest(DesktopAction action, String url) {
        super();
        this.action = action;
        this.url = url;
    }

    
    
}
