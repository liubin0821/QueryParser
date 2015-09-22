package com.myhexin.qparser.util;

import java.util.ArrayList;
import java.util.List;

import com.myhexin.qparser.define.EnumDef.UrlReqType;

public class ResponseItem {
    private RequestItem requestItem_ = new RequestItem(UrlReqType.UNKNOWN);
    private boolean rspOK_ = false;
    private List<String> rspRlts_ = new ArrayList<String>();
    private List<String> rspLogs_ = new ArrayList<String>();
    private long timeUsed_ = 0;//生成本次请求的耗时，按ms来记
    
    public void setRequestItem(RequestItem reqItem){
        requestItem_ = reqItem;
    }
    public void setTimeUsed( long timeUsed ){
        timeUsed_ = timeUsed;
    }
    public void setRspOK(boolean rspOK){
        rspOK_ = rspOK;
    }
    public void setRspRlts( List<String> rspRlts ){
        rspRlts_ = rspRlts;
    }
    public void setRspLogs( List<String> rspLogs ){
        rspLogs_ = rspLogs;
    }
    public long getTimeUsed(){
        return timeUsed_;
    }
    public boolean getRspOK(){
        return rspOK_;
    }
    public List<String> getRspRlts(){
        return rspRlts_;
    }
    public List<String> getRspLogs(){
        return rspLogs_;
    }
    public RequestItem getRequestItem(){
        return requestItem_;
    }
    
    /**
     * 获取rsp rlt的字符串形式，通过传入的连接符连接
     * @param spl
     * @return
     */
    public String getRspRltsStr(String spl){
        return Util.linkStringArrayListBySpl(rspRlts_, spl);
    }
    /**
     * 获取rsp log的字符串形式，通过传入的连接符连接
     * @param spl
     * @return
     */
    public String getRspLogsStr(String spl){
        return Util.linkStringArrayListBySpl(rspLogs_, spl);
    }
}
