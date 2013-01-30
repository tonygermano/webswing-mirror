package sk.viktor.ignored.model.s2c;

import java.io.Serializable;


public class JsonWindowRequest implements Serializable{
    private static final long serialVersionUID = -130511664459173691L;
    public String clazz=JsonWindowRequest.class.getCanonicalName();
    public String windowId;
    
    
    public JsonWindowRequest(String windowId) {
        this.windowId=windowId; 
    }
}
