package com.myhexin.qparser.date.parser;

import java.util.regex.Matcher;

import com.myhexin.qparser.Param;
import com.myhexin.qparser.date.DateInfoNode;
import com.myhexin.qparser.date.DatePatterns;
import com.myhexin.qparser.date.DateRange;
import com.myhexin.qparser.date.DateUtil;
import com.myhexin.qparser.define.EnumDef.Unit;
import com.myhexin.qparser.except.UnexpectedException;



// 解析“周”相关时间
public class ParseDateWeekEnd implements Parser
{
	public DateRange doParse(String dateString, String backtestTime) throws UnexpectedException
	{
		if (!DatePatterns.WEEK_END_NO29.matcher(dateString).matches())
		{
			return null;
		}
		if (Param.DEBUG_DATEINFO)
		{
			System.out.println("WEEK_END");
		}
		Matcher mid = DatePatterns.WEEK_END_NO29.matcher(dateString);
		mid.matches();
		int daychange;
		DateInfoNode pointhead;
		DateInfoNode pointend;
		if (mid.group(1).equals("上"))
		{
			daychange = 6 - DateUtil.getNow().getWeek() - 7;
			pointhead = DateUtil.getNewDate(DateUtil.getNow(),
					DateUtil.CHANGE_BY_DAY, daychange);
			daychange = -DateUtil.getNow().getWeek();
			pointend = DateUtil.getNewDate(DateUtil.getNow(),
					DateUtil.CHANGE_BY_DAY, daychange);
		}
		else if (mid.group(1).equals("下"))
		{
			daychange = 7 - DateUtil.getNow().getWeek() + 6;
			pointhead = DateUtil.getNewDate(DateUtil.getNow(),
					DateUtil.CHANGE_BY_DAY, daychange);
			daychange = 14 - DateUtil.getNow().getWeek();
			pointend = DateUtil.getNewDate(DateUtil.getNow(),
					DateUtil.CHANGE_BY_DAY, daychange);
		}
		else
		{
			daychange = 6 - DateUtil.getNow().getWeek();
			pointhead = DateUtil.getNewDate(DateUtil.getNow(),
					DateUtil.CHANGE_BY_DAY, daychange);
			daychange = 7 - DateUtil.getNow().getWeek();
			pointend = DateUtil.getNewDate(DateUtil.getNow(),
					DateUtil.CHANGE_BY_DAY, daychange);
		}
		DateRange range = new DateRange(pointhead, pointend);
		range.setDateUnit(Unit.DAY);
		range.setLength(2);
		return range;
	}
}
