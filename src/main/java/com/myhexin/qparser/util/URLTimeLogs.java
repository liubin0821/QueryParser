package com.myhexin.qparser.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class URLTimeLogs {
    private static final long CHECK_CYCLE = 12 * 60 * 60 * 1000;
    private int MAX_LOG_COUNT = 100;
    private long lastCheckTime_ = System.currentTimeMillis();
    private Map<String, LinkedList<URLTimeLog>> logs_ = new HashMap<String, LinkedList<URLTimeLog>>();
    
    /**
     * 向指定的线程添加一个URL log
     * @param threadKey
     * @param urlTimeLog
     */
    public void add( String threadKey, URLTimeLog urlTimeLog){
        if( urlTimeLog == null){
            return;
        }
        tryCleanUselessThreadLog();
        if( !logs_.containsKey(threadKey) ){
            logs_.put(threadKey,new LinkedList<URLTimeLog>());
        }
        LinkedList<URLTimeLog> nowLogs = logs_.get(threadKey);
        if( nowLogs.size() > MAX_LOG_COUNT ){
            for( int i=(nowLogs.size()-1); (i>=MAX_LOG_COUNT && i>=0); i-- ){
                nowLogs.pop();
            }
        }
        nowLogs.add(urlTimeLog);
    }
    
    public String getOneThreadLogStr(String threadKey, long start, long end){
        if( !logs_.containsKey(threadKey) ){
            return null;
        }
        LinkedList<URLTimeLog> nowLogs = logs_.get(threadKey);
        StringBuilder sb = new StringBuilder();
        int size = nowLogs.size();
        URLTimeLog nowTimeLog = null;
        long logTime = 0;
        for( int i=0; i<size; i++ ){
            nowTimeLog = nowLogs.get(i);
            logTime = nowTimeLog.getLogTime();
            if( logTime >= start && logTime <= end ){
                sb.append(nowTimeLog.toString());
                sb.append("-");
            }
        }
        if( sb.length() == 0 ){
            return sb.toString();
        }
        if( "-".equals(sb.substring(sb.length()-1, sb.length())) ){
            sb.replace(sb.length()-1, sb.length(), "");
        }
        return sb.toString();
    }
    
    private void tryCleanUselessThreadLog(){
        long nowTime = System.currentTimeMillis();
        if( nowTime-lastCheckTime_ <= CHECK_CYCLE ){
            return;
        }
        lastCheckTime_ = nowTime;
        Set<String> allLiveThreads = Util.getAllAliveThreadIDStrs();
        Iterator<Entry<String,LinkedList<URLTimeLog>>> iterator = logs_.entrySet().iterator();
        Entry<String, LinkedList<URLTimeLog>> nowEntry = null;
        while( iterator.hasNext() ){
            nowEntry = iterator.next();
            if( !allLiveThreads.contains(nowEntry.getKey()) ){
                iterator.remove();
            }
        }
    }
        
    /**
     * 清除指定线程的url read log
     * @param threadKey
     */
    public void clearThreadLog( String threadKey ){
        if( logs_.containsKey(threadKey) ){
            logs_.remove(threadKey);
        }
    }
}
