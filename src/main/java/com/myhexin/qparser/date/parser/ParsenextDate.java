package com.myhexin.qparser.date.parser;

import java.util.regex.Matcher;

import com.myhexin.qparser.Param;
import com.myhexin.qparser.date.DateCompute;
import com.myhexin.qparser.date.DatePatterns;
import com.myhexin.qparser.date.DateRange;
import com.myhexin.qparser.except.NotSupportedException;


// 次日
public class ParsenextDate implements Parser
{
	public DateRange doParse(String dateString, String backtestTime) throws NotSupportedException
	{
		if (!DatePatterns.NEXT_DATE.matcher(dateString).matches())
		{
			return null;
		}
		if (Param.DEBUG_DATEINFO)
		{
			System.out.println("NEXT_DATE");
		}
		Matcher mid = DatePatterns.NEXT_DATE.matcher(dateString);
		mid.matches();
		String unit = mid.group(1);// 单位一定不为空 否则不匹配SINGLE_DATE
		return DateCompute.getDateInfoFromStr("明" + unit, backtestTime);
	}
}
