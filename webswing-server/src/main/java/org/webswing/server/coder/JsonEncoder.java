package org.webswing.server.coder;

import java.io.IOException;
import java.io.Serializable;

import org.atmosphere.config.managed.Encoder;
import org.codehaus.jackson.map.ObjectMapper;

public class JsonEncoder implements Encoder<Serializable, String> {

    private final ObjectMapper mapper = new ObjectMapper();

    public String encode(Serializable m) {
            try {
                if(m instanceof String){
                    return (String) m;
                }
                return mapper.writeValueAsString(m);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
    }


}