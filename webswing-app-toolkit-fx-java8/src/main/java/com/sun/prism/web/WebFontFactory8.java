package com.sun.prism.web;

import java.io.InputStream;

import com.sun.javafx.font.FontFactory;
import com.sun.javafx.font.PGFont;

public class WebFontFactory8 extends AbstractWebFontFactory {

	public WebFontFactory8(FontFactory fontFactory) {
		super(fontFactory);
	}

	@Override
	public PGFont loadEmbeddedFont(String name, InputStream stream, float size, boolean register) {
		return getFontFactory().loadEmbeddedFont(name, stream, size, register);
	}

	@Override
	public PGFont loadEmbeddedFont(String name, String path, float size, boolean register) {
		return getFontFactory().loadEmbeddedFont(name, path, size, register);
	}
}