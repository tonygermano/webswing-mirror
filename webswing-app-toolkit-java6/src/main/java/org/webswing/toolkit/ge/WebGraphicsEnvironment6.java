package org.webswing.toolkit.ge;

import sun.awt.FontConfiguration;

@SuppressWarnings("restriction")
public class WebGraphicsEnvironment6 extends WebGraphicsEnvironment {

	public WebGraphicsEnvironment6() {
		super();
		if (hasFontConfiguration()) {
			fontPath = "";
		}
	}

	@Override
	public FontConfiguration createFontConfiguration(boolean paramBoolean1, boolean paramBoolean2) {
		return new WebFontConfiguration(this);
	}
}
