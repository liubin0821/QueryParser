package com.myhexin.qparser.date.parser;

import java.util.regex.Matcher;

import com.myhexin.qparser.Param;
import com.myhexin.qparser.date.DateInfoNode;
import com.myhexin.qparser.date.DatePatterns;
import com.myhexin.qparser.date.DateRange;
import com.myhexin.qparser.date.DateUtil;
import com.myhexin.qparser.define.EnumDef.Unit;


public class ParseDateWithMD implements Parser
{
	public DateRange doParse(String dateString, String backtestTime)
	{
		if (!DatePatterns.MONTH_DAY.matcher(dateString).matches())
		{
			return null;
		}
		// 匹配有月份，有几号，但无年份的日期，如“3月6日”
		if (Param.DEBUG_DATEINFO)
		{
			System.out.println("MONTH_DAY_NO5");
		}
		Matcher dateMatcher = DatePatterns.MONTH_DAY.matcher(dateString);
		dateMatcher.matches();
		int month = Integer.valueOf(dateMatcher.group(1));
		int day = Integer.valueOf(dateMatcher.group(2));
		// 若当前时间在1月，句中时间为10月以后，则用上一年的
		int year = DateUtil.getNow().getMonth() == 1 && month > 10 ? DateUtil
				.getNow().getYear() - 1 : DateUtil.getNow().getYear();
		DateInfoNode pointonly = new DateInfoNode(year, month, day);
		DateRange range = new DateRange(pointonly, pointonly);
		range.setHasYear(false);
		range.setCanBeAdjust(false);
		range.setDateUnit(Unit.DAY);
		range.setLength(1);
		return range;
	}
}