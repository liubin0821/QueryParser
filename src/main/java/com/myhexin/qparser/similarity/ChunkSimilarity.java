package com.myhexin.qparser.similarity;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import com.google.gson.Gson;
import com.myhexin.qparser.Query.Type;
import com.myhexin.qparser.similarity.SimilarityChunkResult.Candidate;
import com.myhexin.qparser.util.StrStrPair;
//import com.myhexin.qparser.pattern.PatternUtil;
/**
 * ChunkSimilarity是提供相似服务的类，通过调用泛化模块(NormalizeCollection、NormalizeTool)
 * 生成npattern、segger、indexes、nodelist信息，向solr请求相似问句，并处理返回的结果。
 * @author luwenxing
 */

public class ChunkSimilarity {
	
	/** 按chunk划分的query，以“_&_”分隔 */
	private String chunkedQueryStr_ ; 
	
	/** 问句类型 */
	private Type type_ ;
	
	/** 分隔符 */
	public static final String SPLIT_TAG = "_&_" ;
	
	/** 当前npattern支持的泛化类型 */
	public static final String NUM_STOCK_REGEX = "(_HKSTOCK_|_FUND_|_STOCK_|_SMLNUM_|_BIGNUM_)" ;
	public static final Pattern NUM_STOCK_PTN = Pattern.compile(NUM_STOCK_REGEX) ;
	
	/** 定义出现ERROR时的常量 */
	static final String SIM_ERROR_EXCEPTION = "[EXCEPTION]" ;
	static final String SIM_ERROR_NULL = "[NULL]" ;
	static final String SIM_ERROR_REPLACE = "[REPLACE]" ;
	static final String SIM_ERROR_SEGGER = "[SEGGER]" ;
	static final String NULL = "[NULL]" ;
	
	/** log */
	private List<String> logList = new ArrayList<String>() ;
	private int logIdx ;
	
	/** 请求solr时，是否使用Accurate Search ，默认使用Fuzzy Search*/
	private boolean isAccurateSearch = false ;
	
	// degbug param
	public static final boolean DEBUG_TIME_LOG = false ;
	
	
	private static org.slf4j.Logger logger_ = org.slf4j.LoggerFactory
            .getLogger(ChunkSimilarity.class.getName());
	
	/** 构造函数 */
	public ChunkSimilarity(String chunkedQueryStr, Type type){
		this.chunkedQueryStr_ = chunkedQueryStr ;
		this.type_ = type ;
	}
	
	public void accurateSearch(){
		this.isAccurateSearch = true ;
	}
	
	public void similaritySearch(){
		this.isAccurateSearch = false ;
	}
	
	public boolean searchType(){
		return this.isAccurateSearch ;
	}
	
	/** 当不使用交互时，外部访问相似模块的主函数 
	 * @return new A and log
	 */
	public StrStrPair getSimilarityChunkQueryA(){
		/** 对or和提取失败的不做处理 */
		if(!checkChunkedQueryStrIsLegal()) {
			return new StrStrPair(this.chunkedQueryStr_, SIM_ERROR_NULL) ;
		}
		
		int logListSize = this.chunkedQueryStr_.split(SPLIT_TAG).length ;
		
		while(logListSize-- > 0) {
			this.logList.add(NULL) ;
		}
		String rtnStr = chunkedQueryStr_;
		String chunkStr = chunkedQueryStr_;
		long begTime = System.nanoTime() ;
		
		try {
			rtnStr = this.getSimilarityChunkFromSolr(chunkStr);
		} catch(Exception e) {
			logger_.error(e.getMessage()) ;
			return new StrStrPair(this.chunkedQueryStr_, SIM_ERROR_EXCEPTION) ;
		}
		
		if(DEBUG_TIME_LOG) {
			double timeCost = (System.nanoTime()-begTime)/1000000.0 ;
			logger_.info(String.format("sim search time : %.2f ms", timeCost));
		}
		return new StrStrPair(rtnStr, this.logAarrToString()) ;
	}
	
