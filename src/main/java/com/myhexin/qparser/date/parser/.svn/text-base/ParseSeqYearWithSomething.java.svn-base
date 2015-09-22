package com.myhexin.qparser.date.parser;

import java.util.ArrayList;
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


public class ParseSeqYearWithSomething implements Parser
{
	public DateRange doParse(String dateString, String backtestTime) throws UnexpectedException,
			NotSupportedException
	{
		if (!DatePatterns.SEQ_YEAR_WITH_SOMETHING.matcher(dateString).matches())
		{
			return null;
		}
		if (Param.DEBUG_DATEINFO)
		{
			System.out.println("SEQ_YEAR_WITH_SOMETHING");
		}
		Matcher mid = DatePatterns.SEQ_YEAR_WITH_SOMETHING.matcher(dateString);
		mid.matches();
		String dateStr = mid.group(1);
		String quarterStr = mid.group(2);

		boolean hasOldDate = dateStr.length() > 0;
		DateRange oldRange, quarterRange = null;
		try
		{
			oldRange = hasOldDate ? DateCompute.getDateInfoFromStr(dateStr, backtestTime)
					: null;
			quarterRange = quarterStr != null ? DateCompute
					.getDateInfoFromStr(quarterStr, backtestTime) : null;
		} catch (NotSupportedException e)
		{
			throw new NotSupportedException(MsgDef.NOT_SUPPORTED_DATE_FMT,
					dateString);
		}

		if (oldRange == null || quarterRange == null)
			return null;

		// “连续三年1季度”这种的解析
		ArrayList<DateRange> splitYears = DateUtil.getSplitByRange(oldRange, 1,
				Unit.YEAR);
		ArrayList<DateRange> splitDates = DateUtil.getSplitDateByOtherDay(
				splitYears, quarterRange, Unit.MONTH);
		// getSplitDateForReportOrQuarter(splitYears, quarter, false);
		DateInfoNode from = splitDates.get(0).getFrom();
		DateInfoNode to = splitDates.get(splitDates.size() - 1).getTo();
		DateRange range = new DateRange(from, to);
		range.setDateUnit(Unit.YEAR);
		range.setDisperseDates(splitDates);
		range.setHasYear(true);
		range.setCanBeAdjust(false);
		range.setLength(splitDates.size());
		return range;
	}
}
