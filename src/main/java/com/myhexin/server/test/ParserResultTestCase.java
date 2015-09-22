package com.myhexin.server.test;

import java.util.Calendar;
import java.util.List;

import com.myhexin.qparser.util.condition.ConditionParser;
import com.myhexin.server.Parser;
import com.myhexin.server.ParserItem;
import com.myhexin.server.test.QPTestSuite.TestVO;

public class ParserResultTestCase extends QPTestCaseAbstract{

	
	@Override
	public boolean test(TestVO vo) {
		//PhraseParser parser = PhraseParserFactory.createPhraseParser("phraseParser");
		
		//ParserAnnotation annotation = new ParserAnnotation();
    	//annotation.setQueryText(vo.query);
    	//annotation.setQueryType(Query.Type.valueOf(vo.qType) );
    	//Query q = new Query(vo.query, vo.qType);
    	//q.setDomain(null);
    	//annotation.setQuery(q);
		//annotation.setBacktestTime(backtestTime);
		
    	String backtestTimeStr = ConditionParser.getBackTestTimeFromJson(vo.postDataStr);
        Calendar backtestTime = ConditionParser.getBackTestTime(backtestTimeStr);
    	
    	ParserItem item = new ParserItem(vo.query, vo.qType, null, null);
    	item.setBacktestTime(backtestTime);
    	Parser.parserQuery(item, false); //String ret = 
    	List<String> reps = item.getStandardQueryList();
    	if(reps==null || reps.size()==0) {
    		errors.add(String.format("[%s] query=%s, error=%s",  ParserResultTestCase.class.getSimpleName() ,vo.query,"ParseResult empty"));
    	}else{
    		boolean match = false;
    		StringBuilder buf = new StringBuilder();
			for(String s : reps) {
				buf.append(s).append("|");
				if(s!=null && s.equals(vo.exp_result)) {
					match = true;
				}
			}
			if(match==false) {
				errors.add(String.format("[%s] query=%s, error=%s, \n\tpostDataStr=%s \n\trun_result=%s \n\texp_result=%s",  
						ParserResultTestCase.class.getSimpleName() ,vo.query ,"parse result doesn't match", vo.postDataStr, buf.toString(), vo.exp_result));
			}
			
			return match;
		}
    	
		return false;
	}
}
