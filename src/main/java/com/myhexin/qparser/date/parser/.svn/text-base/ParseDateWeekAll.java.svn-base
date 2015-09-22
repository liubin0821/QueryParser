package com.myhexin.qparser.date.parser;

import java.util.regex.Matcher;

import com.myhexin.qparser.Param;
import com.myhexin.qparser.date.DateInfoNode;
import com.myhexin.qparser.date.DatePatterns;
import com.myhexin.qparser.date.DateRange;
import com.myhexin.qparser.date.DateUtil;
import com.myhexin.qparser.define.EnumDef.Unit;
import com.myhexin.qparser.except.UnexpectedException;


public class ParseDateWeekAll implements Parser
{
	public DateRange doParse(String dateString, String backtestTime) throws UnexpectedException
	{
		if (!DatePatterns.WEEK_ALL_NO30.matcher(dateString).matches())
		{
			return null;
		}
		if (Param.DEBUG_DATEINFO)
		{
			System.out.println("WEEK_ALL");
		}
		int daychange;
		Matcher mid = DatePatterns.WEEK_ALL_NO30.matcher(dateString);
		mid.matches();
		String newstr;
		newstr = mid.group(3).replace("一", "1");
		newstr = newstr.replace("二", "2");
		newstr = newstr.replace("三", "3");
		newstr = newstr.replace("四", "4");
		newstr = newstr.replace("五", "5");
		newstr = newstr.replace("六", "6");
		newstr = newstr.replace("日", "7");
		newstr = newstr.replace("天", "7");
		if (mid.group(1).equals("上"))
		{
			daychange = Integer.valueOf(newstr) - DateUtil.getNow().getWeek()
					- 7;
		}
		else if (mid.group(1).equals("下"))
		{
			daychange = 7 - DateUtil.getNow().getWeek()
					+ Integer.valueOf(newstr);
		}
		else
		{
			daychange = Integer.valueOf(newstr) - DateUtil.getNow().getWeek();
		}
		DateInfoNode pointonly = DateUtil.getNewDate(DateUtil.getNow(),
				DateUtil.CHANGE_BY_DAY, daychange);
		if (mid.group(1).length() == 0
				&& DateUtil.isLatter(pointonly, DateUtil.getNow()))
		{
			pointonly = DateUtil.getNewDate(pointonly, DateUtil.CHANGE_BY_DAY,
					-7);
		}
		DateRange range = new DateRange(pointonly, pointonly);
		range.setDateUnit(Unit.DAY);
		range.setLength(1);
		return range;
	}
}
