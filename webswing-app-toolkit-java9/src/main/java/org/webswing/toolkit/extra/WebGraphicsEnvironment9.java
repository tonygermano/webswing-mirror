package org.webswing.toolkit.ge;

import sun.awt.FontConfiguration;

@SuppressWarnings("restriction")
public class WebGraphicsEnvironment9 extends WebGraphicsEnvironment {
    
    public WebGraphicsEnvironment9() {
        if (hasFontConfiguration()) {
            System.setProperty("sun.font.fontmanager", org.webswing.toolkit.ge.WebFontManager.class.getName());
        }
        
    }
    
    public FontConfiguration createFontConfiguration(boolean b1, boolean b2) {
        return null; //not used in java8 (see WebFontManager)
    }
}
