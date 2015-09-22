package com.myhexin.qparser.date.parser;

import java.util.regex.Matcher;

import com.myhexin.qparser.Param;
import com.myhexin.qparser.date.DateInfoNode;
import com.myhexin.qparser.date.DatePatterns;
import com.myhexin.qparser.date.DateRange;
import com.myhexin.qparser.date.DateUtil;
import com.myhexin.qparser.define.EnumDef.Unit;
import com.myhexin.qparser.except.UnexpectedException;


public class ParseDateWithOnlyDay implements Parser
{
	public DateRange doParse(String dateString, String backtestTime) throws UnexpectedException
	{

		if (!DatePatterns.ONLY_DAY.matcher(dateString).matches())
		{
			return null;
		}
		// 匹配只有日的日期，如“30号”
		// TODO：若没到本月“30日”则向前推到上个月“6日”，若上个月无30日，则？
		if (Param.DEBUG_DATEINFO)
		{
			System.out.println("ONLY_DAY_N08");
		}
		Matcher mid = DatePatterns.ONLY_DAY.matcher(dateString);
		mid.matches();
		int day = Integer.valueOf(mid.group(1));
		DateInfoNode pointonly;
		if (day <= DateUtil.getNow().getDay())
		{
			pointonly = new DateInfoNode(DateUtil.getNow().getYear(), DateUtil
					.getNow().getMonth(), day);
		}
		else
		{
			DateInfoNode din = DateUtil.getNewDate(DateUtil.getNow(),
					DateUtil.CHANGE_BY_MONTH, -1);
			pointonly = new DateInfoNode(din.getYear(), din.getMonth(), day);
		}
		DateRange range = new DateRange(pointonly, pointonly);
		range.setHasYear(false);
		range.setCanBeAdjust(false);
		range.setDateUnit(Unit.DAY);
		range.setLength(1);
		return range;
	}
}
