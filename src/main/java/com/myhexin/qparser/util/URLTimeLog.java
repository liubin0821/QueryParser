package com.myhexin.qparser.util;

import com.myhexin.qparser.define.EnumDef.UrlReqType;

public class URLTimeLog {
    private UrlReqType urlReqType_ = UrlReqType.UNKNOWN;//该请求的类型
    private long logTime_ = 0;//该log记录的时间
    private long timeSpend_ = 0;//该请求花费的时间
    
    public URLTimeLog(UrlReqType reqType, long logTime, long timeSpend){
        urlReqType_ = reqType;
        logTime_ = logTime;
        timeSpend_ = timeSpend;
    }
    
    public long getLogTime(){
        return logTime_;
    }
    
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append(urlReqType_.name()).append("[").append(timeSpend_).append("]");
        return sb.toString();
    }
}
