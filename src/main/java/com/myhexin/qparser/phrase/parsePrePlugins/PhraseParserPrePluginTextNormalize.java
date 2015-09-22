package com.myhexin.qparser.phrase.parsePrePlugins;


import com.myhexin.qparser.phrase.ParserAnnotation;
import com.myhexin.qparser.phrase.util.CommonUtil;
import com.myhexin.qparser.phrase.util.Traditional2simplified;

public class PhraseParserPrePluginTextNormalize extends PhraseParserPrePluginAbstract{
	
    public PhraseParserPrePluginTextNormalize() {
        super("Text_Normalize");
    }
    
    @Override
    public void processSingle(ParserAnnotation annotation) {
    	String query = annotation.getQueryText();
    	String temp = Traditional2simplified.toSimplified(query); // 繁体转简体
    	temp = CommonUtil.toLowerAndHalf(temp); // 全角转半角
    	if(temp!=null) {
    		temp = temp.trim();
    	}
    	if (temp != null && temp.length() > 0) {
    		annotation.setQueryText( temp);
    	}
    }
    
    public String getLogResult(ParserAnnotation annotation ) {
    	String queryText = annotation.getQueryText();
    	if(queryText!=null)
    		return String.format("[%s]\n", queryText);
    	else
    		return null;
    }
}

