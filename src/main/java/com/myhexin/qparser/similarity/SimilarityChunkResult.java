package com.myhexin.qparser.similarity;

import java.util.List;

import com.myhexin.qparser.Query;

/**
 * SimilarityChunkResult : 交互时，用于存储一个chunk的交互的候选集。
 * @author luwenxing
 */
public class SimilarityChunkResult {
	
	/** 原chunk query */
	private String rawText ;
	
	/** chunkQuery的数字泛化Pattern */
	private String nPattern;
	
	/** 所有的候选集 */
	private List<Candidate> candidates ;
	
	/** 最优候选，必须保证分值为1 */
	private String firstCandidateStr ;
	
	/** 选定最后候选后的剩余候选集，若无最优候选， 剩余候选集即为所有的候选集*/
	private List<Candidate> restCandidates ;
    /**
     * 记录查询结果，log用
     */
    private SimilarityResponse similarityResponse;
    /**
     * 记录查询请求，log用
     */
    private SimRequestUnit simRq;
    /**
     * 记录查询请求url，log用
     */
    private String urlStr;
	/**
	 * 生成的相似句的queryType
	 */
    private Query.Type queryType = Query.Type.STOCK;
    
	/**
	 * constructor
	 * @param rawText
	 * @param candidates
	 */
	public SimilarityChunkResult(String rawText, List<Candidate> candidates){
		this.rawText = rawText ;
		this.candidates = candidates ;
	}
	
	
	public String rawText(){
		return this.rawText ;
	}
	
	public String firstCandidateStr(){
		return this.firstCandidateStr ;
	}
	
	
	public List<Candidate> candidates(){
		/** 对外部来讲，候选集即为剩余候选集 */
		return this.restCandidates ;
	}
	
	/** 提取最优候选 */
	void extractFirstCandidate(){
		if(this.candidates == null || this.candidates.isEmpty() || firstCandidateStr != null){
			return ;
		}
		int first = 0 ;
		Candidate firstCandidate = this.candidates.get(first) ;
		if(firstCandidate.score() == 1.0) {
			this.firstCandidateStr = firstCandidate.text() ;
			this.candidates.remove(first) ;
		}
		this.restCandidates = candidates ;
	}
	
	
	public static class Candidate{
		public Candidate(String text, double score, Query.Type aQueryType){
			this.text = text ;
			this.score = score ;
			this.queryType = aQueryType;
		}
		private String text ;
		private double score ;
		private Query.Type queryType ;
		private String log ;
		
		public String text(){ return this.text ; }
		public double score(){ return this.score ; }
		public String logStr(){ return this.log ; }
	    public Query.Type getQueryType(){return queryType;}
	    
		public void log(String logStr){
			this.log = logStr ;
		}
		public String toString(){
		    return String.format("text:[%s],score:[%f],type:[%s]", this.text,this.score,this.queryType);
		}
		
	}

    public void setSearchResponse(SimilarityResponse response) {
        this.similarityResponse = response;
    }

    public String getSearchResponseLog() {
        return this.similarityResponse==null?"NULL":this.similarityResponse.toString();
    }

    public void setSearchRq(SimRequestUnit simRq) {
        this.simRq = simRq;
    }

    public String getSearchRqLog() {
        return this.simRq==null?"NULL":this.simRq.toString();
    }
    
    public void setUrlStr(String urlStr) {
        this.urlStr = urlStr;
    }

    public String getUrlStr() {
        return this.urlStr;
    }

    public void setnPattern(String nPattern) {
        this.nPattern = nPattern;
    }

    public String getnPattern() {
        return nPattern;
    }
}
