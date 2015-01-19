package org.webswing.directdraw.model;

import org.webswing.directdraw.DirectDraw;

public class StringConst extends DrawConstant {

	public StringConst(DirectDraw context, String s) {
		super(context);
		this.message = s;
	}

	@Override
	public String getFieldName() {
		return "string";
	}

}
