package com.myhexin.qparser.similarity;

import java.util.List;

import com.myhexin.qparser.Query;
import com.myhexin.qparser.ApplicationContext.ApplicationContextHelper;
import com.myhexin.qparser.phrase.PhraseParser;
import com.myhexin.qparser.similarity.SimilarityChunkResult.Candidate;


/**
 * 相似问句B->A转换
 * 
 * @author 刘小峰 liuxiaofeng@myhexin.com
 * @Version 创建时间 2014-12-31
 *
 */
public class SimilarityQueryWithOptions {
	private static org.slf4j.Logger logger_ = org.slf4j.LoggerFactory.getLogger(SimilarityQueryWithOptions.class.getName());
	
	public final static String SEG_IN_CHUNK = "_&_";
	
	//private final static PhraseParser parser = (PhraseParser) ApplicationContextHelper.getBean("phraseParserBacktestCond");
	
	
	private void mergeSililarityChunkResult( List<SimilarityChunkResult> simRtnsAndLog, List<List<Candidate>> numSimiCandidates ){
        if( simRtnsAndLog == null || numSimiCandidates == null ){
            return;
        }
        if( simRtnsAndLog.size() != numSimiCandidates.size() ){
            String logStr = String.format( "solr chunk rlt size is [%s], but num simi rlt size is [%s]", simRtnsAndLog.size(), numSimiCandidates.size() );
            logger_.error(logStr);
            return;
        }
        int size = simRtnsAndLog.size();
        for( int i=0; i<size; i++ ){
            SimilarityChunkResult nowSCR = simRtnsAndLog.get(i);
            List<Candidate> candidates = numSimiCandidates.get(i);
            List<Candidate> solrCandidates = nowSCR.candidates();
            if(solrCandidates != null){
                solrCandidates.clear();
                solrCandidates.addAll(candidates);
            }
        }
    }
	
	private String makeTextFrom(List<SimilarityChunkResult> simRtnsAndLog) {
        StringBuilder sbRtn = new StringBuilder();
        for(int i=0;i<simRtnsAndLog.size();i++){
           SimilarityChunkResult curRtn = simRtnsAndLog.get(i);
            sbRtn.append(curRtn.firstCandidateStr()==null?curRtn.rawText():curRtn.firstCandidateStr());
            sbRtn.append(i==simRtnsAndLog.size()-1?"":SEG_IN_CHUNK);
        }
        sbRtn.trimToSize();
        return sbRtn.toString();
    }

    public String process(String queryText) {
    	Query.Type qType = Query.Type.ALL;
        if(queryText==null || queryText.trim().length()==0){
            return queryText;
        }
        
        String qText  = new String(queryText);
        //number similarity,只有数字或符号的情况,比如只有股票代码数字
        //从npatterninfos.txt配置文件中candidate
        NumChunkSimilarity numChunkSimilarity = new NumChunkSimilarity();
        List<List<Candidate>> numSimiCandidates = numChunkSimilarity.run(qText, qType);
        
        
        //TODO 测试代码
        //System.out.println(qText);
        /*if(numSimiCandidates!=null ) { 
        	 for(List<Candidate> list : numSimiCandidates) {
             	System.out.println("numSimiCandidates:" + list );
             	for(Candidate ca : list) {
                 	System.out.println("\t" + ca.toString() );
                 }
             }
             
        }else{
        	System.out.println("numSimiCandidates == null");
        }*/
        //System.out.println();
        //TODO 测试代码 END
        
        
        
        //solr similarity
        ChunkSimilarity cs = new ChunkSimilarity(qText, qType);
        List<SimilarityChunkResult> simRtnsAndLog = cs.getSimChkQueryAList();
        
        mergeSililarityChunkResult(simRtnsAndLog, numSimiCandidates);
        
        //String origText = query.text;
        String solrSimiRlt = makeTextFrom(simRtnsAndLog);
        qText = solrSimiRlt;
        
        // 切分chunk未成功则不在此处设置chunkLog
        //query.setSimChunkLog(simRtnsAndLog);
        //query.setSimRlt(simRtnsAndLog);
        //qLog.logModRlt(MODULE.SIM, origText + "=>" + query.getText());
        //query.logTime(MODULE.SIM);
        
        return qText;
    }
}
