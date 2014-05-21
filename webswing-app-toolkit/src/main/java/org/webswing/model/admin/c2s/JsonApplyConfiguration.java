package org.webswing.model.admin.c2s;

import java.io.Serializable;

public class JsonApplyConfiguration implements Serializable {

    
    public enum Type {
        user,config;
    }

    private static final long serialVersionUID = -6657140101659804419L;

    private Type type;
    private byte[] configContent;

    public Type getType() {
        return type;
    }
    
    public void setType(Type type) {
        this.type = type;
    }

    
    public byte[] getConfigContent() {
        return configContent;
    }

    
    public void setConfigContent(byte[] configContent) {
        this.configContent = configContent;
    }


}
