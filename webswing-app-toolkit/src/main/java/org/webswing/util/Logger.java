package org.webswing.util;

import java.io.PrintWriter;
import java.io.StringWriter;

public class Logger {

    public static final int TRACE = 0;
    public static final int DEBUG = 1;
    public static final int INFO = 2;
    public static final int WARNING = 3;
    public static final int ERROR = 4;
    public static final int FATAL = 5;

    private static Logger log = new Logger();
    private int thresshold = 3;

    public static void trace(String message, Object... o) {
        log(TRACE, "T " + message, o);
    }

    public static void debug(String message, Object... o) {
        log(DEBUG, "D " + message, o);
    }

    public static void info(String message, Object... o) {
        log(INFO, "I " + message, o);
    }

    public static void warn(String message, Object... o) {
        log(WARNING, "W " + message, o);
    }

    public static void error(String message, Object... o) {
        log(ERROR, "E " + message, o);
    }

    public static void fatal(String message, Object... o) {
        log(FATAL, "F " + message, o);
    }

    public static void log(int level, String message, Object... o) {
        if (log.thresshold <= level) {
            if (o.length > 0) {
                StringBuilder sb = new StringBuilder(message);
                sb.append(" -> ");
                for (Object obj : o) {
                    if (obj == null) {
                        sb.append("null");
                    } else if (obj instanceof Throwable) {
                        StringWriter sw = new StringWriter();
                        PrintWriter pw = new PrintWriter(sw);
                        ((Throwable) obj).printStackTrace(pw);
                        sb.append(sw.toString());
                    } else {
                        sb.append(o.toString());
                    }
                    sb.append(" | ");
                }
                log.log(sb.toString());
            } else {
                log.log(message);
            }
        }
    }

    public static void setThreshold(int t) {
        log.thresshold = t;
    }

    private void log(String s) {
        System.out.println(s);
    }
}
