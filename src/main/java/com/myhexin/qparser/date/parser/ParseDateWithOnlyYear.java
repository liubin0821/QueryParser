package com.myhexin.qparser.date.parser;

import java.util.regex.Matcher;

import com.myhexin.qparser.Param;
import com.myhexin.qparser.date.DateInfoNode;
import com.myhexin.qparser.date.DatePatterns;
import com.myhexin.qparser.date.DateRange;
import com.myhexin.qparser.define.EnumDef.Unit;



public class ParseDateWithOnlyYear implements Parser
{
	public DateRange doParse(String dateString, String backtestTime)
	{

		if (!DatePatterns.ONLY_YEAR.matcher(dateString).matches())
		{
			return null;
		}
		// 匹配只有年的日期，如“2009年”
		if (Param.DEBUG_DATEINFO)
		{
			System.out.println("ONLY_YEAR_NO6");
		}
		Matcher mid = DatePatterns.ONLY_YEAR.matcher(dateString);
		mid.matches();
		int year = Integer.valueOf(mid.group(1));
		DateInfoNode pointhead = new DateInfoNode(year, 1, 1);
		DateInfoNode pointend = new DateInfoNode(year, 12, 31);
		DateRange range = new DateRange(pointhead, pointend);
		range.setDateUnit(Unit.YEAR);
		range.setLength(1);
		return range;
	}
}
