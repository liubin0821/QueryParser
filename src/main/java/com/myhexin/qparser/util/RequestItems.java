package com.myhexin.qparser.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class RequestItems {
    private List<RequestItem> requestItems = new ArrayList<RequestItem>();
    public void add( RequestItem reqItem ){
        requestItems.add(reqItem);
    }
    
    public int size(){
        return requestItems.size();
    }
    
    public Iterator<RequestItem> getIterator(){
        return requestItems.iterator();
    }
}
