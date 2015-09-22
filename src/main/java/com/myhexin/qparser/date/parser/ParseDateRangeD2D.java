package com.myhexin.qparser.date.parser;

import java.util.regex.Matcher;

import com.myhexin.qparser.Param;
import com.myhexin.qparser.date.DateInfoNode;
import com.myhexin.qparser.date.DatePatterns;
import com.myhexin.qparser.date.DateRange;
import com.myhexin.qparser.date.DateUtil;
import com.myhexin.qparser.define.EnumDef.Unit;

public class ParseDateRangeD2D implements Parser
{
	public DateRange doParse(String dateString, String backtestTime)
	{
		if (!DatePatterns.ONLY_DAY_2_DAY_NO18.matcher(dateString).matches())
		{
			return null;
		}
		if (Param.DEBUG_DATEINFO)
		{
			System.out.println("ONLY_DAY_2_DAY_NO18");
		}
		// 现默认取当前月份
		Matcher mid = DatePatterns.ONLY_DAY_2_DAY_NO18.matcher(dateString);
		mid.matches();
		DateInfoNode pointhead = new DateInfoNode(DateUtil.getNow().getYear(),
				DateUtil.getNow().getMonth(), Integer.valueOf(mid.group(2)));
		DateInfoNode pointend = new DateInfoNode(DateUtil.getNow().getYear(),
				DateUtil.getNow().getMonth(), Integer.valueOf(mid.group(3)));
		DateRange range = new DateRange(pointhead, pointend);
		range.setHasYear(false);
		range.setDateUnit(Unit.DAY);
		
		if(!range.getFrom().equals(range.getTo())){
			range.setIsLength(true);
			range.calculateLength();
		}
		
		return range;
	}
}
