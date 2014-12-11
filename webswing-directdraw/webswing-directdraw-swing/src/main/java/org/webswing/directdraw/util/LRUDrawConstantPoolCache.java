package org.webswing.directdraw.util;

import java.util.HashMap;

import org.webswing.directdraw.model.DrawConstant;

public class LRUDrawConstantPoolCache {

    private HashMap<DrawConstant, DoubleLinkedListNode> map = new HashMap<DrawConstant, DoubleLinkedListNode>();
    private DoubleLinkedListNode head;
    private DoubleLinkedListNode end;
    private int capacity;
    private int len;

    public LRUDrawConstantPoolCache(int capacity) {
        this.capacity = capacity;
        len = 0;
    }

    public synchronized boolean contains(DrawConstant constant) {
        return map.containsKey(constant);
    }

    private int removeNode(DoubleLinkedListNode node) {
        DoubleLinkedListNode cur = node;
        DoubleLinkedListNode pre = cur.pre;
        DoubleLinkedListNode post = cur.next;

        if (pre != null) {
            pre.next = post;
        } else {
            head = post;
        }

        if (post != null) {
            post.pre = pre;
        } else {
            end = pre;
        }
        return cur.getAddress();
    }

    private void setHead(DoubleLinkedListNode node) {
        node.next = head;
        node.pre = null;
        if (head != null) {
            head.pre = node;
        }

        head = node;
        if (end == null) {
            end = node;
        }
    }

    public DrawConstant set(DrawConstant constant) {
        if (map.containsKey(constant)) {
            DoubleLinkedListNode oldNode = map.get(constant);
            removeNode(oldNode);
            setHead(oldNode);
            return oldNode.getVal();
        } else {
            DoubleLinkedListNode newNode = new DoubleLinkedListNode(constant);
            if (len < capacity) {
                setHead(newNode);
                newNode.setAddress(len);
                map.put(newNode.getVal(), newNode);
                len++;
                return newNode.getVal();
            } else {
                int evictedAddress = end.getAddress();
                map.remove(end.getVal());
                end = end.pre;
                if (end != null) {
                    end.next = null;
                }
                setHead(newNode);
                newNode.setAddress(evictedAddress);
                map.put(newNode.getVal(), newNode);
                return newNode.getVal();
            }
        }
    }

    private class DoubleLinkedListNode {

        DrawConstant val;
        DoubleLinkedListNode pre;
        DoubleLinkedListNode next;

        public DoubleLinkedListNode(DrawConstant value) {
            val = value;
        }

        public int getAddress() {
            return val.getAddress();
        }

        public void setAddress(int address) {
            val.setAddress(address);
        }

        public DrawConstant getVal() {
            return val;
        }

    }
}
