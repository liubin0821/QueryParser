package com.myhexin.qparser.similarity;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.myhexin.qparser.Param;
import com.myhexin.qparser.define.EnumDef.UrlReqType;
//import com.myhexin.qparser.pattern.PatternUtil;
import com.myhexin.qparser.util.RequestItem;
import com.myhexin.qparser.util.ResponseItem;
import com.myhexin.qparser.util.URLReader;
import com.myhexin.qparser.util.URLTimeLog;

public class SimSearch {
	
	 /** 通过SFE一次性请求solr */
    public static boolean CALL_SFE_BY_ONE_TIME = true;
    //search_accurate_cq_from_solr_url=http://172.20.201.65:5300/solr/select?qt=search&wt=json
    //search_similarity_cq_from_solr_url=http://172.20.201.65:5300/solr/select?qt=chunk&indent=on&wt=json
    //search_cq_from_sfe_url=http://172.20.201.87:8080/search?
    public static String SEARCH_ACCURATE_CQ_FROM_SOLR_URL = "http://172.20.201.65:5300/solr/select?qt=search&wt=json" ;
    public static String SEARCH_SIMILARITY_CQ_FROM_SOLR_URL = "http://172.20.201.65:5300/solr/select?qt=chunk&indent=on&wt=json" ;
    public static String SEARCH_CQ_FROM_SFE_URL = "http://172.20.201.87:8080/search?" ;
    
    
	List<SimilarityChunkUnit> simChkUnitList ;
	boolean isAccurateSearch = false ;
	
	public SimSearch(List<SimilarityChunkUnit> simChkUnitList, boolean isAcc ){
		this.simChkUnitList = simChkUnitList ;
		this.isAccurateSearch = isAcc;
	}
	
	public List<SimilarityChunkUnit> search(){
		this.search(this.simChkUnitList) ;
		return this.simChkUnitList ;
	}
	
	/**
	 * 总的search模块入口。依靠{@link Param.CALL_SFE_BY_ONE_TIME和SimSearch.isAccurateSearch}
	 * 两个变量控制请求的方式。前者控制是否请求SFE，后者控制是否请求精确匹配。
	 * 当前系统支持以下请求： 
	 * <li> 多chunk，一次性请求SFE或分次请求solr
	 * <li> 单chunk只请求solr
	 * @param simChkUnitList
	 * @return
	 */
	public boolean search(List<SimilarityChunkUnit> simChkUnitList) {
		boolean isSearchSFE = CALL_SFE_BY_ONE_TIME && !isAccurateSearch ;	//精确匹配不请求SFE
		isSearchSFE &= simChkUnitList.size() > 1 ;	// 单chunk问句不请求SFE
		try{
			if(isSearchSFE) {
				List<String> params = new ArrayList<String>() ;
				for(SimilarityChunkUnit simUnit : simChkUnitList) {
					params.add(simUnit.simRq.requestJson) ;
				}
				List<FuzzySimilarityResponse> rtnList = this.searchSFE(params) ;
				if( rtnList == null || rtnList.isEmpty() ){
				    return false;
				}
				for(int idx = 0 ; idx < rtnList.size() ; ++idx){
					SimilarityChunkUnit simChkUnit = simChkUnitList.get(idx) ;
					FuzzySimilarityResponse response = rtnList.get(idx) ;
					simChkUnit.setSimilarityResponse(response) ;
				}
			} else {
				for(SimilarityChunkUnit simUnit : simChkUnitList) {
					SimilarityResponse response = this.search(simUnit) ;
					simUnit.setSimilarityResponse(response) ;
				}
			}
		} catch (Exception e) {
		    String errMsg = String.format("Search error msg: %s", e.getMessage()) ;
            logger_.warn(errMsg) ;
			return false ;
		}
		return true ;
	}
	
	
	/**
	 * 单次请求solr，查找相似问句
	 * @param simChkUnit
	 * @return
	 */
	private SimilarityResponse search(SimilarityChunkUnit simChkUnit) {
		SimilarityResponse response = searchSolr(simChkUnit.simRq.requestJson);
		return response;
	}
	
