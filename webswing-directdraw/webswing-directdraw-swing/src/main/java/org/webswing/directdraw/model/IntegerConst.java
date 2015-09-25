/**
 * <p>Title: FundCount, LLC</p>
 * <p>Description: FundCount project</p>
 * <p>Copyright: Copyright (c) 2001-2015 FundCount, LLC</p>
 * <p>Company: FundCount, LLC</p>
 */
package org.webswing.directdraw.model;

public class IntegerConst extends DrawConstant<Integer> {

	public IntegerConst(int integer) {
		super(null);
		setId(integer);
	}

	@Override
	public Integer getValue() {
		return getId();
	}

	@Override
	public String getFieldName() {
		return null;
	}

	@Override
	public Object toMessage() {
		throw new UnsupportedOperationException();
	}

	@Override
	public int hashCode() {
		return getId();
	}

	@Override
	public boolean equals(Object o) {
		return o == this ||
			o instanceof IntegerConst && getId() == ((IntegerConst) o).getId();
	}
}
