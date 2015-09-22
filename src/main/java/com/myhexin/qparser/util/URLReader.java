/**
 * revision history
 * Author		DateTime	Comments
 * ------------ ------------ ---------
 * liuxiaofeng	2015/2/3	 删除urlTimeLog_, 没有其他地方用到这个东西
 * liuxiaofeng	2015/2/3	 没有传timeout的给默认timeout
 */

package com.myhexin.qparser.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.params.HttpMethodParams;

import com.myhexin.qparser.Param;
import com.myhexin.qparser.except.ExceptionUtil;
//import com.myhexin.qparser.define.EnumDef.UrlReqType;
import com.myhexin.qparser.except.QPException;
import com.myhexin.qparser.phrase.util.Consts;

public class URLReader{
	private final static org.slf4j.Logger logger_ = org.slf4j.LoggerFactory.getLogger(URLReader.class);
	
	
	static class UTF8PostMethod extends PostMethod {
	    public UTF8PostMethod(String url) {
	        super(url);
	    }

	    @Override
	    public String getRequestCharSet() {
	        return Consts.CHARSET_UTF_8;
	    }
	}
	
	static class ResponseItems{
        private List<ResponseItem> rspItems = new ArrayList<ResponseItem>();
        public void add(ResponseItem rspItem){
            rspItems.add(rspItem);
        }
    }
    
    //用来记录不同线程的对应的耗时日志
    //private URLTimeLogs urlTimeLog_ = new URLTimeLogs();
    
    
    //这个保持静态,只要一个
    private static HttpClient httpClient_=null;
    
    private final String url_;
    
    public URLReader( String url ){
        url_ = url;
    }

    /*public String getThreadTimeLog(String threadKey, long startTime, long endTime){
        return urlTimeLog_.getOneThreadLogStr(threadKey, startTime, endTime);
    }*/
    
    /**
     * 清除记录的某一个线程的log
     * @param threadKey
     */
    /*public void removeThreadLog(String threadKey){
        urlTimeLog_.clearThreadLog(threadKey);
    }*/
    
    public ResponseItems run( RequestItems reqItems){
        Iterator<RequestItem> iterator = reqItems.getIterator();
        RequestItem nowRequestItem = null;
        ResponseItem nowResponseItem = null;
        ResponseItems responseItems = new ResponseItems();
        while(iterator.hasNext()){
            nowRequestItem = iterator.next();
            nowResponseItem = run(nowRequestItem);
            responseItems.add(nowResponseItem);
        }
        return responseItems;
    }
    
    // 可设定请求时间
    /*public ResponseItem run(RequestItem requestItem, int read_content_time_out){
        long start = System.currentTimeMillis();
        PostMethod postMethod = null;
        ResponseItem responseItem = null;
        List<String> errorStr = new ArrayList<String>();
        try {
            String reqParamStr = tryAppendQueryStr(requestItem);
            postMethod = new UTF8PostMethod(reqParamStr);
            postMethod.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, read_content_time_out);
            tryAppendPostBody(requestItem, postMethod);
            responseItem = readResponseItem(postMethod);
        } catch (QPException e) {
            errorStr.add(e.getMessage());
        } finally {
            if( postMethod != null ){
	            try {
	                postMethod.releaseConnection();
	            } catch (Exception e) {
	                errorStr.add(e.getMessage());
	            }
            }
        }
        if( responseItem == null ){
            responseItem = new ResponseItem();
            responseItem.setRspOK(false);
        }
        if( !errorStr.isEmpty() ){
            responseItem.setRspOK(false);
            responseItem.setRspLogs(errorStr);
        }
        long end = System.currentTimeMillis();
        long timeSpend = end-start;
        if(timeSpend>read_content_time_out ) {
        	logger_.error("[ERROR] " + requestItem.getQueryStr() + " timeout [" + timeSpend + "," + read_content_time_out +"]" );
        }
        responseItem.setRequestItem(requestItem);
        responseItem.setTimeUsed(timeSpend);
        //logTime(responseItem);
        return responseItem;
    }*/
    
