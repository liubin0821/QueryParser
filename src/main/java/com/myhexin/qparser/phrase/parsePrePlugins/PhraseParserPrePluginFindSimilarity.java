package com.myhexin.qparser.phrase.parsePrePlugins;

import com.myhexin.qparser.except.ExceptionUtil;
import com.myhexin.qparser.phrase.ParserAnnotation;
import com.myhexin.qparser.similarity.SimilarityQueryWithOptions;


/**
 * 
 * @author 刘小峰 liuxiaofeng@myhexin.com
 * @Version 创建时间 2014-12-26
 *
 */
public class PhraseParserPrePluginFindSimilarity  extends PhraseParserPrePluginAbstract {
	public static org.slf4j.Logger logger_ = org.slf4j.LoggerFactory.getLogger(PhraseParserPrePluginFindSimilarity.class.getName());
	public PhraseParserPrePluginFindSimilarity() {
	        super("Solr_Query_Similarity");
	}
    
    @Override
    public void processSingle(ParserAnnotation annotation) {
    	String queryText = annotation.getChunkedText();
    	if (queryText == null || queryText.trim().length() == 0) {
    		return;
    	} else {
    		SimilarityQueryWithOptions simQueryOption = new SimilarityQueryWithOptions();
    		//Query query = new Query(queryText); //"股票代码是300033");
    		//System.out.println("SimilarityQueryWithOptions Input : " + query.text);
    		String newqText = null;
    		try{
    			newqText = simQueryOption.process(queryText);
    		}catch(Exception e) {
        		logger_.error("[ERROR]" + ExceptionUtil.getStackTrace(e));
        	}
    		
    		//去掉&符号
    		if(newqText!=null) {
    			String[] queryStrs = newqText.split("_&_");
    			if(queryStrs!=null && queryStrs.length>0) {
    				StringBuilder buf = new StringBuilder();
    				int i=0;
    				for(;i<queryStrs.length-1;i++) {
    					if(queryStrs[i]!=null) queryStrs[i] = queryStrs[i].trim();
    					buf.append(queryStrs[i]).append(' ');
    				}
    				buf.append(queryStrs[i]);
    				
    				annotation.setQueryText( buf.toString());
    			}
    		}
    	}
    }
    public String getLogResult(ParserAnnotation annotation ) {
    	String chunkedText = annotation.getChunkedText();
    	String queryText = annotation.getQueryText();
    	if(queryText!=null)
    		return String.format("[Similarity]: %s =&gt; %s\n", chunkedText, queryText);
    	else
    		return null;
    }
}
