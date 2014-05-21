package org.webswing.server.util;

import org.apache.tools.ant.BuildEvent;
import org.apache.tools.ant.listener.TimestampedLogger;
import org.apache.tools.ant.util.StringUtils;

public class SwingAntTimestampedLogger extends TimestampedLogger {

    @Override
    public void messageLogged(BuildEvent event) {
        int priority = event.getPriority();
        if (priority > this.msgOutputLevel) {
            return;
        }
        StringBuffer message = new StringBuffer();
        message.append(event.getMessage());
        Throwable ex = event.getException();
        if (ex != null) {
            message.append(StringUtils.getStackTrace(ex));
        }

        String msg = message.toString();
        if (priority != 0)
            printMessage(msg, this.out, priority);
        else {
            printMessage(msg, this.err, priority);
        }
        log(msg);
    }
}
