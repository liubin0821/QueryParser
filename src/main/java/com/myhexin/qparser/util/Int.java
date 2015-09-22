package com.myhexin.qparser.util;

import java.io.Serializable;

/**
 * 对int的mutable包装 
 */
public class Int extends Number implements Comparable<Int>, Serializable{
    private static final long serialVersionUID = 6432421990051740179L;
    private int val_;
    
    public Int(int value) {
        this.val_ = value;
    }

    @Override
    public double doubleValue() {
        return val_;
    }

    @Override
    public float floatValue() {
        return val_;
    }

    @Override
    public int intValue() {
        return val_;
    }

    @Override
    public long longValue() {
        return val_;
    }

    public int compareTo(Integer otherInt) {
        int other = otherInt.intValue();
        return val_ < other ? -1 : (val_ == other ? 0 : 1);
    }

    public int compareTo(Int otherInt) {
        int other = otherInt.intValue();
        return val_ < other ? -1 : (val_ == other ? 0 : 1);
    }
    
    public boolean equals(Object obj) {
        if (obj instanceof Int) {
            return val_ == ((Int) obj).intValue();
        } else if(obj instanceof Integer) {
            return val_ == ((Integer)obj).intValue();
        }
        return false;
    }
    
    public int hashCode() { return val_; }
    
    public void add(int n) { val_ += n; }
    
    public void sub(int n) { val_ -= n; }
    
    public void mul(int n) { val_ *= n; }
    
    public void div(int n) { val_ /= n; }
    
    public void increase() { ++val_; }
    
    public void decrease() { --val_; }
    
    public void set(int n) { val_ = n; }
    
    public int get() { return val_; }
    
    public String toString() { return String.valueOf(val_); }
    
    public Integer toInteger() { return Integer.valueOf(val_); }
}
