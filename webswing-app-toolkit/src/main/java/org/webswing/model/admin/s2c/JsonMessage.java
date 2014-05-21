package org.webswing.model.admin.s2c;

import java.io.Serializable;
import java.util.Date;


public class JsonMessage implements Serializable{
    
    public enum Type {
        danger, success
    }

    private static final long serialVersionUID = -1414888884283067502L;

    private Type type;
    private String text;
    private Date time;
    
    public Type getType() {
        return type;
    }
    
    public void setType(Type type) {
        this.type = type;
    }
    
    public String getText() {
        return text;
    }
    
    public void setText(String text) {
        this.text = text;
    }
    
    public Date getTime() {
        return time;
    }
    
    public void setTime(Date time) {
        this.time = time;
    }
    
    
}
