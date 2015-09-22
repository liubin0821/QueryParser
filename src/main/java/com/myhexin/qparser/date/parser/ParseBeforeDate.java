package com.myhexin.qparser.date.parser;

import java.util.regex.Matcher;

import com.myhexin.qparser.Param;
import com.myhexin.qparser.date.DateCompute;
import com.myhexin.qparser.date.DateInfoNode;
import com.myhexin.qparser.date.DatePatterns;
import com.myhexin.qparser.date.DateRange;
import com.myhexin.qparser.date.DateUtil;
import com.myhexin.qparser.define.MsgDef;
import com.myhexin.qparser.define.EnumDef.Unit;
import com.myhexin.qparser.except.NotSupportedException;

public class ParseBeforeDate implements Parser
{
	public DateRange doParse(String dateString, String backtestTime) throws NotSupportedException
	{
		if (!DatePatterns.IN_ONE_DAY_BEFORE_NO43.matcher(dateString).matches())
		{
			return null;
		}
		if (Param.DEBUG_DATEINFO)
		{
			System.out.println("IN_ONE_DAY_BEFORE_NO43");
		}
		// 匹配某天之前，“2月以前”
		Matcher mid = DatePatterns.IN_ONE_DAY_BEFORE_NO43.matcher(dateString);
		mid.matches();
		String date = mid.group(1);
		DateRange range;
		try
		{
			if(DatePatterns.RANGE_DATE.matcher(date).matches()){
				range = DateCompute.getDateInfoFromStr("近"+date, backtestTime);
				range.setTo(range.getFrom().clone());
			}
			else
			    range = DateCompute.getDateInfoFromStr(date, backtestTime);
		} catch (NotSupportedException e)
		{
			throw new NotSupportedException(MsgDef.NOT_SUPPORTED_DATE_FMT,
					dateString);
		}
		if (range == null)
			return null;
		DateInfoNode to = range.getFrom();
		range.setDateRange(null, to);
		
		range.setDateUnit(Unit.DAY);
		range.setLength(DateUtil.daysBetween(range.getFrom(), range.getTo())+1);
		
		return range;
	}
}
