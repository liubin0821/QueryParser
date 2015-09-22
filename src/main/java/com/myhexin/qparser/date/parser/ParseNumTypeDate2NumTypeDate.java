package com.myhexin.qparser.date.parser;

import java.util.regex.Matcher;

import com.myhexin.qparser.Param;
import com.myhexin.qparser.date.DateInfoNode;
import com.myhexin.qparser.date.DatePatterns;
import com.myhexin.qparser.date.DateRange;
import com.myhexin.qparser.date.DateUtil;
import com.myhexin.qparser.define.EnumDef.Unit;
import com.myhexin.qparser.except.UnexpectedException;


public class ParseNumTypeDate2NumTypeDate implements Parser
{
	public DateRange doParse(String dateString, String backtestTime) throws UnexpectedException
	{
		if (!DatePatterns.NUM_TYPE_2_NUM_TYPE_NO34.matcher(dateString)
				.matches())
		{
			return null;
		}
		if (Param.DEBUG_DATEINFO)
		{
			System.out.println("NUM_TYPE_2_NUM_TYPE");
		}
		Matcher mid = DatePatterns.NUM_TYPE_2_NUM_TYPE_NO34.matcher(dateString);
		mid.matches();
		int first = Integer.valueOf(mid.group(1));
		int sec = Integer.valueOf(mid.group(2));
		int change = sec;
		Unit unit = null;
		if (first > sec)
		{
			change = first;
		}
		String unitStr = mid.group(3);
		String tag = mid.group(4);
		if (tag.matches("^以来|来|$"))
		{
			change = -change;
		}
		String unittag = null;
		if (unitStr.equals("年"))
		{
			unit = Unit.YEAR;
			unittag = DateUtil.CHANGE_BY_YEAR;
		}
		else if (unitStr.matches("个月"))
		{
			unit = Unit.MONTH;
			unittag = DateUtil.CHANGE_BY_MONTH;
		}
		else if (unitStr.matches("个?季度"))
		{
			unit = Unit.QUARTER;
			unittag = DateUtil.CHANGE_BY_MONTH;
			change = change * 3;
		}
		else if (unitStr.matches("周|个?礼拜|星期"))
		{
			unit = Unit.WEEK;
			unittag = DateUtil.CHANGE_BY_DAY;
			change = change * 7;
		}
		else
		{
			unit = Unit.DAY;
			unittag = DateUtil.CHANGE_BY_DAY;
		}
		DateInfoNode pointhead = DateUtil.getNow();
		DateInfoNode pointend = DateUtil.getNewDate(DateUtil.getNow(), unittag,
				change);
		if (tag.matches("^以来|来|$"))
		{
			pointend = DateUtil.getNewDate(pointend, DateUtil.CHANGE_BY_DAY, 1);
		}
		else
		{
			pointend = DateUtil
					.getNewDate(pointend, DateUtil.CHANGE_BY_DAY, -1);
		}
		DateRange range = new DateRange(pointhead, pointend);
		range.setIsLength(true);
		range.setDateUnit(unit);
		
		if(!range.getFrom().equals(range.getTo())){
			range.setIsLength(true);
			range.calculateLength();
		}
		return range;
	}
}
