package com.myhexin.qparser.date.parser;

import java.util.regex.Matcher;

import com.myhexin.qparser.Param;
import com.myhexin.qparser.date.DateInfoNode;
import com.myhexin.qparser.date.DatePatterns;
import com.myhexin.qparser.date.DateRange;
import com.myhexin.qparser.date.DateUtil;
import com.myhexin.qparser.define.EnumDef.Unit;
import com.myhexin.qparser.except.UnexpectedException;


public class ParseDateRelativeQuarter implements Parser
{
	public DateRange doParse(String dateString, String backtestTime) throws UnexpectedException
	{
		if (!DatePatterns.RELATIVE_QUARTER_NO27.matcher(dateString).matches())
		{
			return null;
		}
		// 匹配相对季度的日期，如“上季度”
		if (Param.DEBUG_DATEINFO)
		{
			System.out.println("RELATIVE_QUARTER_NO27");
		}
		Matcher mid = DatePatterns.RELATIVE_QUARTER_NO27.matcher(dateString);
		mid.matches();
		/*
		 * String tagStr = mid.group(1); boolean isThisQuarter =
		 * tagStr.matches("本|这"); boolean isNextQuarter = tagStr.equals("下");
		 * DateInfoNode dateTmp = isThisQuarter ? DateUtil.getNow() :
		 * isNextQuarter ? DateUtil .getNewDate(DateUtil.getNow(),
		 * DateUtil.CHANGE_BY_MONTH, 3) : DateUtil
		 * .getNewDate(DateUtil.getNow(), DateUtil.CHANGE_BY_MONTH, -3);
		 */
		DateInfoNode dateTmp = null;
		if (mid.group(1).equals("上"))
		{
			dateTmp = DateUtil.getNewDate(DateUtil.getNow(),
					DateUtil.CHANGE_BY_MONTH, -3);
		}
		else if (mid.group(1).equals("上上"))
		{
			dateTmp = DateUtil.getNewDate(DateUtil.getNow(),
					DateUtil.CHANGE_BY_MONTH, -6);
		}
		else if (mid.group(1).equals("下"))
		{
			dateTmp = DateUtil.getNewDate(DateUtil.getNow(),
					DateUtil.CHANGE_BY_MONTH, 3);
		}
		else if (mid.group(1).equals("下下"))
		{
			dateTmp = DateUtil.getNewDate(DateUtil.getNow(),
					DateUtil.CHANGE_BY_MONTH, 6);
		}
		else
		{
			dateTmp = DateUtil.getNow();
		}
		int year = dateTmp.getYear();
		int seasonstart = DateUtil.getQuarterStart(dateTmp.getQuarter());
		int seasonend = DateUtil.getQuarterEnd(dateTmp.getQuarter());
		int monthday = DateUtil.getMonthDayCount(year, seasonend);
		DateInfoNode pointhead = new DateInfoNode(year, seasonstart, 1);
		DateInfoNode pointend = new DateInfoNode(year, seasonend, monthday);
		DateRange range = new DateRange(pointhead, pointend);
		range.setDateUnit(Unit.QUARTER);
		range.setLength(1);
		return range;
	}
}