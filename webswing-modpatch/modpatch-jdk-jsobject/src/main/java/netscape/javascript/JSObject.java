package netscape.javascript;

import java.applet.Applet;
import org.webswing.model.appframe.in.JSObjectMsgIn;
import org.webswing.toolkit.jslink.WebJSObject;

public abstract class JSObject {
    public JSObject() {}

    public abstract Object call(String var1, Object[] var2) throws JSException;

    public abstract Object eval(String var1) throws JSException;

    public abstract Object getMember(String var1) throws JSException;

    public abstract void setMember(String var1, Object var2) throws JSException;

    public abstract void removeMember(String var1) throws JSException;

    public abstract Object getSlot(int var1) throws JSException;

    public abstract void setSlot(int var1, Object var2) throws JSException;

    public static JSObject getWindow(Applet paramApplet) throws JSException {
        return new WebJSObject((JSObjectMsgIn)null);
    }
}