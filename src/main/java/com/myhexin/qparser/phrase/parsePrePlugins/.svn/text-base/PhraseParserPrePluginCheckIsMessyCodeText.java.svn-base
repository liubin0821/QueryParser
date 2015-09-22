package com.myhexin.qparser.phrase.parsePrePlugins;


import com.myhexin.qparser.phrase.ParserAnnotation;
import com.myhexin.qparser.tool.encode.Encode;

/**
 * 检查问句看是不是乱码
 * 中文字符要占一半以上才合格
 * 
 * @author 刘小峰 liuxiaofeng@myhexin.com
 * @Version 创建时间 2015-2-3
 *
 */
public class PhraseParserPrePluginCheckIsMessyCodeText extends PhraseParserPrePluginAbstract{
	
	private final static String MessyCode_Y = "为乱码";
	private final static String MessyCode_N = "不是乱码";
	
    public PhraseParserPrePluginCheckIsMessyCodeText() {
        super("Check_Is_Messy_Code_Text");
    }
    
    @Override
    public void processSingle(ParserAnnotation annotation) {
    	String query = annotation.getQueryText();
    	if (Encode.isMessyCode(query)) { // 判断是否为乱码
    		annotation.setStopProcessFlag(true);
    		annotation.setQueryText( null);
        }
    }
    
    @Override
    public String getLogResult(ParserAnnotation annotation) {
    	String queryText = annotation.getQueryText();
    	if (queryText == null || queryText.length() == 0) {
    		return String.format("[%s] : %s\n", queryText, MessyCode_Y);
        } else {
        	return String.format("[%s] : %s\n", queryText, MessyCode_N);
        }
    }
}

