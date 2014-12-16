package org.webswing.directdraw.model;

import java.awt.image.BufferedImage;

import org.webswing.directdraw.DirectDraw;

public class ImageConst extends DrawConstant {

	public ImageConst(DirectDraw context,BufferedImage img) {
    	super(context);
	}

	@Override
	public String getFieldName() {
		return "image";
	}

}
