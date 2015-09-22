package com.myhexin.qparser.date.parser;

import com.myhexin.qparser.date.DatePatterns;
import com.myhexin.qparser.date.DateRange;
import com.myhexin.qparser.define.EnumDef.Unit;


// 处理分钟时间
public class ParseDateWithMunites implements Parser
{
	public DateRange doParse(String dateString, String backtestTime)
	{
		if (!DatePatterns.MUNITE.matcher(dateString).matches())
		{
			return null;
		}
		DateRange dRange = new DateRange();
		dRange.setDateUnit(Unit.MUNITE);
		return dRange;
	}
}
