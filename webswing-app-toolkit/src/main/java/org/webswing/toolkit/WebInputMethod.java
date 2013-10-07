package org.webswing.toolkit;

import java.awt.AWTEvent;
import java.awt.im.spi.InputMethod;
import java.awt.im.spi.InputMethodContext;
import java.lang.Character.Subset;
import java.util.Locale;

@SuppressWarnings("restriction")
public class WebInputMethod extends sun.awt.im.InputMethodAdapter implements InputMethod {

    public void activate() {
        // TODO Auto-generated method stub
        
    }

    public void deactivate(boolean isTemporary) {
        // TODO Auto-generated method stub
        
    }

    public void dispatchEvent(AWTEvent event) {
        // TODO Auto-generated method stub
        
    }

    public void dispose() {
        // TODO Auto-generated method stub
        
    }

    public void endComposition() {
        // TODO Auto-generated method stub
        
    }

    public Object getControlObject() {
        return null;
    }

    public Locale getLocale() {
        return Locale.getDefault();
    }

    public void hideWindows() {
        // TODO Auto-generated method stub
        
    }

    public boolean isCompositionEnabled() {
        // TODO Auto-generated method stub
        return false;
    }

    public void removeNotify() {
        // TODO Auto-generated method stub
        
    }

    public void setCharacterSubsets(Subset[] subsets) {
        // TODO Auto-generated method stub
        
    }

    public void setCompositionEnabled(boolean enable) {
        // TODO Auto-generated method stub
        
    }

    public void setInputMethodContext(InputMethodContext context) {
        // TODO Auto-generated method stub
        
    }

    public boolean setLocale(Locale locale) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void disableInputMethod() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public String getNativeInputMethodInfo() {
        return "getNativeInputMethodInfo";
    }



}
