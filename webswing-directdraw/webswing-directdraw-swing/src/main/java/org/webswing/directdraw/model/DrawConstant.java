package org.webswing.directdraw.model;

import org.webswing.directdraw.DirectDraw;

import com.google.protobuf.Message;

public abstract class DrawConstant {

	public static final NullConst nullConst = new NullConst();

	private DirectDraw context;

	private int address = -1;
	private Long hash = null;
	protected Object message;

	public DrawConstant(DirectDraw context) {
		this.context = context;
	}

	public Object extractMessage(DirectDraw dd) {
		Object result = message;
		message = null;
		return result;
	}

	public int getAddress() {
		return address;
	}

	public void setAddress(int address) {
		this.address = address;
	}

	public DirectDraw getContext() {
		return context;
	}

	public void setContext(DirectDraw context) {
		this.context = context;
	}

	protected long getHash() {
		if (hash == null && message != null) {
			if (message instanceof Message) {
				hash = context.getServices().getSignature(((Message) message).toByteArray());
			} else if (message instanceof String) {
				hash = context.getServices().getSignature(((String) message).getBytes());
			}
		}
		return hash;
	}

	@Override
	public int hashCode() {
		return (int) getHash();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DrawConstant other = (DrawConstant) obj;
		if (getHash() != other.getHash())
			return false;
		return true;
	}

	abstract public String getFieldName();

	private static class NullConst extends DrawConstant {

		public NullConst() {
			super(null);
		}

		@Override
		public String getFieldName() {
			return null;
		}

		@Override
		public int hashCode() {
			return this.getClass().hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			return obj == this;
		}
	}

	public static class Integer extends DrawConstant {

		public Integer(int integer) {
			super(null);
			setAddress(integer);
		}

		@Override
		public String getFieldName() {
			return null;
		}

	}
}
