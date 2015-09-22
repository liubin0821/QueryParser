package com.myhexin.qparser.phrase.parsePrePlugins;


import com.myhexin.qparser.phrase.ParserAnnotation;
import com.myhexin.qparser.tokenize.NoParserMatch;

public class PhraseParserPrePluginCheckIsNoParser extends PhraseParserPrePluginAbstract{
	
    public PhraseParserPrePluginCheckIsNoParser() {
        super("Check_Is_No_Parser");
    }
    
    @Override
    public void processSingle(ParserAnnotation annotation) {
    	String query = annotation.getQueryText();
    	if (NoParserMatch.noParserTrieTree.mpMatchingOne(query)) { // 临时处理，系统解析不完善的不再进行分词等操作
    		annotation.setStopProcessFlag(true);
    		annotation.setQueryText( null);
        }
    }
    
    public String getLogResult(ParserAnnotation annotation) {
    	String queryText = annotation.getQueryText();
    	if (queryText == null || queryText.length() == 0) {
    		return String.format("[%s] : %s\n", queryText,"包含解析不完善的词");
        } else {
        	return String.format("[%s] : %s\n",  queryText, "都是可解析的词");
        }
    }
}

