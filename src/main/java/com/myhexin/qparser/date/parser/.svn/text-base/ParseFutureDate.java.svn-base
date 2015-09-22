package com.myhexin.qparser.date.parser;

import java.util.regex.Matcher;

import com.myhexin.qparser.Param;
import com.myhexin.qparser.date.DateCompute;
import com.myhexin.qparser.date.DatePatterns;
import com.myhexin.qparser.date.DateRange;
import com.myhexin.qparser.date.DateUtil;
import com.myhexin.qparser.define.MsgDef;
import com.myhexin.qparser.except.NotSupportedException;


public class ParseFutureDate implements Parser
{
	public DateRange doParse(String dateString, String backtestTime) throws NotSupportedException
	{
		if (!DatePatterns.FURURE_DATE.matcher(dateString).matches())
		{
			return null;
		}

		if (Param.DEBUG_DATEINFO)
		{
			System.out.println("FURURE_DATE");
		}
		Matcher mid = DatePatterns.FURURE_DATE.matcher(dateString);
		mid.matches();
		String dateStr = mid.group(1);
		DateRange dr;
		try
		{
			dr = DateCompute.getDateInfoFromStr(dateStr, backtestTime);
		} catch (NotSupportedException e)
		{
			throw new NotSupportedException(MsgDef.NOT_SUPPORTED_DATE_FMT,
					dateString);
		}
		if (dr == null)
			return null;
		
		dr.setDateRange(DateUtil.getNow(), dr.getTo());
		return dr;
	}
}
