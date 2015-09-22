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
import com.myhexin.qparser.except.UnexpectedException;

public class ParseDateAfterLength2 implements Parser
{
	public DateRange doParse(String dateString, String backtestTime) throws NotSupportedException
	{
		if (!DatePatterns.IN_ONE_DAY_AFTER_LENGTH2.matcher(dateString).matches())
		{
			return null;
		}
		if (Param.DEBUG_DATEINFO)
		{
			System.out.println("IN_ONE_DAY_AFTER_LENGTH2");
		}
		// 匹配某天之后，“2月以后”
		Matcher mid = DatePatterns.IN_ONE_DAY_AFTER_LENGTH2.matcher(dateString);
		mid.matches();
		String date1 = mid.group(1);
		String date2 = mid.group(2);

		date2 = "连续" + date2;
		// 连续3季度变为近三季度
		if (!DatePatterns.SEQUENCE_SP.matcher(date2).matches())
		{
			return null;
		}

		DateRange range1;
		DateRange range2;
		try
		{
			range1 = DateCompute.getDateInfoFromStr(date1, backtestTime);
			range2 = DateCompute.getDateInfoFromStr(date2, backtestTime);
		} catch (NotSupportedException e)
		{
			throw new NotSupportedException(MsgDef.NOT_SUPPORTED_DATE_FMT,
					dateString);
		}
		if (range1 == null || range2 == null)
			return null;

		DateInfoNode from = range1.getFrom();
		DateInfoNode to = range1.getTo();
		int length = range2.getLength();
		
		try
		{
			from = DateUtil.getNewDate(from,range2.getDateUnit(),length);
			to = DateUtil.getNewDate(to, range2.getDateUnit(), length);
		} catch (UnexpectedException e)
		{
			throw new NotSupportedException(e.getMessage());
		}
		range1.setDateRange(from, to);
		return range1;
	}
}
