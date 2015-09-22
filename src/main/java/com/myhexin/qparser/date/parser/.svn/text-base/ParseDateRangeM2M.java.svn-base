package com.myhexin.qparser.date.parser;

import java.util.regex.Matcher;

import com.myhexin.qparser.Param;
import com.myhexin.qparser.date.DateInfoNode;
import com.myhexin.qparser.date.DatePatterns;
import com.myhexin.qparser.date.DateRange;
import com.myhexin.qparser.date.DateUtil;
import com.myhexin.qparser.define.EnumDef.Unit;

public class ParseDateRangeM2M implements Parser
{
	public DateRange doParse(String dateString, String backtestTime)
	{
		if (!DatePatterns.ONLY_MONTH_2_MONTH_NO17.matcher(dateString).matches())
		{
			return null;
		}
		if (Param.DEBUG_DATEINFO)
		{
			System.out.println("ONLY_MONTH_2_MONTH_NO17");
		}
		Matcher mid = DatePatterns.ONLY_MONTH_2_MONTH_NO17.matcher(dateString);
		mid.matches();
		DateInfoNode pointhead = new DateInfoNode(DateUtil.getNow().getYear(),
				Integer.valueOf(mid.group(2)), 1);
		DateInfoNode pointend = new DateInfoNode(DateUtil.getNow().getYear(),
				Integer.valueOf(mid.group(3)), DateUtil.getMonthDayCount(DateUtil
						.getNow().getYear(), Integer.valueOf(mid.group(3))));
		DateRange range = new DateRange(pointhead, pointend);
		range.setHasYear(false);
		range.setDateUnit(Unit.MONTH);
		
		if(!range.getFrom().equals(range.getTo())){
			range.setIsLength(true);
			range.calculateLength();
		}
		return range;
	}
}