package com.myhexin.qparser.date.parser;

import java.util.regex.Matcher;

import com.myhexin.qparser.Param;
import com.myhexin.qparser.date.DateCompute;
import com.myhexin.qparser.date.DatePatterns;
import com.myhexin.qparser.date.DateRange;
import com.myhexin.qparser.except.NotSupportedException;


// XXXX年()
public class ParseYearWithSomthing implements Parser
{
	public DateRange doParse(String dateString, String backtestTime) throws NotSupportedException
	{
		Matcher mid = DatePatterns.YEAR_WITH_SOMTHING.matcher(dateString);
		if (!mid.matches())
			return null;

		if (Param.DEBUG_DATEINFO)
			System.out.println("YEAR_WITH_SOMTHING");

		int year = Integer.valueOf(mid.group(1));
		String dateStr = mid.group(2);

		DateRange range = DateCompute.getDateInfoFromStr(dateStr, backtestTime);
		if (range == null)
			return null;
		range.getFrom().setYear(year);
		range.getTo().setYear(year);
		range.setCanBeAdjust(false);
		return range;
	}
}
