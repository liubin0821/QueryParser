package com.myhexin.qparser.date.parser;

import java.util.regex.Matcher;

import com.myhexin.qparser.Param;
import com.myhexin.qparser.date.DateInfoNode;
import com.myhexin.qparser.date.DatePatterns;
import com.myhexin.qparser.date.DateRange;
import com.myhexin.qparser.date.DateUtil;
import com.myhexin.qparser.define.EnumDef.Unit;
import com.myhexin.qparser.except.UnexpectedException;


public class ParseDateRelativeWeek implements Parser
{
	public DateRange doParse(String dateString, String backtestTime) throws UnexpectedException
	{
		if (!DatePatterns.RELATIVE_WEEK_NO28.matcher(dateString).matches())
		{
			return null;
		}
		// 处理“上周|下周|本周”
		// 此处，每周的开头为周日，结束为周六
		if (Param.DEBUG_DATEINFO)
		{
			System.out.println("RELATIVE_WEEK");
		}
		Matcher mid = DatePatterns.RELATIVE_WEEK_NO28.matcher(dateString);
		mid.matches();
		DateInfoNode pointend;
		if (mid.group(1).equals("上"))
		{
			pointend = DateUtil.getNow().getLastWeekEnd();
		}
		else if (mid.group(1).equals("上上"))
		{
			pointend = DateUtil.getNow().getLastWeekEnd().getLastWeekEnd();
		}
		else if (mid.group(1).equals("下"))
		{
			pointend = DateUtil.getNow().getNextWeekEnd();
		}
		else if (mid.group(1).equals("下下"))
		{
			pointend = DateUtil.getNow().getNextWeekEnd().getNextWeekEnd();
		}
		else
		{
			pointend = DateUtil.getNow().getWeekEnd();
		}
		DateInfoNode pointhead = pointend.getWeekStart();
		DateRange range = new DateRange(pointhead, pointend);
		range.setDateUnit(Unit.WEEK);
		range.setLength(1);
		return range;
	}
}
