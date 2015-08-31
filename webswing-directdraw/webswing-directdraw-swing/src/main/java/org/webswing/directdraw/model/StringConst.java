package org.webswing.directdraw.model;

import org.webswing.directdraw.*;

public class StringConst extends DrawConstant {

    private String string;
    
	public StringConst(DirectDraw context, String string) {
		super(context);
        this.string = string;
	}

	@Override
	public String getFieldName() {
		return "string";
	}

    @Override
    public Object toMessage() {
        return string;
    }

    @Override
    public int hashCode() {
        return string.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        return o == this ||
            o instanceof StringConst && string.equals(((StringConst) o).string);
    }

    public String getString() {
	    return string;
	}
}
