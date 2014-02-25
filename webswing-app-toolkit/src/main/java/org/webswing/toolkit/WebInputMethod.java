package org.webswing.toolkit;

import java.awt.AWTEvent;
import java.awt.im.spi.InputMethod;
import java.awt.im.spi.InputMethodContext;
import java.lang.Character.Subset;
import java.util.Locale;

@SuppressWarnings("restriction")
public class WebInputMethod extends sun.awt.im.InputMethodAdapter implements InputMethod {

    public void activate() {
System.out.println("activate");        
    }

    public void deactivate(boolean isTemporary) {
        System.out.println("deactivate");        
        
    }

    public void dispatchEvent(AWTEvent event) {
       // System.out.println("dispatchEvent");        
        
    }

    public void dispose() {
        System.out.println("dispose");        
        
    }

    public void endComposition() {
        System.out.println("endComposition");        
        
    }

    public Object getControlObject() {
        System.out.println("getControlObject");        

        return null;
    }

    public Locale getLocale() {
        return Locale.getDefault();
    }

    public void hideWindows() {
        System.out.println("hideWindows");        

        // TODO Auto-generated method stub
        
    }

    public boolean isCompositionEnabled() {
        System.out.println("isCompositionEnabled");        

        // TODO Auto-generated method stub
        return false;
    }

    public void removeNotify() {
        System.out.println("removeNotify");        

        // TODO Auto-generated method stub
        
    }

    public void setCharacterSubsets(Subset[] subsets) {
        System.out.println("setCharacterSubsets");        

        // TODO Auto-generated method stub
        
    }

    public void setCompositionEnabled(boolean enable) {
        System.out.println("setCompositionEnabled");        

        // TODO Auto-generated method stub
        
    }

    public void setInputMethodContext(InputMethodContext context) {
        System.out.println("setInputMethodContext");        

        // TODO Auto-generated method stub
        
    }

    public boolean setLocale(Locale locale) {
        System.out.println("setLocale");        

        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void disableInputMethod() {
        System.out.println("disableInputMethod");        

        // TODO Auto-generated method stub
        
    }

    @Override
    public String getNativeInputMethodInfo() {
        return "getNativeInputMethodInfo";
    }



}
