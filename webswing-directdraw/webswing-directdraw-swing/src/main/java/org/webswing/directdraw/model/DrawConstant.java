package org.webswing.directdraw.model;

import org.webswing.directdraw.DirectDraw;

public abstract class DrawConstant<T> {

	public static final NullConst nullConst = new NullConst();

	private DirectDraw context;

	private int id = -1;

	public DrawConstant(DirectDraw context) {
		this.context = context;
	}

	public abstract T getValue();

	public abstract String getFieldName();

	public abstract Object toMessage();

	@Override
	public abstract int hashCode();

	@Override
	public abstract boolean equals(Object o);

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public DirectDraw getContext() {
		return context;
	}

	public void setContext(DirectDraw context) {
		this.context = context;
	}

	private static final class NullConst extends DrawConstant {

		private NullConst() {
			super(null);
		}

		@Override
		public int getId() {
			// always have zero id
			return 0;
		}

		@Override
		public Object getValue() {
			return null;
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
			return 0;
		}

		@Override
		public boolean equals(Object o) {
			return o == this;
		}
	}
}
