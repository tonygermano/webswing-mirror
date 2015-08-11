package org.webswing.directdraw.model;

import com.google.protobuf.*;
import org.webswing.directdraw.*;

public abstract class DrawConstant {

	public static final NullConst nullConst = new NullConst();

	private DirectDraw context;

	private int id = -1;
	protected Long hash = null;
	protected Object message;

	public DrawConstant(DirectDraw context) {
		this.context = context;
	}

	public Object extractMessage(DirectDraw dd) {
		return message;
	}

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
            hash = 0L;
		}

        @Override
        public int getId()
        {
            // always have zero id
            return 0;
        }

        @Override
		public String getFieldName() {
			return null;
		}
	}

	public static class Integer extends DrawConstant {

		public Integer(int integer) {
			super(null);
			setId(integer);
		}

		@Override
		public String getFieldName() {
			return null;
		}
	}

	public static class HashConst extends DrawConstant {

        private DrawConstant parent;

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
		public void setId(int id) {
            super.setId(id);
			parent.setId(id);
			parent = null;
		}

		@Override
		public String getFieldName() {
			return null;
		}
	}
}
