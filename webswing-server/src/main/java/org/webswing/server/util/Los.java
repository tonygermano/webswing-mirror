package org.webswing.server.util;

import java.io.OutputStream;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * LoggerOutputStream
 *
 */
public class Los extends OutputStream {

    private Logger logger;
    private String clientId;
    private StringBuffer mem;

    public Los(String clientId) {
        logger = Logger.getLogger("client." + clientId);
        this.clientId = "[" + clientId + "] ";
        mem = new StringBuffer("");
    }

    public void write(int i) {
        char b = (char) (i & 0xff);
        if (String.valueOf(b).equals("\n")) {
            flush();
        } else {
            mem.append(b);
        }

    }

    public void flush() {
        String msg=mem.toString().trim();
        while(msg.endsWith("\n")){
            msg=msg.substring(0, msg.length()-1);
        }
        if (msg.length() != 0) {
            logger.log(Level.INFO, clientId + msg);
            mem = new StringBuffer("");
        }
    }
}
