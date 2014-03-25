package org.webswing.model.s2c;

import java.io.Serializable;

public class JsonCopyEvent implements Serializable {

    private static final long serialVersionUID = -472686460967596635L;
    public String content;

    public JsonCopyEvent(String content) {
        super();
        this.content = content;
    }

}