	/**
	 * 当使用交互时，外面访问相似模块的主函数。
	 * 本方法获取一个chunk句的TOP K个相似句，返回用于交互。
	 * @return
	 */
	public List<SimilarityChunkResult> getSimChkQueryAList(){
		List<SimilarityChunkResult>  rtnList = null ;
		/** 对or和提取失败的不做处理 */
		if(!checkChunkedQueryStrIsLegal()) {
			rtnList = null ;
		} else {
			try {
				//核心代码
				rtnList = this.getSimChkFromSolrForInterface(this.chunkedQueryStr_) ;
			} catch(Exception e) {
				e.printStackTrace() ;
				logger_.error(e.getMessage()) ;
			}
		}
		
		
		/** 对返回为null的情况做处理，只初始化rawText */
		if(rtnList == null){
			rtnList = new ArrayList<SimilarityChunkResult>() ;
			String[] chunkStrs = chunkedQueryStr_.split(SPLIT_TAG);
			for(String rawChunkStr : chunkStrs){
				List<Candidate> emptyCandidates = new ArrayList<Candidate>(0) ;
				SimilarityChunkResult simChkRlt = new SimilarityChunkResult(rawChunkStr, emptyCandidates) ;
				simChkRlt.extractFirstCandidate() ;
				rtnList.add(simChkRlt) ;
			}
		}
		return rtnList;
	}

	
	//检查问句是否合法
	private boolean checkChunkedQueryStrIsLegal() {
		if(this.chunkedQueryStr_ == null 
					|| this.chunkedQueryStr_.isEmpty()
					|| this.chunkedQueryStr_.contains("_OR_")
					|| this.chunkedQueryStr_.contains("提取chunk失败")
					|| this.chunkedQueryStr_.contains("CHUNK-FAIL")) {
			return false ;
		}
		return true ;
	}
	
	public String getSimilarityChunkFromSolr (String rawQueryStr){
	    long begTime = System.nanoTime() ;
		NormalizeCollection nts = new NormalizeCollection(rawQueryStr, this.type_) ;
		if(nts.parse() == false ) {
			this.logIdx = 0 ;
			String logStr = rawQueryStr + SIM_ERROR_SEGGER ;
			this.logList.clear() ;
			this.logList.add(NULL) ;
			this.log(logStr) ;
			return rawQueryStr ;
		}
		if(DEBUG_TIME_LOG) {
		    double timeCost = (System.nanoTime()-begTime)/1000000.0 ;
		    //ChunkSimilarity.TOTAL_SIM_TIME += timeCost ;
		    logger_.info(String.format("sim npattern create time : %.2f ms", timeCost));
		}
		
		begTime = System.nanoTime() ;
		List<SimilarityChunkUnit> simUnitList = createSimUnitList(nts);
		SimSearch simSearch = new SimSearch(simUnitList, this.isAccurateSearch) ;
		if(simSearch.search(simUnitList)) {
			StringBuilder rtnSb = new StringBuilder() ;
			this.logList.clear() ;
			for(SimilarityChunkUnit simUnit : simUnitList) { 
				simUnit.creatNewChunkQuery() ;
				if(rtnSb.length() == 0) {
					rtnSb.append(simUnit.getNewChunkQuery()) ;
				} else {
					rtnSb.append(SPLIT_TAG).append(simUnit.getNewChunkQuery());
				}
				logList.add(simUnit.errorLog()) ;
			}
			if(DEBUG_TIME_LOG) {
	                double timeCost = (System.nanoTime()-begTime)/1000000.0 ;
	                //ChunkSimilarity.TOTAL_SIM_TIME += timeCost ;
	                logger_.info(String.format("sim solr search time : %.2f ms", timeCost));
	        }
			return rtnSb.toString() ;
		} else {
		    if(DEBUG_TIME_LOG) {
		        double timeCost = (System.nanoTime()-begTime)/1000000.0 ;
		        //ChunkSimilarity.TOTAL_SIM_TIME += timeCost ;
		        logger_.info(String.format("sim solr search time : %.2f ms", timeCost));
		    }
			return rawQueryStr ;
		}
	}
	
