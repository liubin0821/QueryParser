package com.myhexin.qparser.util;

import java.util.Map;

import com.myhexin.qparser.define.EnumDef.UrlReqType;

public class RequestItem {
    private UrlReqType urlReqType_ = UrlReqType.UNKNOWN;
    private String queryStr_ = null;
    private String postBodyStr_ = null;
    private Map<String,String> postKVMap_ = null;
    
    public RequestItem( UrlReqType urlReqType ){
        urlReqType_ = urlReqType;
    }
    public String getQueryStr(){
        return queryStr_;
    }
    public String getPostBodyStr(){
        return postBodyStr_;
    }
    public Map<String,String> getPostKVMap(){
        return postKVMap_;
    }
    public UrlReqType getUrlReqType(){
        return urlReqType_;
    }
    public void setQueryStr(String queryStr){
        queryStr_ = queryStr;
    }
    public void setPostBodyStr(String postBodyStr){
        postBodyStr_ = postBodyStr;
    }
    public void setPostKVMap(Map<String,String> postKVMap ){
        postKVMap_ = postKVMap;
    }
    public void setUrlReqType(UrlReqType type){
        urlReqType_ = type;
    }
}
