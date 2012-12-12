package sk.viktor.special;

import sk.viktor.ignored.common.PaintManager;
import sk.viktor.util.Util;


public class RedirectedMethods {

    public static void exit(int status){
        PaintManager.getInstance(Util.resolveClientId(RedirectedMethods.class)).disposeApplication();
        System.out.println("REDIRECTED exit method call");
    }
    
    
    public static void writeObject(Object o){
        System.out.println("DUMMY writeObject(Object o)");
    }
}
