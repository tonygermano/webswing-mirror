package sk.viktor.ignored.model.s2c;


public class JsonWindowRequest {
    public String clazz=JsonWindowRequest.class.getCanonicalName();
    public String windowId;
    
    
    public JsonWindowRequest(String windowId) {
        this.windowId=windowId; 
    }
}