	/** 从solr中查找相似的A */
	private SimilarityResponse searchSolr(String json) {
		SimilarityResponse response = null ;
		if(isAccurateSearch == true) { 
			response = searchSimiSolr(SEARCH_ACCURATE_CQ_FROM_SOLR_URL, json);
		} else {
			response = searchSimiSolr(SEARCH_SIMILARITY_CQ_FROM_SOLR_URL, json);
		}
		return response;
	}
	
	/**
	 * 请求SFE
	 * @param params
	 * @return
	 */
	private List<FuzzySimilarityResponse> searchSFE(List<String> params){
		StringBuilder requestBuilder = new StringBuilder() ;
		for(int idx = 0 ; idx < params.size() ; ++idx){
			String param = params.get(idx) ;
			requestBuilder.append(SOLR_PARAM).append(param) ;
			if(idx < params.size()-1) {
				requestBuilder.append("%16") ;     // "%16"为SFE约定的参数分隔符
			}
		}
		String paramContent = SFE_HEAD + encodeForSFE(requestBuilder.toString());
		String responseMsg = submitPost(SEARCH_CQ_FROM_SFE_URL, paramContent) ;
		if( responseMsg == null || responseMsg.isEmpty() ){
		    return new ArrayList<FuzzySimilarityResponse>();
		}
		List<FuzzySimilarityResponse> list = new Gson().fromJson(responseMsg, new TypeToken<List<FuzzySimilarityResponse>>(){}.getType()) ;
		return list ;
	}
	
	/**
	 * 按SFE需要的字符串格式进行编码 :
	 * = : %3d
	 * & : %15
	 * : : %3a
	 * { : %7b
	 * [ : %5b
	 * ] : %5d
	 * } : %7d
	 * @param input
	 * @return
	 */
	public static String encodeForSFE(String input){
		return input.replace("=", "%3D").replace("&", "%15") ;
	}
	
	
	/** 查询相似问句solr
	 *  失败返回null
	 * */
	private FuzzySimilarityResponse searchSimiSolr( String urlBody, String paramJson){
		FuzzySimilarityResponse responseRlt = null ;
		String responseStr = null;
        String logStr = null;
        RequestItem reqItem = new RequestItem(UrlReqType.SIMI_SOLR);
        reqItem.setQueryStr(paramJson);
        //TODO can not get rlt
        StringBuilder urlStr = new StringBuilder().append(urlBody).append("&q=");
        URLReader urlReader = new URLReader(urlStr.toString());
        ResponseItem rspItem = urlReader.run(reqItem);
        if (!rspItem.getRspOK()) {
            logStr = rspItem.getRspLogsStr("\n");
            logger_.error(logStr);
            return null;
        }
        responseStr = rspItem.getRspRltsStr("");
        responseRlt = (FuzzySimilarityResponse) new Gson().fromJson(responseStr, FuzzySimilarityResponse.class);
        // 用于log
        responseRlt.urlStr = urlStr.append(paramJson).toString();
		return responseRlt ;
	}
	
	/**
	 * 向指定url提交一些数据
	 * @param url
	 * @param paramContent
	 * @return
	 */
	private String submitPost(String url, String paramContent) {
	    String logStr = null;
		RequestItem reqItem = new RequestItem(UrlReqType.SIMI_SFE);
		reqItem.setPostBodyStr(paramContent);
		URLReader urlReader = new URLReader(url);
		ResponseItem rspItem = urlReader.run(reqItem);
		if( !rspItem.getRspOK() ){
		    logStr = rspItem.getRspLogsStr("\n");
		    logger_.error(logStr);
		    return null;
		}
		String rlt = rspItem.getRspRltsStr("");
		return rlt;
	}
	
	private static final String SFE_HEAD = "q=*:*&tid=fwds&type=patterOperation&wt=1&udps=" ;
	private static final String SOLR_PARAM = "qt=chunk&indent=on&wt=json&q=" ;
	private static org.slf4j.Logger logger_ = org.slf4j.LoggerFactory
            .getLogger(SimSearch.class.getName());
}
