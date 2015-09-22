package com.myhexin.qparser.date.parser;

import java.util.regex.Matcher;

import com.myhexin.qparser.Param;
import com.myhexin.qparser.date.DateCompute;
import com.myhexin.qparser.date.DateInfoNode;
import com.myhexin.qparser.date.DatePatterns;
import com.myhexin.qparser.date.DateRange;
import com.myhexin.qparser.date.DateUtil;
import com.myhexin.qparser.define.EnumDef.Unit;
import com.myhexin.qparser.define.MsgDef;
import com.myhexin.qparser.except.NotSupportedException;


public class ParsePeriodOfTenDays implements Parser
{
	public DateRange doParse(String dateString, String backtestTime) throws NotSupportedException
	{
		if (!DatePatterns.A_PERIOD_OF_TEN_DAYS_NO39.matcher(dateString)
				.matches())
		{
			return null;
		}
		// 匹配表示旬的日期，如“2009年7月上旬”
		if (Param.DEBUG_DATEINFO)
		{
			System.out.println("A_PERIOD_OF_TEN_DAYS_NO39");
		}
		Matcher mid = DatePatterns.A_PERIOD_OF_TEN_DAYS_NO39
				.matcher(dateString);
		mid.matches();
		String dateStr = mid.group(1);
		boolean hasOldDate = dateStr.length() > 0;
		DateRange oldRange;
		try
		{
			oldRange = hasOldDate ? DateCompute.getDateInfoFromStr(dateStr, backtestTime)
					: null;
		} catch (NotSupportedException e)
		{
			throw new NotSupportedException(MsgDef.NOT_SUPPORTED_DATE_FMT,
					dateString);
		}
		String tag = mid.group(2);
		int year = oldRange == null ? DateUtil.getNow().getYear() : oldRange
				.getTo().getYear();
		int month = oldRange == null ? DateUtil.getNow().getMonth() : oldRange
				.getTo().getMonth();
		int day = DateUtil.getMonthDayCount(year, month);
		DateInfoNode pointhead = new DateInfoNode();
		DateInfoNode pointend = new DateInfoNode();
		if (tag.equals("上"))
		{
			pointhead.setDateInfo(year, month, 1);
			pointend.setDateInfo(year, month, 10);
		}
		else if (tag.equals("中"))
		{
			pointhead.setDateInfo(year, month, 11);
			pointend.setDateInfo(year, month, 20);
		}
		else
		{
			pointhead.setDateInfo(year, month, 21);
			pointend.setDateInfo(year, month, day);
		}
		DateRange range = new DateRange(pointhead, pointend);
		range.setHasYear(oldRange != null && oldRange.hasYear());
		range.setDateUnit(Unit.DAY);
		return range;
	}
}