	/**
	 * 从solr中获取相似的A，返回作为交互的结果
	 * @param rawQueryStr
	 * @return 返回交互结果，若为空则返回null，外部会对null做处理。
	 */
	private List<SimilarityChunkResult> getSimChkFromSolrForInterface (String rawQueryStr){
		NormalizeCollection nts = new NormalizeCollection(rawQueryStr, this.type_) ;
		double begTime = System.nanoTime();
		if(nts.parse() == false) {
			return null ;
		}
		if(DEBUG_TIME_LOG) {
            double timeCost = (System.nanoTime()-begTime)/1000000.0 ;
            logger_.info(String.format("sim npattern create time : %.2f ms", timeCost));
        }
		
		
		begTime = System.nanoTime();
		/** 所有的信息都存储在SimilarityChunkUnit */
		List<SimilarityChunkUnit> simUnitList = createSimUnitList(nts);
		if(DEBUG_TIME_LOG) {
              double timeCost = (System.nanoTime()-begTime)/1000000.0 ;
              logger_.info(String.format("sim solr pre search time : %.2f ms", timeCost));
        }
		List<SimilarityChunkResult> simChkRltList = new ArrayList<SimilarityChunkResult>() ;
		SimSearch search = new SimSearch(simUnitList, this.isAccurateSearch) ;
		
		begTime = System.nanoTime();
		if(search.search(simUnitList)) {
			if(DEBUG_TIME_LOG) {
				for(SimilarityChunkUnit simUnit : simUnitList) { 
					System.out.println("simUnit : " + simUnit.toString());
				}
			}
			
			for(SimilarityChunkUnit simUnit : simUnitList) { 
				simUnit.createCandidateChunkQueries() ;
				simChkRltList.add(simUnit.getSimChkRlt());
			}
			if(DEBUG_TIME_LOG) {
                double timeCost = (System.nanoTime()-begTime)/1000000.0 ;
                logger_.info(String.format("sim solr search time : %.2f ms", timeCost));
            }
			
			if(DEBUG_TIME_LOG) {
				for(SimilarityChunkUnit simUnit : simUnitList) { 
					System.out.println("\tsimUnit : " + simUnit.toString());
					
				}
			}
			return simChkRltList ;
		} else {
		    if(DEBUG_TIME_LOG) {
                double timeCost = (System.nanoTime()-begTime)/1000000.0 ;
                logger_.info(String.format("sim solr search time : %.2f ms", timeCost));
            }
			return null ;
		}
	}
	
	/**
	 * 使用泛化单元集合，初始化相似单元。
	 * @param 泛化单元的集合
	 * @return 相似单元集合
	 */
	private List<SimilarityChunkUnit> createSimUnitList(NormalizeCollection nts) {
		List<SimilarityChunkUnit> simUnitList = new ArrayList<SimilarityChunkUnit>() ;
		for(int idx = 0 ; idx < nts.ntList().size() ; ++idx) {
			NormalizeTool nt = nts.ntList().get(idx) ;
			NormalizeResult result = new NormalizeResult() ;
			result.assignResult(nt) ;
			String json = new Gson().toJson(result) ; 
			//System.out.println("solr similar result : " + json);
			SimRequestUnit simRq = new SimRequestUnit(json) ;
			SimilarityChunkUnit simUnit = new SimilarityChunkUnit(nt, simRq, nts.getQueryType()) ;
			simUnitList.add(simUnit) ;
		}
		return simUnitList;
	}
	
	private void log(String logStr){
		if(logList.get(logIdx).equals(NULL)) {
			logList.set(logIdx, logStr) ;
		}
	}
	
	private String logAarrToString(){
		StringBuilder sb = new StringBuilder() ;
		for(String logStr : this.logList){
			if(sb.length() == 0) {
				sb.append(logStr) ;
			} else {
				sb.append("_&_" + logStr) ;
			}
		}
		return sb.toString() ;
	}
	
	public static double TOTAL_SIM_TIME = 0.0 ;
	public static double TOTAL_SEG_TIME = 0.0 ;
	public static List<String> QUERY_CANDIDATE_LIST = new ArrayList<String>() ;
	public static int COUNT = 0 ;
	
	public static int SINGLE_CHUNK_QUERY_COUNT = 0 ;
	public static int MUTIL_CHUNK_QUERY_COUNT = 0 ;
	public static int CHUNK_COUNT = 0 ;
}
