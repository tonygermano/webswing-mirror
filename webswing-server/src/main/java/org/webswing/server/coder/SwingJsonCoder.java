package org.webswing.server.coder;

import java.io.IOException;
import java.io.Serializable;

import org.atmosphere.config.managed.Decoder;
import org.atmosphere.config.managed.Encoder;
import org.codehaus.jackson.map.ObjectMapper;
import org.webswing.model.admin.c2s.JsonApplyConfiguration;
import org.webswing.model.c2s.JsonConnectionHandshake;
import org.webswing.model.c2s.JsonEventKeyboard;
import org.webswing.model.c2s.JsonEventMouse;
import org.webswing.model.c2s.JsonEventPaste;

public class SwingJsonCoder implements Decoder<String, Object>,Encoder<Serializable, String> {

    private final ObjectMapper mapper = new ObjectMapper();

    public Object decode(String s) {
        try {
            return mapper.readValue(s, JsonEventMouse.class);
        } catch (IOException e) {
            try {
                return mapper.readValue(s, JsonEventKeyboard.class);
            } catch (IOException e1) {
                try {
                    return mapper.readValue(s, JsonConnectionHandshake.class);
                } catch (IOException e2) {
                    try {
                        return mapper.readValue(s, JsonEventPaste.class);
                    } catch (IOException e3) {
                        try {
                            return mapper.readValue(s, JsonApplyConfiguration.class);
                        } catch (IOException e4) {
                            return null;
                        }
                    }
                }
            }
        }
    }

    public String encode(Serializable m) {
        try {
            if (m instanceof String) {
                return (String) m;
            }
            return mapper.writeValueAsString(m);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
