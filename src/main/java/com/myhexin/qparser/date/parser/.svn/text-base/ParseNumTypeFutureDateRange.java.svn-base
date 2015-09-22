package com.myhexin.qparser.date.parser;

import java.util.regex.Matcher;

import com.myhexin.qparser.Param;
import com.myhexin.qparser.date.DateInfoNode;
import com.myhexin.qparser.date.DatePatterns;
import com.myhexin.qparser.date.DateRange;
import com.myhexin.qparser.date.DateUtil;
import com.myhexin.qparser.define.EnumDef.Unit;
import com.myhexin.qparser.except.UnexpectedException;


public class ParseNumTypeFutureDateRange implements Parser
{
	public DateRange doParse(String dateString, String backtestTime) throws UnexpectedException
	{
		if (!DatePatterns.NUM_TYPE_FUTURE_NO32.matcher(dateString).matches())
		{// new
			return null;
		}
		if (Param.DEBUG_DATEINFO)
		{
			System.out.println("NUM_TYPE_FUTURE_NO32");
		}
		Matcher mid = DatePatterns.NUM_TYPE_FUTURE_NO32.matcher(dateString);
		mid.matches();
		int change = Integer.valueOf(mid.group(1));
		Unit unit = null;
		int length = Integer.valueOf(mid.group(1));
		String tag = mid.group(2);
		String changeTag = null;
		if (tag.equals("年"))
		{
			unit = Unit.YEAR;
			changeTag = DateUtil.CHANGE_BY_YEAR;
		}
		else if (tag.matches("个?月"))
		{
			unit = Unit.MONTH;
			changeTag = DateUtil.CHANGE_BY_MONTH;
		}
		else if (tag.matches("季|个?季度"))
		{
			unit = Unit.QUARTER;
			changeTag = DateUtil.CHANGE_BY_MONTH;
			change = change * 3;
		}
		else if (tag.matches("日|天"))
		{
			unit = Unit.DAY;
			changeTag = DateUtil.CHANGE_BY_DAY;
		}
		else if (tag.matches("星期|礼拜|周"))
		{
			unit = Unit.WEEK;
			changeTag = DateUtil.CHANGE_BY_DAY;
			change = change * 7;
		}
		DateInfoNode pointhead = DateUtil.getNewDate(DateUtil.getNow(),
				DateUtil.CHANGE_BY_DAY, 1);
		DateInfoNode endTmp = DateUtil.getNewDate(pointhead, changeTag, change);
		DateInfoNode pointend = DateUtil.getNewDate(endTmp,
				DateUtil.CHANGE_BY_DAY, -1);
		DateRange range = new DateRange(pointhead, pointend);
		range.setIsLength(true);
		range.setDateUnit(unit);
		range.setLength(length);
		return range;
	}
}