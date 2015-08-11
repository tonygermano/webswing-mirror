package org.webswing.directdraw.model;

import com.google.protobuf.*;
import org.webswing.directdraw.*;

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
		return message;
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
		if (this == obj) {
            return true;
        }
		if (!(obj instanceof DrawConstant)) {
            return false;
        }
		DrawConstant other = (DrawConstant) obj;
		return getHash() == other.getHash();
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
		protected long getHash() {
			return 0;
		}

		@Override
		public int hashCode() {
			return 0;
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

	public static class HashConst extends DrawConstant {

		long hash;
		DrawConstant parent;

		public HashConst(DrawConstant dc) {
			super(null);
			this.hash = dc.getHash();
			this.parent = dc;
		}

		public HashConst(long hash) {
			super(null);
			this.hash = hash;
		}

		@Override
		protected long getHash() {
			return hash;
		}

		@Override
		public void setAddress(int address) {
			parent.setAddress(address);
			parent = null;
			super.setAddress(address);
		}

		@Override
		public String getFieldName() {
			return null;
		}

	}

}