    public ResponseItem run(RequestItem requestItem){
    	return run(requestItem, Param.READ_CONTENT_TIME_OUT);
    }
    
    public ResponseItem run(RequestItem requestItem, int read_content_time_out){
        long start = System.currentTimeMillis();
        PostMethod postMethod = null;
        ResponseItem responseItem = null;
        List<String> errorStr = new ArrayList<String>(0);
        try {
            String reqParamStr = tryAppendQueryStr(requestItem);
            postMethod = new UTF8PostMethod(reqParamStr);
            tryAppendPostBody(requestItem, postMethod);
            responseItem = readResponseItem(postMethod);
        } catch (QPException e) {
        	String url = getUrl(postMethod);
            errorStr.add(e.getMessage());
            logger_.error("[ERROR] URLReader(url = "+url+"), Error Message=" + e.getMessage());
            logger_.error(ExceptionUtil.getStackTrace(e) );
        } finally {
            if( postMethod != null ){
	            try {
	                postMethod.releaseConnection();
	            } catch (Exception e) {
	                errorStr.add(e.getMessage());
	                String url = getUrl(postMethod);
	                logger_.error("[ERROR] URLReader.finally(url = "+url+"), Error Message ="+ e.getMessage());
	                logger_.error(ExceptionUtil.getStackTrace(e) );
	            }
            }
        }
        if( responseItem == null ){
            responseItem = new ResponseItem();
            responseItem.setRspOK(false);
        }
        if( !errorStr.isEmpty() ){
            responseItem.setRspOK(false);
            responseItem.setRspLogs(errorStr);
        }
        long end = System.currentTimeMillis();
        long timeSpend = end-start;
        if(timeSpend>Consts.TIMEOUT_MILLSECOND ) {
        	String url = getUrl(postMethod);
        	logger_.warn("[WARN] URLReader time= " + timeSpend + ", " + url);
        }
        responseItem.setRequestItem(requestItem);
        responseItem.setTimeUsed(timeSpend);
        //logTime(responseItem);
        return responseItem;
    }
    
    //从postMethod中拿到URL
    private String getUrl(PostMethod postMethod) {
    	String url = null;
    	if(postMethod!=null) {
    		try{
    			url = postMethod.getURI().toString();
    		}catch(Exception e){
    			
    		}
    		if(url!=null) {
    			url += postMethod.getQueryString();
    		}else{
    			url = postMethod.getQueryString();
    		}
    	}
    	return url;
    }
    
    /**
     * 记录每个线程的请求处理时间
     * @param responseItem
     */
    /*private void logTime( ResponseItem responseItem ){
        String threadKey = Util.getNowThreadKey();
        UrlReqType urlReqType = UrlReqType.UNKNOWN;
        RequestItem reqItem = responseItem.getRequestItem();
        if( reqItem != null ){
            urlReqType = reqItem.getUrlReqType();
        }
        long recordTime = System.currentTimeMillis();
        URLTimeLog urlTimeLog = new URLTimeLog(urlReqType, recordTime, responseItem.getTimeUsed());
        urlTimeLog_.add(threadKey, urlTimeLog);
    }*/
    
