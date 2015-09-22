package com.myhexin.qparser.date.parser;

import com.myhexin.qparser.Param;
import com.myhexin.qparser.date.DateCompute;
import com.myhexin.qparser.date.DatePatterns;
import com.myhexin.qparser.date.DateRange;
import com.myhexin.qparser.define.EnumDef.Unit;
import com.myhexin.qparser.define.MsgDef;
import com.myhexin.qparser.except.NotSupportedException;


public class ParseDateTillNow implements Parser
{
	public DateRange doParse(String dateString, String backtestTime) throws NotSupportedException
	{
		if (!DatePatterns.TILL_NOW.matcher(dateString).matches())
		{
			return null;
		}
		if (Param.DEBUG_DATEINFO)
		{
			System.out.println("TILL_NOW");
		}
		DateRange dr;
		try
		{
			dr = DateCompute.getDateInfoFromStr("今天之前", backtestTime);
		} catch (NotSupportedException e)
		{
			throw new NotSupportedException(MsgDef.NOT_SUPPORTED_DATE_FMT,
					dateString);
		}
		if (dr == null)
			return null;
		dr.setDateUnit(Unit.DAY);
		
		
		if(!dr.getFrom().equals(dr.getTo())){
			dr.setIsLength(true);
			dr.calculateLength();
		}
		return dr;
	}
}
