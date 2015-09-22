package com.myhexin.qparser.date.parser;

import java.util.regex.Matcher;

import com.myhexin.qparser.Param;
import com.myhexin.qparser.date.DateCompute;
import com.myhexin.qparser.date.DatePatterns;
import com.myhexin.qparser.date.DateRange;
import com.myhexin.qparser.date.DateUtil;
import com.myhexin.qparser.define.MsgDef;
import com.myhexin.qparser.define.EnumDef.Unit;
import com.myhexin.qparser.except.NotSupportedException;

public class ParseBetweenDate implements Parser
{
	public DateRange doParse(String dateString, String backtestTime) throws NotSupportedException
	{
		if (!DatePatterns.IN_A_DATE_RANGE_NO44.matcher(dateString).matches())
		{
			return null;
		}
		if (Param.DEBUG_DATEINFO)
		{
			System.out.println("IN_A_DATE_RANGE_NO44");
		}
		Matcher mid = DatePatterns.IN_A_DATE_RANGE_NO44.matcher(dateString);
		mid.matches();
		String dateStr = mid.group(1);
		DateRange range;
		try
		{
			range = DateCompute.getDateInfoFromStr(dateStr, backtestTime);
			
			//一个月内 等 单位都变成day 
			if (range != null) {
				range.setLength(DateUtil.daysBetween(range.getFrom(), range.getTo())+1);
				range.setDateUnit(Unit.DAY);
			}
			
		} catch (NotSupportedException e)
		{
			throw new NotSupportedException(MsgDef.NOT_SUPPORTED_DATE_FMT,
					dateString);
		}
		return range;
	}
}
