package org.webswing.ignored.model.s2c;

import java.io.Serializable;
import java.util.Map;

public class JsonPaintRequest implements Serializable{

    private static final long serialVersionUID = 6019708608380425820L;

    public String type = "paint";
    public Map<String,String> b64images;
    public Map<String,JsonWindowInfo> windowInfos;

    public JsonPaintRequest(Map<String,String> b64images, Map<String,JsonWindowInfo> infos) {
        super();
        this.b64images=b64images;
        this.windowInfos=infos;
    }
}
