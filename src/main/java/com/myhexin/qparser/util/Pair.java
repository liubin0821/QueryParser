package com.myhexin.qparser.util;

import java.io.Serializable;

public class Pair <T_FIRST, T_SECOND> implements Serializable{
    private static final long serialVersionUID = 2863269675697969128L;

    public T_FIRST first = null;
    public T_SECOND second = null;
    
    public Pair(T_FIRST first, T_SECOND second) {
        this.first = first; this.second = second;
    }
    
    @Override
    public boolean equals(Object other) {
        if(this == other) return true;
        if(!(other instanceof Pair<?, ?>)) return false;
        Pair<?, ?> oth = (Pair<?, ?>)(other);
        return (first == null ? oth.first == null :
                first.equals(oth.first))
            && (second == null ? oth.second == null :
                second.equals(oth.second));
    }
    
    public String toString() {
        return String.format("from=%s, to=%s", first!=null?first.toString():"null", second!=null?second.toString():"null");
    }
}
