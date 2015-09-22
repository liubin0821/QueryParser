package com.myhexin.qparser.similarity;

import com.myhexin.qparser.util.Pair;

public class IntIntPair extends Pair<Integer, Integer>{
    private static final long serialVersionUID = 4265926498936151289L;

    public IntIntPair(Integer first, Integer second) {
        super(first, second);
    }
    
    public IntIntPair(){
        super(-1,-1);
    }
    
    public void clean(){
        first = -1;
        second = -1;
    }
    
    public boolean isClean(){
        return first<0 && second<0;
    }
    
    /**
     * 生成可以代表本Pair的字符串型的ID
     * @return
     */
    public String getStrID(){
        StringBuilder sb = new StringBuilder();
        sb.append(Integer.toString(first));
        sb.append("_");
        sb.append(Integer.toString(second));
        return sb.toString();
    }
}
