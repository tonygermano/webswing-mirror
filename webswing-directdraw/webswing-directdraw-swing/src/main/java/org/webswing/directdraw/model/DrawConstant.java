package org.webswing.directdraw.model;

import org.webswing.directdraw.DirectDraw;

import com.google.protobuf.AbstractMessage.Builder;

public abstract class DrawConstant<T extends Builder<?>> {

    public static final NullConst nullConst = new NullConst();

    private int address = -1;

    public Object toMessage(DirectDraw dd) {
        return getProtoBuilder().build();
    }

    protected abstract T getProtoBuilder();

    public int getAddress() {
        return address;
    }

    public void setAddress(int address) {
        this.address = address;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + getProtoBuilder().build().hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        DrawConstant<?> other = (DrawConstant<?>) obj;
        if (!getProtoBuilder().build().equals(other.getProtoBuilder().build()))
            return false;
        return true;
    }

    abstract public String getFieldName();

    @SuppressWarnings({ "rawtypes" })
    private static class NullConst extends DrawConstant {

        @Override
        protected Builder getProtoBuilder() {
            return null;
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

    @SuppressWarnings({ "rawtypes" })
    public static class Integer extends DrawConstant {

        public Integer(int integer) {
            setAddress(integer);
        }

        @Override
        protected Builder getProtoBuilder() {
            return null;
        }

        @Override
        public String getFieldName() {
            return null;
        }

    }
}
