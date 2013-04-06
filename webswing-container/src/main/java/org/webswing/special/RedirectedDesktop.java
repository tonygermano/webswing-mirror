package org.webswing.special;

import java.awt.Desktop.Action;
import java.net.URI;

import org.webswing.ignored.common.PaintManager;
import org.webswing.ignored.model.s2c.JsonDesktopRequest;
import org.webswing.ignored.model.s2c.JsonDesktopRequest.DesktopAction;



public class RedirectedDesktop {

    public static boolean isDesktopSupported(){
        return true;
    }
    
    public static  void browse(URI uri) {
        JsonDesktopRequest request=new JsonDesktopRequest(DesktopAction.url,uri.toString());
        PaintManager.getInstance().getJmsService().sendJsonObject(request);
    }
    
    public static boolean isSupported(Action action) {
        return true;
    }
    
    public static void mail() {
        JsonDesktopRequest request=new JsonDesktopRequest(DesktopAction.url,"mailto:?");
        PaintManager.getInstance().getJmsService().sendJsonObject(request);
    }
    
    public static void mail(URI mailtoURI) {
        JsonDesktopRequest request=new JsonDesktopRequest(DesktopAction.url,mailtoURI.toString());
        PaintManager.getInstance().getJmsService().sendJsonObject(request);
    }
}

