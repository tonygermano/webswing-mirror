package org.webswing.model.s2c;

import java.io.Serializable;

public class JsonWindowPartialContent implements Serializable {

    private static final long serialVersionUID = -7577833300925947305L;

    Integer positionX;
    Integer positionY;
    String base64Content;
    
    public Integer getPositionX() {
        return positionX;
    }
    
    public void setPositionX(Integer positionX) {
        this.positionX = positionX;
    }
    
    public Integer getPositionY() {
        return positionY;
    }
    
    public void setPositionY(Integer positionY) {
        this.positionY = positionY;
    }
    
    public String getBase64Content() {
        return base64Content;
    }
    
    public void setBase64Content(String base64Content) {
        this.base64Content = base64Content;
    }

    }
