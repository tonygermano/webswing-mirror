package org.webswing.special;

import java.awt.Desktop.Action;
import java.net.URI;

import org.webswing.model.s2c.JsonLinkAction;
import org.webswing.model.s2c.JsonLinkAction.JsonLinkActionType;
import org.webswing.util.Util;

public class RedirectedDesktop {

    public static boolean isDesktopSupported() {
        return true;
    }

    public static void browse(URI uri) {
        JsonLinkAction request = new JsonLinkAction(JsonLinkActionType.url, uri.toString());
        Util.getWebToolkit().getPaintDispatcher().sendJsonObject(request);
    }

    public static boolean isSupported(Action action) {
        return true;
    }

    public static void mail() {
        JsonLinkAction request = new JsonLinkAction(JsonLinkActionType.url, "mailto:?");
        Util.getWebToolkit().getPaintDispatcher().sendJsonObject(request);
    }

    public static void mail(URI mailtoURI) {
        JsonLinkAction request = new JsonLinkAction(JsonLinkActionType.url, mailtoURI.toString());
        Util.getWebToolkit().getPaintDispatcher().sendJsonObject(request);
    }
}
