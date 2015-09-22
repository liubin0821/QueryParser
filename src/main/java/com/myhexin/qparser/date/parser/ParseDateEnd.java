package com.myhexin.qparser.date.parser;

import java.util.regex.Matcher;

import com.myhexin.qparser.Param;
import com.myhexin.qparser.date.DateCompute;
import com.myhexin.qparser.date.DatePatterns;
import com.myhexin.qparser.date.DateRange;
import com.myhexin.qparser.date.DateUtil;
import com.myhexin.qparser.except.NotSupportedException;
import com.myhexin.qparser.number.NumUtil;



// XXXX年X季度
public class ParseDateEnd implements Parser
{
	public DateRange doParse(String dateString, String backtestTime) throws NumberFormatException,
			NotSupportedException
	{
		Matcher match = DatePatterns.DATE_END.matcher(dateString);
		if (!match.matches())
			return null;

		if (Param.DEBUG_DATEINFO)
			System.out.println("DATE_END");

		String date1 = match.group(1);
		DateRange range = DateCompute.getDateInfoFromStr(date1, backtestTime);
		if(range==null) return null;
		
		range.setFrom(range.getTo());
		return range;
	}
}