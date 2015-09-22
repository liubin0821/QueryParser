package com.myhexin.qparser.phrase.parsePrePlugins;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import com.myhexin.qparser.Param;
import com.myhexin.qparser.Query;
import com.myhexin.qparser.chunk.ClassifyChunkQuery;
import com.myhexin.qparser.except.ExceptionUtil;
import com.myhexin.qparser.phrase.ParserAnnotation;

public class PhraseParserPrePluginClassifyChunkQuery extends PhraseParserPrePluginAbstract {
	public static org.slf4j.Logger logger_ = org.slf4j.LoggerFactory.getLogger(PhraseParserPrePluginClassifyChunkQuery.class.getName());
	
    public PhraseParserPrePluginClassifyChunkQuery() {
        super("Classify_Chunk_Query");
    }
    //为b->a相似问句而添加的
    @Override
    public void processSingle(ParserAnnotation annotation) {
    	String query = annotation.getQueryText();
    	if (query == null || query.trim().length() == 0)
    		return;
    	
    	List<String> queryList = new ArrayList<String>();
    	List<Entry<String, Double>> rlt = null;
    	try{
    		rlt = getChunkRet(query);
    	}catch(Exception e) {
    		logger_.error("[ERROR]" + ExceptionUtil.getStackTrace(e));
    	}
    	
    	//失败了,但是不影响后续执行,所以不停止
    	if(rlt==null) {
    		return;
    	}
    	
    	// 将超过CRF_MIN_PROB_PARSER的可用的chunk划分结果添加到queryList中
        for (Entry<String, Double> rltEntry : rlt) {
    		if ((Double) rltEntry.getValue() >= Param.CRF_MIN_PROB_PARSER) {
    			String str = (String) rltEntry.getKey();
    			if(str!=null) 
    				queryList.add(str);
    		}
        }
        
        
        //把List转成String
        StringBuilder builder = new StringBuilder();
        
        // 如果queryList为空，返回原问句
    	if (queryList == null || queryList.size() == 0) {
    		builder.append( query );
    	} else {
    		int i=0;
    		for(;i<queryList.size()-1;i++) {
    			String s = queryList.get(i);
    			if(s!=null) s= s.trim();
    			builder.append(s).append("_&_");
    		}
    		String s = queryList.get(i);
    		builder.append(s);
    	}
    	annotation.setChunkedText(builder.toString());
    	annotation.setChunkedTextList(queryList);
    }
    
    /*
     * 返回chunk划分结果
     */
    private List<Entry<String, Double>> getChunkRet(String queryStr) {
    	if (queryStr == null || queryStr.trim().length() == 0)
    		return null;
    	
		ClassifyChunkQuery chunkFacade_=new ClassifyChunkQuery();
        List<Entry<String, Double>> rlt = chunkFacade_.createChunkKVRlt(queryStr, Query.Type.ALL, false);
        return rlt;
	}
    
    public String getLogResult(ParserAnnotation annotation ) {
    	String queryText = annotation.getChunkedText();
    	if(queryText!=null)
    		return String.format("[Chunked text]: %s\n", queryText);
    	else
    		return null;
    }
}

