package com.myhexin.qparser.phrase;

import com.myhexin.qparser.ApplicationContext.ApplicationContextHelper;

public class PhraseParserFactory {
	public static org.slf4j.Logger logger_ = org.slf4j.LoggerFactory.getLogger(PhraseParserFactory.class.getName());
	
	public final static String ParserName_Condition = "conditionPhraseParser";
	public final static String split_ = "_&_";
	/*private static Map<String, PhraseParser> parserMap = new HashMap<String, PhraseParser>();
	static {
		PhraseParser parser = (PhraseParser) ApplicationContextHelper.getBean(ParserName_Condition);
		if(parser==null) {
			parserMap.put(ParserName_Condition, arg1);
		}
	}*/
	
	public static PhraseParser createPhraseParser(String parserName) {
		PhraseParser parser = (PhraseParser) ApplicationContextHelper.getBean(parserName);
		if(parser==null) {
			logger_.warn("CAN NOT FIND phraseParser: " + parserName);
			parser = (PhraseParser) ApplicationContextHelper.getBean(ParserName_Condition); 
		}
		if(parser!=null)
			parser.setSplitWords(split_);
		
		return parser;
	}
	
}
