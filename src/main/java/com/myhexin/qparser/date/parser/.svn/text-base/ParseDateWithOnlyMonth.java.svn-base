package com.myhexin.qparser.date.parser;

import java.util.regex.Matcher;

import com.myhexin.qparser.Param;
import com.myhexin.qparser.date.DateInfoNode;
import com.myhexin.qparser.date.DatePatterns;
import com.myhexin.qparser.date.DateRange;
import com.myhexin.qparser.date.DateUtil;
import com.myhexin.qparser.define.EnumDef.Unit;


public class ParseDateWithOnlyMonth implements Parser
{
	public DateRange doParse(String dateString, String backtestTime)
	{
		if (!DatePatterns.ONLY_MONTH.matcher(dateString).matches())
		{
			return null;
		}
		// 匹配只有月份的日期，如“6月”
		// TODO:对未来时间的解析，现暂未作调整
		if (Param.DEBUG_DATEINFO)
		{
			System.out.println("ONLY_MONTH_NO7");
		}
		Matcher mid = DatePatterns.ONLY_MONTH.matcher(dateString);
		mid.matches();
		int month = Integer.valueOf(mid.group(1));
		// int year = DateUtil.getNow().getYear();
		int year = DateUtil.getNow().getMonth() < month
				&& DateUtil.getNow().getMonth() > 2 ? DateUtil.getNow()
				.getYear() - 1 : DateUtil.getNow().getYear();
		int day = DateUtil.getMonthDayCount(year, month);
		DateInfoNode pointhead = new DateInfoNode(year, month, 1);
		DateInfoNode pointend = new DateInfoNode(year, month, day);
		DateRange range = new DateRange(pointhead, pointend);
		range.setHasYear(false);
		range.setDateUnit(Unit.MONTH);
		range.setLength(1);
		return range;
	}
}
