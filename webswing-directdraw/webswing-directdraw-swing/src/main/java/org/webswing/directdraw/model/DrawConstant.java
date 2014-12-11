package org.webswing.directdraw.model;

import org.webswing.directdraw.DirectDraw;

public abstract class DrawConstant {

    public static final NullConst nullConst = new NullConst();

    private int address = -1;
    protected Object message;

    public Object getMessage(DirectDraw dd) {
        return message;
    }

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
        result = prime * result + message.hashCode();
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
        DrawConstant other = (DrawConstant) obj;
        if (!message.equals(other.message))
            return false;
        return true;
    }

    abstract public String getFieldName();

    private static class NullConst extends DrawConstant {

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
            setAddress(integer);
        }


        @Override
        public String getFieldName() {
            return null;
        }

    }
}
