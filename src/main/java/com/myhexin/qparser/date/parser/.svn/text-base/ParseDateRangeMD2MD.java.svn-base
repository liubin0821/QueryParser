package com.myhexin.qparser.date.parser;

import java.util.regex.Matcher;

import com.myhexin.qparser.Param;
import com.myhexin.qparser.date.DateInfoNode;
import com.myhexin.qparser.date.DatePatterns;
import com.myhexin.qparser.date.DateRange;
import com.myhexin.qparser.date.DateUtil;
import com.myhexin.qparser.define.EnumDef.Unit;

public class ParseDateRangeMD2MD implements Parser
{
	public DateRange doParse(String dateString, String backtestTime)
	{
		if (!DatePatterns.MD_2_MD_NO14.matcher(dateString).matches())
		{
			return null;
		}
		if (Param.DEBUG_DATEINFO)
		{
			System.out.println("MD_2_MD_NO14");
		}
		Matcher mid = DatePatterns.MD_2_MD_NO14.matcher(dateString);
		mid.matches();
		DateInfoNode pointhead = new DateInfoNode();
		pointhead.setDateInfo(DateUtil.getNow().getYear(),
				Integer.valueOf(mid.group(1)), Integer.valueOf(mid.group(2)));
		DateInfoNode pointend = new DateInfoNode();
		pointend.setDateInfo(DateUtil.getNow().getYear(),
				Integer.valueOf(mid.group(3)), Integer.valueOf(mid.group(4)));
		DateRange range = new DateRange(pointhead, pointend);
		range.setHasYear(false);
		range.setDateUnit(Unit.DAY);
		range.setCanBeAdjust(false);
		
		if(!range.getFrom().equals(range.getTo())){
			range.setIsLength(true);
			range.calculateLength();
		}
		return range;
	}
}
