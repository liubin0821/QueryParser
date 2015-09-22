package com.myhexin.qparser.date.parser;

import java.util.regex.Matcher;

import com.myhexin.qparser.Param;
import com.myhexin.qparser.date.DateCompute;
import com.myhexin.qparser.date.DatePatterns;
import com.myhexin.qparser.date.DateRange;
import com.myhexin.qparser.except.NotSupportedException;
import com.myhexin.qparser.number.NumUtil;


// 处理 连续XXX 变为近XXX
public class ParseSequenceInEnd implements Parser
{
	public DateRange doParse(String dateString, String backtestTime) throws NotSupportedException
	{
		if (!DatePatterns.SEQUENCE_IN_END.matcher(dateString).matches())
		{
			return null;
		}
		if (Param.DEBUG_DATEINFO)
		{
			System.out.println("SEQUENCE_IN_END");
		}
		Matcher mid = DatePatterns.SEQUENCE_IN_END.matcher(dateString);
		mid.matches();
		DateRange range = DateCompute.getDateInfoFromStr(NumUtil.getArabic(mid.group(1)), backtestTime);
		
		if(range==null)
			return null;
		
		if(!range.getFrom().equals(range.getTo())){
			range.setIsLength(true);
			range.calculateLength();
		}
		
		return range;
	}
}
