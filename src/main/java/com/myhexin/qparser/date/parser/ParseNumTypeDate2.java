package com.myhexin.qparser.date.parser;

import java.util.regex.Matcher;

import com.myhexin.qparser.Param;
import com.myhexin.qparser.date.DateCompute;
import com.myhexin.qparser.date.DatePatterns;
import com.myhexin.qparser.date.DateRange;
import com.myhexin.qparser.date.DateUtil;
import com.myhexin.qparser.except.NotSupportedException;
import com.myhexin.qparser.except.UnexpectedException;


public class ParseNumTypeDate2 implements Parser
{
	public DateRange doParse(String dateString, String backtestTime) throws NotSupportedException,
			UnexpectedException
	{
		if (!DatePatterns.NUM_TYPE_NOT_RANGE_NO33_1.matcher(dateString)
				.matches())
		{
			return null;
		}
		if (Param.DEBUG_DATEINFO)
		{
			System.out.println("NUM_TYPE_NOT_RANGE_NO33_1");
		}
		Matcher mid1 = DatePatterns.NUM_TYPE_NOT_RANGE_NO33_1
				.matcher(dateString);
		mid1.matches();
		String changeStr = mid1.group(2);
		String dateUnit = mid1.group(3);
		String tagStr = mid1.group(1);
		DateRange range = DateUtil
				.dateSeveralDaysOrWeeksOrMonthOrYearsBeforeOrAfterNow(
						changeStr, dateUnit, tagStr, DateUtil.getNow(), false, backtestTime);
		return range;
	}
}
