package com.myhexin.qparser.date.parser;

import java.util.regex.Matcher;

import com.myhexin.qparser.Param;
import com.myhexin.qparser.date.DateInfoNode;
import com.myhexin.qparser.date.DatePatterns;
import com.myhexin.qparser.date.DateRange;
import com.myhexin.qparser.date.DateUtil;
import com.myhexin.qparser.define.EnumDef.Unit;

public class ParseDateRangeYM2M implements Parser
{
	public DateRange doParse(String dateString, String backtestTime)
	{
		if (!DatePatterns.YM_2_M_NO13.matcher(dateString).matches())
		{
			return null;
		}
		if (Param.DEBUG_DATEINFO)
		{
			System.out.println("YM_2_M_NO13");
		}
		Matcher mid = DatePatterns.YM_2_M_NO13.matcher(dateString);
		mid.matches();
		DateInfoNode pointhead = new DateInfoNode(
				Integer.valueOf(mid.group(2)), Integer.valueOf(mid.group(3)), 1);
		DateInfoNode pointend = new DateInfoNode(Integer.valueOf(mid.group(2)),
				Integer.valueOf(mid.group(4)), DateUtil.getMonthDayCount(
						Integer.valueOf(mid.group(2)),
						Integer.valueOf(mid.group(4))));
		DateRange range = new DateRange(pointhead, pointend);
		range.setDateUnit(Unit.MONTH);
		range.setCanBeAdjust(false);
		
		if(!range.getFrom().equals(range.getTo())){
			range.setIsLength(true);
			range.calculateLength();
		}
		return range;
	}
}
