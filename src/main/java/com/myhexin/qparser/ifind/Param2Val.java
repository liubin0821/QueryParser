package com.myhexin.qparser.ifind;

import com.myhexin.qparser.util.Pair;

public class Param2Val extends Pair<IFindParam, String> implements Cloneable{
    private static final long serialVersionUID = -5078591419444417251L;

    public Param2Val(IFindParam first, String second) {
        super(first, second);
    }
    
    public Param2Val clone() {
        Param2Val rtn;
        try {
            rtn = (Param2Val) super.clone();
            rtn.first = first; //IFindParam is immutable
            return rtn;
        } catch (CloneNotSupportedException e) {
            throw new InternalError();
        }
    }
}
