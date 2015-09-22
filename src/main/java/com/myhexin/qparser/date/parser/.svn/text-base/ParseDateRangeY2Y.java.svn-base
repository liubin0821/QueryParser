package com.myhexin.qparser.date.parser;

import java.util.regex.Matcher;

import com.myhexin.qparser.Param;
import com.myhexin.qparser.date.DateInfoNode;
import com.myhexin.qparser.date.DatePatterns;
import com.myhexin.qparser.date.DateRange;
import com.myhexin.qparser.define.EnumDef.Unit;

public class ParseDateRangeY2Y implements Parser
{
	public DateRange doParse(String dateString, String backtestTime)
	{
		if (!DatePatterns.ONLY_YEAR_2_YEAR_NO16.matcher(dateString).matches())
		{
			return null;
		}
		if (Param.DEBUG_DATEINFO)
		{
			System.out.println("ONLY_YEAR_2_YEAR_NO16");
		}
		Matcher mid = DatePatterns.ONLY_YEAR_2_YEAR_NO16.matcher(dateString);
		mid.matches();
		int yearHead = Integer.valueOf(mid.group(2));
		int yearEnd = Integer.valueOf(mid.group(3));
		DateInfoNode pointhead = new DateInfoNode(yearHead, 1, 1);
		DateInfoNode pointend = new DateInfoNode(yearEnd, 12, 31);
		DateRange range = new DateRange(pointhead, pointend);
		range.setDateUnit(Unit.YEAR);
		
		if(!range.getFrom().equals(range.getTo())){
			range.setIsLength(true);
			range.calculateLength();
		}
		return range;
	}
}
