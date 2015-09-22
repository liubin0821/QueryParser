package com.myhexin.qparser.fuzzy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Comparator;
import java.util.Iterator;

class SortByScore implements Comparator<Object>{
    @Override
    public int compare(Object arg0, Object arg1) {
        // TODO Auto-generated method stub
        FuzzyItem i1 = (FuzzyItem)arg0;
        FuzzyItem i2 = (FuzzyItem)arg1;
        if( i1.score < i2.score ){
            return 1;
        }else{
            return 0;
        }
    }
}

public class FuzzyItem{
    public static SortFuzzStringByScore sortByFuzzStringScore = new SortFuzzStringByScore();
    
    public enum TYPE_CLASS  { 
        VALUE, INDEX; 
    }
    
    public FuzzyItem(){
        score = 0.0;
        type = TYPE_CLASS.VALUE;
        content = "";
        userQuery = "";
        innerFuzzyResult = new HashSet<String>();
        fuzzyResult = new ArrayList<String>();
        stockSet = new HashSet<String>();
    }
    
    public void addToInnerFuzzyResult( String label ){
        innerFuzzyResult.add(label);
    }
    
    public void addToFuzzyResult( String label ){
        fuzzyResult.add(label);
    }
    
    public void calAndSort(){
        Iterator<String> fuzzIter = innerFuzzyResult.iterator();
        String nowFuzzStr = null;
        ArrayList<FuzzyString> tempFuzzStr = new ArrayList<FuzzyString>();
        while( fuzzIter.hasNext() ){
            nowFuzzStr = fuzzIter.next();
            int tempScore = FuzzyUtil.ld(userQuery, nowFuzzStr);
            FuzzyString temp = new FuzzyString(tempScore , nowFuzzStr );
            tempFuzzStr.add(temp);
        }
        Collections.sort(tempFuzzStr, sortByFuzzStringScore);
        for( int i = 0; i < tempFuzzStr.size(); i++ ){
            fuzzyResult.add(tempFuzzStr.get(i).strValue);
        }
    }
    
    public void clearSet(){
        fuzzyResult.clear();
        stockSet.clear();
    }
    //score is used to estimate the possibility of this item 
    public double score; 
    //user query
    public String userQuery;
    //type is used to identify the type of this onto-object
    //it can be "value" or "index" 
    public TYPE_CLASS type;
    //value store the name of this item, like "has_stockconcept" or "市盈率" 
    public String content;
    //fuzzyResult
    private HashSet<String> innerFuzzyResult;
    public ArrayList<String> fuzzyResult;
    //stockSet
    public HashSet<String> stockSet;
    //to string
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append( "score:");
        sb.append( Double.toString(score) );
        sb.append( " type:[" );
        sb.append( type );
        sb.append( "] userQuery:[" );
        sb.append( userQuery );
        sb.append( "] content:[" );
        sb.append( content );
        sb.append( "] fuzzy result list:[");
        Iterator<String> resultsetIterator = fuzzyResult.iterator();
        while( resultsetIterator.hasNext() ){
            sb.append(" ");
            sb.append(resultsetIterator.next());
        }
        sb.append( "] stock list:[");
        Iterator<String> stocksetIterator = stockSet.iterator();
        while( stocksetIterator.hasNext() ){
            sb.append(" ");
            sb.append(stocksetIterator.next());
        }
        sb.append("]");
        return sb.toString();
    }
}
