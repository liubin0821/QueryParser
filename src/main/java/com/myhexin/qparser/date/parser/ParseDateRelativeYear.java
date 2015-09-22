package com.myhexin.qparser.date.parser;

import java.util.regex.Matcher;

import com.myhexin.qparser.Param;
import com.myhexin.qparser.date.DateInfoNode;
import com.myhexin.qparser.date.DatePatterns;
import com.myhexin.qparser.date.DateRange;
import com.myhexin.qparser.date.DateUtil;
import com.myhexin.qparser.define.EnumDef.Unit;
import com.myhexin.qparser.except.UnexpectedException;


public class ParseDateRelativeYear implements Parser
{
	public DateRange doParse(String dateString, String backtestTime) throws UnexpectedException
	{

		if (!DatePatterns.RELATIVE_YEAR_NO25.matcher(dateString).matches())
		{
			return null;
		}
		// 匹配相对年的日期，如“上个年度”
		if (Param.DEBUG_DATEINFO)
		{
			System.out.println("RELATIVE_YEAR_NO25");
		}
		Matcher mid = DatePatterns.RELATIVE_YEAR_NO25.matcher(dateString);
		mid.matches();

		DateInfoNode theYear = null;
		if (mid.group(1).equals("上"))
		{
			theYear = DateUtil.getNewDate(DateUtil.getNow(),
					DateUtil.CHANGE_BY_YEAR, -1);
		}
		else if (mid.group(1).equals("上上"))
		{
			theYear = DateUtil.getNewDate(DateUtil.getNow(),
					DateUtil.CHANGE_BY_YEAR, -2);
		}
		else if (mid.group(1).equals("下"))
		{
			theYear = DateUtil.getNewDate(DateUtil.getNow(),
					DateUtil.CHANGE_BY_YEAR, 1);
		}
		else if (mid.group(1).equals("下下"))
		{
			theYear = DateUtil.getNewDate(DateUtil.getNow(),
					DateUtil.CHANGE_BY_YEAR, 2);
		}
		else
		{
			theYear = DateUtil.getNow();
		}
		int year = theYear.getYear();
		boolean isThisYear = mid.group(1).matches("本|这");
		// 若为当月，则截止日期选取今天
		int month = isThisYear ? DateUtil.getNow().getMonth() : 12;
		int day = isThisYear ? DateUtil.getNow().getDay() : 31;
		DateInfoNode pointhead = new DateInfoNode(year, 1, 1);
		DateInfoNode pointend = new DateInfoNode(year, month, day);
		DateRange range = new DateRange(pointhead, pointend);
		range.setDateUnit(Unit.YEAR);
		range.setLength(1);
		return range;
	}
}
