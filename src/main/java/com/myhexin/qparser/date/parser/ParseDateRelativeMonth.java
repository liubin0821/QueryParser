package com.myhexin.qparser.date.parser;

import java.util.regex.Matcher;

import com.myhexin.qparser.Param;
import com.myhexin.qparser.date.DateInfoNode;
import com.myhexin.qparser.date.DatePatterns;
import com.myhexin.qparser.date.DateRange;
import com.myhexin.qparser.date.DateUtil;
import com.myhexin.qparser.define.EnumDef.Unit;
import com.myhexin.qparser.except.UnexpectedException;


public class ParseDateRelativeMonth implements Parser
{
	public DateRange doParse(String dateString, String backtestTime) throws UnexpectedException
	{

		if (!DatePatterns.RELATIVE_MONTH_NO26.matcher(dateString).matches())
		{
			return null;
		}
		// 匹配相对月的日期，如“上个月”
		if (Param.DEBUG_DATEINFO)
		{
			System.out.println("RELATIVE_MONTH_NO26");
		}
		Matcher mid = DatePatterns.RELATIVE_MONTH_NO26.matcher(dateString);
		mid.matches();
		/*
		 * String tagStr = mid.group(1); boolean isThisMonth =
		 * tagStr.matches("本|这"); boolean isNextMonth = tagStr.equals("下");
		 * DateInfoNode nextMonth = DateUtil.getNewDate(DateUtil.getNow(),
		 * DateUtil.CHANGE_BY_MONTH, 1); DateInfoNode lastMonth =
		 * DateUtil.getNewDate(DateUtil.getNow(), DateUtil.CHANGE_BY_MONTH, -1);
		 * int year = isThisMonth ? DateUtil.getNow().getYear() : isNextMonth ?
		 * nextMonth .getYear() : lastMonth.getYear(); int month = isThisMonth ?
		 * DateUtil.getNow().getMonth() : isNextMonth ? nextMonth .getMonth() :
		 * lastMonth.getMonth();
		 */

		DateInfoNode theMonth = null;
		if (mid.group(1).equals("上"))
		{
			theMonth = DateUtil.getNewDate(DateUtil.getNow(),
					DateUtil.CHANGE_BY_MONTH, -1);
		}
		else if (mid.group(1).equals("上上"))
		{
			theMonth = DateUtil.getNewDate(DateUtil.getNow(),
					DateUtil.CHANGE_BY_MONTH, -2);
		}
		else if (mid.group(1).equals("下"))
		{
			theMonth = DateUtil.getNewDate(DateUtil.getNow(),
					DateUtil.CHANGE_BY_MONTH, 1);
		}
		else if (mid.group(1).equals("下下"))
		{
			theMonth = DateUtil.getNewDate(DateUtil.getNow(),
					DateUtil.CHANGE_BY_MONTH, 2);
		}
		else
		{
			theMonth = DateUtil.getNow();
		}
		int year = theMonth.getYear();
		int month = theMonth.getMonth();
		boolean isThisMonth = mid.group(1).matches("本|这");
		// 若为当月，则截止日期选取今天
		int day = isThisMonth ? DateUtil.getNow().getDay() : DateUtil
				.getMonthDayCount(year, month);
		DateInfoNode pointhead = new DateInfoNode(year, month, 1);
		DateInfoNode pointend = new DateInfoNode(year, month, day);
		DateRange range = new DateRange(pointhead, pointend);
		range.setDateUnit(Unit.MONTH);
		range.setLength(1);
		return range;
	}
}
