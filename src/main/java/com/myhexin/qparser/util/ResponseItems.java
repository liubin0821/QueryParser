package com.myhexin.qparser.util;

import java.util.ArrayList;
import java.util.List;

public class ResponseItems {
    private List<ResponseItem> rspItems = new ArrayList<ResponseItem>();
    public void add(ResponseItem rspItem){
        rspItems.add(rspItem);
    }
}
