package org.webswing.toolkit.ge;

import sun.awt.FontConfiguration;

@SuppressWarnings("restriction")
public class WebGraphicsEnvironment11 extends WebGraphicsEnvironment {
    
    public WebGraphicsEnvironment11() {
        if (hasFontConfiguration()) {
            System.setProperty("sun.font.fontmanager", org.webswing.toolkit.ge.WebFontManager.class.getName());
        }
        
    }
    
    public FontConfiguration createFontConfiguration(boolean b1, boolean b2) {
        return null; //not used in java8 (see WebFontManager)
    }
}
