package com.myhexin.qparser.date.parser;

import java.util.regex.Matcher;

import com.myhexin.qparser.Param;
import com.myhexin.qparser.date.DateCompute;
import com.myhexin.qparser.date.DatePatterns;
import com.myhexin.qparser.date.DateRange;
import com.myhexin.qparser.except.NotSupportedException;
import com.myhexin.qparser.number.NumUtil;


// 处理 连续XXX 变为近XXX
public class ParseSequenceDate implements Parser
{
	public DateRange doParse(String dateString, String backtestTime) throws NotSupportedException
	{
		if (!DatePatterns.SEQUENCE_SP.matcher(dateString).matches()
				&&!DatePatterns.SEQUENCE_SP2.matcher(dateString).matches())
		{
			return null;
		}
		if (Param.DEBUG_DATEINFO)
		{
			System.out.println("SEQUENCE_SP");
		}
		Matcher mid = DatePatterns.SEQUENCE_SP.matcher(dateString); 
		if(DatePatterns.SEQUENCE_SP2.matcher(dateString).matches()){
			mid = DatePatterns.SEQUENCE_SP2.matcher(dateString);
		}
		mid.matches();
		DateRange range = DateCompute.getDateInfoFromStr(NumUtil.getArabic("近"
				+ mid.group(1) + mid.group(2)), backtestTime);
		
		if(!range.getFrom().equals(range.getTo())){
			range.setIsLength(true);
			range.calculateLength();
		}
		
		return range;
	}
}
