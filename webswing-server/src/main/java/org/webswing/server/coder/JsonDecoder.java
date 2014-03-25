package org.webswing.server.coder;

import java.io.IOException;

import org.atmosphere.config.managed.Decoder;
import org.codehaus.jackson.map.ObjectMapper;
import org.webswing.model.c2s.JsonConnectionHandshake;
import org.webswing.model.c2s.JsonEventKeyboard;
import org.webswing.model.c2s.JsonEventMouse;
import org.webswing.model.c2s.JsonEventPaste;

public class JsonDecoder implements Decoder<String, Object> {

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
                        return null;
                    }
                }
            }
        }
    }
}
