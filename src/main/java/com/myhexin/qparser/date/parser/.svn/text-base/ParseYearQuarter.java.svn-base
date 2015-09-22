package com.myhexin.qparser.date.parser;

import java.util.regex.Matcher;

import com.myhexin.qparser.Param;
import com.myhexin.qparser.date.DateCompute;
import com.myhexin.qparser.date.DatePatterns;
import com.myhexin.qparser.date.DateRange;
import com.myhexin.qparser.date.DateUtil;
import com.myhexin.qparser.except.NotSupportedException;
import com.myhexin.qparser.number.NumUtil;



// XXXX年X季度
public class ParseYearQuarter implements Parser
{
	public DateRange doParse(String dateString, String backtestTime) throws NumberFormatException,
			NotSupportedException
	{
		Matcher mid = DatePatterns.YEAR_QUARTER.matcher(dateString);
		if (!mid.matches())
			return null;

		if (Param.DEBUG_DATEINFO)
			System.out.println("YEAR_QUARTER");

		int year = Integer.valueOf(mid.group(1));
		int quarter = Integer.parseInt(NumUtil.getArabic(mid.group(2)));

		// 不在此处年份进行调整，到后期再调整
		DateRange range = DateUtil.getQuarterWithYearNum(year, quarter);
		range.setCanBeAdjust(false);
		range.setLength(1);
		return range;
	}
}