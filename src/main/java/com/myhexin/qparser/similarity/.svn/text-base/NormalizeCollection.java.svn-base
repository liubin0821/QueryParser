package com.myhexin.qparser.similarity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import com.myhexin.qparser.Query;
import com.myhexin.qparser.Query.Type;
//import com.myhexin.qparser.define.EnumDef.MODULE;

/**
 * 一个问句所有的chunk泛化的集合。构造此类为的就是在分词时，
 * 能将整的query的text一次性请求分词服务，以提高性能。
 */
public class NormalizeCollection implements Serializable{
	private static final long serialVersionUID = 944017751008073238L;
	
	private static org.slf4j.Logger logger_ = org.slf4j.LoggerFactory.getLogger(NormalizeCollection.class.getName());
	boolean DEBUG_TIME_LOG = false;
	
	/** 原始的query的Text，注意：<b>text中用_&_来标记chunk</b> */
	private String rawQueryStr_ ;
	
	/** 预处理过的 rawQueryStr_，将里面的标点符号无关的信息去除*/
	private String preTreatRawStr_ ;
	
	/** 问句的类型 */
	private Type type_ ;
	
	/** 包含的泛化的单元，一个chunk一个NormalizeTool */
	List<NormalizeTool> ntList = new ArrayList<NormalizeTool>() ;
	
	/** constructor */
	public NormalizeCollection(String rawQueryStr, Type type) {
		this.rawQueryStr_ = rawQueryStr ;
		this.preTreatRawStr_ = preTreatRawText(rawQueryStr) ;
		this.type_ = type ;
	}
	
	public Query.Type getQueryType(){
	    return this.type_;
	}
	
	/**
	 * 去除无关的信息，如chunk前后的标点符号
	 * @param line
	 * @return 预处理后的text
	 */
	private static String preTreatRawText(String line){
		String[] arr = line.split(ChunkSimilarity.SPLIT_TAG) ;
		StringBuilder rtnSb = new StringBuilder() ;
		for( int idx = 0; idx < arr.length ; ++idx ){
			String chunkQ = arr[idx] ;
			Matcher m = NormalizeTool.DECIMAL_PTN.matcher(chunkQ) ;
	        if(m.find()) {
	        	arr[idx] = m.group(1) ;
	        }
	        if(rtnSb.length() == 0) {
	        	rtnSb.append(arr[idx]) ;
	        } else {
	        	rtnSb.append(ChunkSimilarity.SPLIT_TAG + arr[idx]) ;
	        }
		}
		
		return rtnSb.toString() ;
	}
	
	
	/**
	 * 一次性将已chunk的问句发送到分词服务，请求分词，
	 * 而后对问句中的每一个chunk进行npattern泛化。
	 * @return
	 */
	public boolean parse() {
	    long begTime = System.nanoTime() ;
		// 统一请求无同义词转换的分词
		String segLine = NormalizeUtil.tokenizeBySegger(this.preTreatRawStr_, this.type_);
		if( segLine == null ){
		    return false;
		}
		if(DEBUG_TIME_LOG) {
			System.out.println("segLine=" + segLine);
		    double timeCost = (System.nanoTime()-begTime)/1000000.0 ;
		    logger_.info(String.format("sim first ltp call time : %.2f ms", timeCost));
		}
		//统一请求有同义词转换的分词
		begTime = System.nanoTime() ;
        String hasTransSegLine = NormalizeUtil.tokenizeByHasTransSegger(this.preTreatRawStr_, this.type_);
        if( hasTransSegLine == null ){
            return false;
        }
        if(DEBUG_TIME_LOG) {
        	System.out.println("hasTransSegLine=" + hasTransSegLine);
            double timeCost = (System.nanoTime()-begTime)/1000000.0 ;
            logger_.info(String.format("sim first ltp call time : %.2f ms", timeCost));
        }
		
		// 对无同义词转换的分词划分chunk
		String[] segArr = segLine.split(ChunkSimilarity.SPLIT_TAG+"/") ;
		// 对有同义词转换的分词划分chunk
		String[] hasTransSegArr = null;
		if( hasTransSegLine != null && !hasTransSegLine.isEmpty() ){
		    hasTransSegArr = hasTransSegLine.split(ChunkSimilarity.SPLIT_TAG+"/");
		}
		
		// 对原问句划分chunk
		String[] rawQArr = this.rawQueryStr_.split(ChunkSimilarity.SPLIT_TAG) ;
		if( segArr.length != rawQArr.length ) { 
			return  false ; 
		}
		begTime = System.nanoTime() ;
		String nowHasTransSegArr = null;
		for(int idx =0 ; idx < segArr.length ; ++idx){
			// 分词和原问句一一对应的chunk送入NormalizeTool，进行npattern的泛化。
		    if( hasTransSegArr != null && hasTransSegArr.length == segArr.length ){
		        nowHasTransSegArr =  hasTransSegArr[idx];
		    }else{
		        nowHasTransSegArr = null;
		    }
			NormalizeTool nt = new NormalizeTool(this.type_, rawQArr[idx], segArr[idx], nowHasTransSegArr) ;
			nt.normalParse() ;
			this.ntList.add(nt) ;
		}
		if(DEBUG_TIME_LOG) {
            double timeCost = (System.nanoTime()-begTime)/1000000.0 ;
            logger_.info(String.format("sim create chunk npatterns call time : %.2f ms", timeCost));
        }
		return true ;
	}

	public List<NormalizeTool> ntList(){
		return this.ntList ;
	}
}
