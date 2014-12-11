package org.webswing.directdraw.model;

import org.webswing.directdraw.DirectDraw;

public class StringConst extends DrawConstant {

    private String string;

    public StringConst(String s) {
        this.string = s;
    }

    public Object getMessage(DirectDraw dd) {
        return string;
    };

    @Override
    public String getFieldName() {
        return "string";
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = prime * ((string == null) ? 0 : string.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (getClass() != obj.getClass())
			return false;
		StringConst other = (StringConst) obj;
		if (string == null) {
			if (other.string != null)
				return false;
		} else if (!string.equals(other.string))
			return false;
		return true;
	}

}
