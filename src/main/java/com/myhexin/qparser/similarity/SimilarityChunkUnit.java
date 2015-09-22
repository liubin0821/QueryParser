package com.myhexin.qparser.similarity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;

import com.myhexin.qparser.Query;
import com.myhexin.qparser.similarity.SimilarityChunkResult.Candidate;
import com.myhexin.qparser.util.StrStrPair;


/**
 * 相似问句的Chunk单元。
 * @author luwenxing
 */
public class SimilarityChunkUnit {
	public static double SIMI_ACCEPT_SCORE = 0.6;
	
	/** 原始的chuank问句 */
	private String rawChunkQuery ;
	
	/** 新生成的chunkQuery */
	private String newChunkQuery ;
	
	/** 生成的chunkQuery候选 */
	private List<Candidate> candidates ;
	
	/** 泛化单元 */ 
	private NormalizeTool nt ;
	
	/** 请求单元 */ 
	SimRequestUnit simRq ;
	
	/** 相似的err log */ 
	private StringBuilder simErrorLogSb = new StringBuilder() ;
	
	/** 返回的相似结果 */
	private SimilarityResponse response ; 
	
	/** 返回的相似结果的领域类型 */
	private Query.Type queryType ;
	
	public String toString() {
		String s1 = String.format("rawChunkQuery:[%s],newChunkQuery:[%s] ", rawChunkQuery, newChunkQuery);
		String s2 = null;
		if(candidates!=null)
			s2 = String.format("candidates:[%s]", candidates.toString());
		String s3 = String.format("simRq:[%s],response:[%s] ", simRq.toString(), response.toString());
		return s1  +"\n" + s2 + "\n" + s3;
	}
	
	/********************** constructor ********************/
	public SimilarityChunkUnit(NormalizeTool nt, SimRequestUnit simRq, Query.Type aQueryType) {
		this.nt = nt ;
		this.simRq = simRq ;
		this.rawChunkQuery = nt.text() ;
		this.queryType = aQueryType;
	}
	
	/********************** method ********************/
	
	public String getNewChunkQuery() {
		return this.newChunkQuery ;
	}
	
	public List<Candidate> getCandidates(){
		return this.candidates ;
	}
	
	public String errorLog(){
		return this.simErrorLogSb.toString() ;
	}
	
	public void setSimilarityResponse(SimilarityResponse response){
		this.response = response ;
	}
	
	/**
	 * 生成新的chunkQuery，即新的A。
	 * 如果请求solr的返回结果中没有符合条件的，直接返回rawChunkQuery
	 * @return a newChunkQuery
	 */
	public String creatNewChunkQuery() {
		if (this.response != null && this.response.response != null) {
			this.response.parseJson();
			String rplStr = this.repalce(nt, response.reponseA()) ;
			if(rplStr == null || rplStr.isEmpty()) {
				return this.newChunkQuery = this.rawChunkQuery ;
			} else {
				return this.newChunkQuery = rplStr ;
			}
		}
		return this.newChunkQuery = this.rawChunkQuery;
	}
	
	/**
	 * 生成候选的chunkQuery，供交互之用。若请求solr返回结果没有候选，
	 * 对应的chunk直接返回rawChunkQuery
	 */
	public List<Candidate> createCandidateChunkQueries(){
		if(this.response == null || this.response.response == null || !(response instanceof FuzzySimilarityResponse)) {
			return Collections.emptyList() ;
		}
		this.response.parseJson();
		List<Candidate> rtnList = new ArrayList<Candidate>() ;
		int accMatchIndex = -1;//有bpattern精确匹配上的文档位置
		for(FuzzySimilarityResponse.Document doc : ((FuzzySimilarityResponse)response).topA()){
		    if( !isParseAbleNPattern(doc) ){
		        continue;
		    }
			String newA= this.repalce(nt, doc.text()) ;
			String needMatchNPattern = nt.nPattern();
			if(newA != null && !newA.isEmpty()){
				Candidate candidate = new Candidate(newA, doc.score(), this.queryType) ;
				candidate.log(this.simErrorLogSb.toString()) ;
				if(candidate.logStr().contains(ChunkSimilarity.SIM_ERROR_NULL)
						|| candidate.logStr().contains(ChunkSimilarity.SIM_ERROR_REPLACE)
						|| candidate.logStr().contains(ChunkSimilarity.SIM_ERROR_SEGGER)
						|| candidate.logStr().contains(ChunkSimilarity.SIM_ERROR_EXCEPTION)){
					continue ;
				} else {
				    rtnList.add(candidate) ;
				    String docBPattern = doc.getUID();
				    if( needMatchNPattern.equals(docBPattern) ){
				        accMatchIndex = rtnList.size()-1;
				    }
				}
			}
		}
		tryMovAccMatchToTopAndDE(rtnList,accMatchIndex);
		tryDE(rtnList);
		return this.candidates = rtnList ;
	}
	
