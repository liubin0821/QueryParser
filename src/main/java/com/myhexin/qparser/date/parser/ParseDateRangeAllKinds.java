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
import com.myhexin.qparser.define.OperDef;
import com.myhexin.qparser.except.NotSupportedException;

public class ParseDateRangeAllKinds implements Parser
{
	public DateRange doParse(String dateString, String backtestTime) throws NotSupportedException
	{
		if (!DatePatterns.FOR_ALL_KIND_FROM_TO_NO37.matcher(dateString)
				.matches())
		{
			return null;
		}
		if (Param.DEBUG_DATEINFO)
		{
			System.out.println("FOR_ALL_KIND_FROM_TO_NO37");
		}
		Matcher mid = DatePatterns.FOR_ALL_KIND_FROM_TO_NO37
				.matcher(dateString);
		mid.matches();
		String dateStr = mid.group(1);
		DateRange dr = null;
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
		if (dr.getRangeType() != OperDef.QP_EQ)
		{
			return dr;
		}
		DateInfoNode from = dr.getFrom();
		DateRange range = new DateRange(from, DateUtil.getNow());
		range.setIsLength(dr.isLength());
		range.setDateUnit(Unit.DAY);
		
		if(!range.getFrom().equals(range.getTo())){
			range.setIsLength(true);
			range.calculateLength();
		}
		return range;
	}

}
