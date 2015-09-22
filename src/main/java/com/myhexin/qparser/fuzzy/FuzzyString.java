package com.myhexin.qparser.fuzzy;

import java.util.Comparator;

class SortFuzzStringByScore implements Comparator<Object>{
    @Override
    public int compare(Object arg0, Object arg1) {
        // TODO Auto-generated method stub
     // TODO Auto-generated method stub
        FuzzyString i1 = (FuzzyString)arg0;
        FuzzyString i2 = (FuzzyString)arg1;
        if( i1.score > i2.score ){
            return 1;
        }else{
            return 0;
        }
    }
}

public class FuzzyString{
    public int score;
    public String strValue;
    public FuzzyString( int aScore, String aStrValue){
        score = aScore;
        strValue = aStrValue;
    }
}
