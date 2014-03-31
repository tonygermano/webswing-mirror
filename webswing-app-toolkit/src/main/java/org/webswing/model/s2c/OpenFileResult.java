package org.webswing.model.s2c;

import java.io.File;
import java.io.Serializable;

public class OpenFileResult implements Serializable {

    private static final long serialVersionUID = 2490892979442744806L;
    private File f;
    private String clientId;

    public File getF() {
        return f;
    }

    public void setF(File f) {
        this.f = f;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    @Override
    public String toString() {
        return "OpenFileResult [f=" + f + ", clientId=" + clientId + "]";
    }

}