	private void tryDE( List<Candidate> candidates ){
	    Set<String> metCandStrs = new HashSet<String>();
	    List<Candidate> newCandidates = new ArrayList<Candidate>();
	    String nowQuery = null;
	    for(Candidate nowCand : candidates){
	        nowQuery = nowCand.text();
	        if( metCandStrs.contains(nowQuery) ){
	            continue;
	        }
	        newCandidates.add(nowCand);
	        metCandStrs.add(nowQuery);
	    }
	    candidates.clear();
	    candidates.addAll(newCandidates);
	}
	
	/**
	 * 如果有精确匹配的结果，将精确匹配的结果移动到所有结果的最顶端，并且去重
	 * @param candidates
	 * @param accMatchPos
	 */
	private void tryMovAccMatchToTopAndDE(
	        List<Candidate> candidates,
	        int accMatchPos){
	    if( accMatchPos < 0 || accMatchPos >= candidates.size() ){
	        return;
	    }
	    Candidate nowCandidate = null;
	    List<Candidate> newCandidates = new ArrayList<Candidate>();
	    nowCandidate = candidates.get(accMatchPos);
	    newCandidates.add(nowCandidate);
	    for( int i=0; i<candidates.size(); i++ ){
	        if( i == accMatchPos ){
	            continue;
	        }
	        nowCandidate = candidates.get(i);
	        newCandidates.add(nowCandidate);
	    }
	    candidates.clear();
	    candidates.addAll(newCandidates);
	}
	
	private boolean isParseAbleNPattern( FuzzySimilarityResponse.Document doc ){
	    double score = doc.score();
	    if( score >= 1.0 ){
	        return true;
	    }
	    boolean scoreOK = score >= SIMI_ACCEPT_SCORE;
	    String npattern = doc.text();
	    NPatternInfos nPatternInfo = NPatternInfos.getInstance();
	    boolean parseOK = nPatternInfo.checkExist(this.queryType, npattern);
	    if( scoreOK && parseOK ){
	        return true;
	    }else{
	        return false;
	    }
	}
	
	public SimilarityChunkResult getSimChkRlt(){
		SimilarityChunkResult simChkRlt = new SimilarityChunkResult(this.rawChunkQuery,this.candidates) ;
		simChkRlt.extractFirstCandidate();
		//以下信息用于log
		simChkRlt.setnPattern(this.nt.nPattern());
		simChkRlt.setSearchRq(this.simRq);
		simChkRlt.setSearchResponse(this.response);
		if( this.response != null ){
		    simChkRlt.setUrlStr(this.response.urlStr);
		}
		return simChkRlt ;
	}
	
	
	/** 替换 */
	public String repalce(NormalizeTool nt, String solrResponseMsg) {
		
		// 处理返回为空的情况
		if(solrResponseMsg == null || solrResponseMsg.isEmpty()) {
			String logStr = nt.text() + ChunkSimilarity.SIM_ERROR_NULL ;
			solrResponseMsg = nt.text() ;
			
			this.log(logStr) ;
			return solrResponseMsg;
		}
		
		int findCount = 0 ;
		int replaceCount = 0 ;
		String rtnMsg = solrResponseMsg ;
		Matcher matcher = ChunkSimilarity.NUM_STOCK_PTN.matcher(rtnMsg) ;
		
		while(matcher.find()) {
			if(findCount == nt.numStockValList().size()){
				String logStr = solrResponseMsg + ChunkSimilarity.SIM_ERROR_REPLACE ;
				this.log(logStr) ;
				return null ;
			}
			StrStrPair pair = nt.numStockValList().get(findCount) ;
			if(!matcher.group(1).equals(pair.first)) {
				String logStr = solrResponseMsg + ChunkSimilarity.SIM_ERROR_REPLACE ;
				this.log(logStr) ;
				return null ;
			}
			rtnMsg = rtnMsg.replaceFirst(ChunkSimilarity.NUM_STOCK_REGEX, pair.second) ;
			++findCount ;
			++replaceCount ;
		}
		
		if(replaceCount != nt.numStockValList().size()) {
			String logStr = solrResponseMsg + ChunkSimilarity.SIM_ERROR_REPLACE ;
			this.log(logStr) ;
			return null ;
		}
		this.log(rtnMsg) ;
		return rtnMsg;
	}

	private void log(String logStr) {
		this.simErrorLogSb.append(logStr) ; 
	}
}
