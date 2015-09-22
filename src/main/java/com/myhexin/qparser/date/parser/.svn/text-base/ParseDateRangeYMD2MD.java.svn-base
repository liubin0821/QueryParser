package com.myhexin.qparser.date.parser;

import java.util.regex.Matcher;

import com.myhexin.qparser.Param;
import com.myhexin.qparser.date.DateInfoNode;
import com.myhexin.qparser.date.DatePatterns;
import com.myhexin.qparser.date.DateRange;
import com.myhexin.qparser.define.EnumDef.Unit;
import com.myhexin.qparser.define.MsgDef;
import com.myhexin.qparser.except.NotSupportedException;
import com.myhexin.qparser.except.UnexpectedException;
import com.myhexin.qparser.number.NumUtil;


public class ParseDateRangeYMD2MD implements Parser
{
	public DateRange doParse(String dateString, String backtestTime) throws UnexpectedException,
			NotSupportedException
	{
		if (!DatePatterns.YMD_2MD_NO10.matcher(dateString).matches())
		{
			return null;
		}
		if (Param.DEBUG_DATEINFO)
		{
			System.out.println("YMD_2MD_NO10");
		}
		Matcher mid = DatePatterns.YMD_2MD_NO10.matcher(dateString);
		mid.matches();
		if (!NumUtil.checkAsYMD(mid.group(2), mid.group(3), mid.group(4))
				|| !NumUtil
						.checkAsYMD(mid.group(2), mid.group(5), mid.group(6)))
		{
			throw new NotSupportedException(MsgDef.NOT_SUPPORTED_DATE_FMT,
					dateString);
		}
		DateInfoNode pointhead = new DateInfoNode(
				Integer.valueOf(mid.group(2)), Integer.valueOf(mid.group(3)),
				Integer.valueOf(mid.group(4)));
		DateInfoNode pointend = new DateInfoNode(Integer.valueOf(mid.group(2)),
				Integer.valueOf(mid.group(5)), Integer.valueOf(mid.group(6)));
		DateRange range = new DateRange(pointhead, pointend);
		range.setDateUnit(Unit.DAY);
		range.setCanBeAdjust(false);
		
		if(!range.getFrom().equals(range.getTo())){
			range.setIsLength(true);
			range.calculateLength();
		}
		return range;
	}
}
