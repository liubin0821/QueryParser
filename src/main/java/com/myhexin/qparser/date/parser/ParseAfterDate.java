package com.myhexin.qparser.date.parser;

import java.util.regex.Matcher;

import com.myhexin.qparser.Param;
import com.myhexin.qparser.date.DateCompute;
import com.myhexin.qparser.date.DateInfoNode;
import com.myhexin.qparser.date.DatePatterns;
import com.myhexin.qparser.date.DateRange;
import com.myhexin.qparser.date.DateUtil;
import com.myhexin.qparser.define.EnumDef.Unit;
import com.myhexin.qparser.define.MsgDef;
import com.myhexin.qparser.except.NotSupportedException;

// 某时间之前或之后或之间
public class ParseAfterDate implements Parser
{
	public DateRange doParse(String dateString, String backtestTime) throws NotSupportedException
	{
		if (!DatePatterns.IN_ONE_DAY_AFTER_NO42.matcher(dateString).matches())
		{
			return null;
		}
		if (Param.DEBUG_DATEINFO)
		{
			System.out.println("IN_ONE_DAY_AFTER_NO42");
		}
		// 匹配某天之后，“2月以后”
		Matcher mid = DatePatterns.IN_ONE_DAY_AFTER_NO42.matcher(dateString);
		mid.matches();
		String date = mid.group(1);

		DateRange range;
		try
		{
			//一个月后
			if(DatePatterns.RANGE_DATE.matcher(date).matches()){
				range = DateCompute.getDateInfoFromStr("未来"+date, backtestTime);
				range.setFrom(range.getTo().clone());
			}
			else{
				range = DateCompute.getDateInfoFromStr(date, backtestTime);
			}
		} catch (NotSupportedException e)
		{
			throw new NotSupportedException(MsgDef.NOT_SUPPORTED_DATE_FMT,
					dateString);
		}
		if (range == null)
			return null;
		DateInfoNode from = range.getFrom();
		if (DateUtil.isLatter(from, DateUtil.getNow()))
		{
			range.setDateRange(from, null);
		}
		else
		{
			range.setDateRange(from, DateUtil.getNow());
		}
		
		range.setDateUnit(Unit.DAY);
		range.setLength(DateUtil.daysBetween(range.getFrom(), range.getTo())+1);
		
		return range;
	}
}