    private synchronized HttpClient getHttpClient(){
        if( httpClient_ != null ){
            return httpClient_;
        }
        MultiThreadedHttpConnectionManager mgr = new MultiThreadedHttpConnectionManager();
        mgr.getParams().setDefaultMaxConnectionsPerHost(com.myhexin.qparser.Param.PER_HOST_CONN_COUNT);
        mgr.getParams().setMaxTotalConnections(com.myhexin.qparser.Param.TOTAL_CONN_COUNT);
        mgr.getParams().setConnectionTimeout(com.myhexin.qparser.Param.CREATE_CONN_TIME_OUT);
        mgr.getParams().setSoTimeout(com.myhexin.qparser.Param.READ_CONTENT_TIME_OUT);
        //设置如果不成功，默认重试3次
        mgr.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler());
        httpClient_ = new HttpClient(mgr);
        return httpClient_;
    }
    
    /**
     * 执行的post method,并且读取内容 
     * @param postMethod
     * @return
     * @throws QPException
     */
    private ResponseItem readResponseItem( PostMethod postMethod ) throws QPException{
        String logStr = null;
        InputStream in = null;
        InputStreamReader isReader = null;
        BufferedReader reader = null;
        try {
            HttpClient httpClient = getHttpClient();
            int statusCode = httpClient.executeMethod(postMethod);
            if (statusCode != HttpStatus.SC_OK) {
                logStr = String.format("Rsp is not ok, status code [%s]", statusCode);
                throw new QPException(logStr);
            }
            List<String> contentBuffer = new ArrayList<String>();
            
            in = postMethod.getResponseBodyAsStream();
            isReader = new InputStreamReader(in, Consts.CHARSET_UTF8);
            reader = new BufferedReader(isReader);
            String inputLine = null;
            while ((inputLine = reader.readLine()) != null) {
                contentBuffer.add(inputLine);
            }
            ResponseItem responseItem = new ResponseItem();
            responseItem.setRspRlts(contentBuffer);
            responseItem.setRspOK(true);
            return responseItem;
        } catch (IOException e) {
            logStr = e.getMessage();
            throw new QPException(logStr);
        } catch( Exception e ){
            logStr = e.getMessage();
            throw new QPException(logStr);
        }finally{
            try {
                if (reader != null ) {
                    reader.close();
                }
                if( isReader != null ){
                    isReader.close();
                }
                if( in != null ){
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    /**
     * 如果包含了url的query信息，则追加query字符串
     * @param requestItem
     * @return
     * @throws QPException 
     */
    private String tryAppendQueryStr(RequestItem requestItem) throws QPException{
        String logStr = null;
        String rlt = url_;
        String queryStr = requestItem.getQueryStr();
        if( queryStr != null ){
            try {
                rlt = url_+ URLEncoder.encode(queryStr,Consts.CHARSET_UTF_8);
            } catch (UnsupportedEncodingException e) {
                logStr = e.getMessage();
                throw new QPException(logStr);
            }
        }
        return rlt;
    }
    
    /**
     * 尝试添加post的信息
     * 注意：当同时有postBodyStr和kvMap的时候，以kvMap为优先
     * 二者不可同时使用
     * @return
     */
    private void tryAppendPostBody(RequestItem requestItem, PostMethod postMethod)throws QPException{
        Map<String, String> kvMap = requestItem.getPostKVMap();
        if( kvMap != null ){
            setNameValuePairs(kvMap,postMethod);
            return;
        }
        String postBodyStr = requestItem.getPostBodyStr();
        if( postBodyStr != null ){
            setPostBodyStr(postBodyStr, postMethod);
        }
    }
    
    /**
     * 为一个post method设置post body str
     * @throws QPException 
     */
    private void setPostBodyStr( String postBodyStr, PostMethod postMethod ) throws QPException{
        String logStr = null;
        StringRequestEntity reqEntity = null;
        try {
            reqEntity = new StringRequestEntity(postBodyStr, Consts.TEXT_PLAIN, Consts.CHARSET_UTF_8);
        } catch (UnsupportedEncodingException e) {
            logStr = e.getMessage();
            throw new QPException(logStr);
        }
        postMethod.setRequestEntity(reqEntity);
    }
    
    /**
     * 为一个post method设置kv map
     * @param kvMap
     * @param postMethod
     */
    private void setNameValuePairs( Map<String, String> kvMap, PostMethod postMethod ){
        List<NameValuePair> nvpList = new ArrayList<NameValuePair>();
        for( Entry<String,String> kv : kvMap.entrySet() ){
            nvpList.add(new NameValuePair(kv.getKey(), kv.getValue()));
        }
        NameValuePair[] nvpArray = nvpList.toArray(new NameValuePair[nvpList.size()]);
        postMethod.setRequestBody(nvpArray);
    }
}