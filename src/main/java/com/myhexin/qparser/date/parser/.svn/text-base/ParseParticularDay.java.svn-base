package com.myhexin.qparser.date.parser;

import java.util.regex.Matcher;

import com.myhexin.qparser.Param;
import com.myhexin.qparser.date.DateCompute;
import com.myhexin.qparser.date.DateInfoNode;
import com.myhexin.qparser.date.DatePatterns;
import com.myhexin.qparser.date.DateRange;
import com.myhexin.qparser.define.EnumDef.Unit;
import com.myhexin.qparser.define.MsgDef;
import com.myhexin.qparser.except.NotSupportedException;


public class ParseParticularDay implements Parser
{
	public DateRange doParse(String dateString, String backtestTime) throws NotSupportedException
	{
		if (!DatePatterns.NOT_REGULAR_DAY_NO46.matcher(dateString).matches())
		{
			return null;
		}
		if (Param.DEBUG_DATEINFO)
		{
			System.out.println("NOT_REGULAR_DAY_NO45");
		}
		Matcher mid = DatePatterns.NOT_REGULAR_DAY_NO46.matcher(dateString);
		mid.matches();
		String dateFirstPartStr = mid.group(1);
		String dateSecPartStr = mid.group(2);
		int dateSec = Integer.valueOf(dateSecPartStr);
		DateRange drFirst;
		try
		{
			drFirst = DateCompute.getDateInfoFromStr(dateFirstPartStr, backtestTime);
		} catch (NotSupportedException e)
		{
			throw new NotSupportedException(MsgDef.NOT_SUPPORTED_DATE_FMT,
					dateString);
		}
		if (drFirst == null)
			return null;
		DateInfoNode pointonly = new DateInfoNode(drFirst.getFrom().getYear(),
				drFirst.getFrom().getMonth(), dateSec);
		DateRange range = new DateRange(pointonly, pointonly);
		range.setDateUnit(Unit.DAY);
		return range;
	}
}