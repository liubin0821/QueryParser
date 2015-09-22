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
import com.myhexin.qparser.define.OperDef;
import com.myhexin.qparser.except.NotSupportedException;
import com.myhexin.qparser.except.UnexpectedException;




public class ParseReportDate implements Parser
{
	public DateRange doParse(String dateString, String backtestTime) throws NotSupportedException,
			UnexpectedException
	{
		if (!DatePatterns.REPORT_NO41.matcher(dateString).matches())
		{
			return null;
		}
		if (Param.DEBUG_DATEINFO)
		{
			System.out.println("REPORT_NO41");
		}
		Matcher mid = DatePatterns.REPORT_NO41.matcher(dateString);
		mid.matches();
		String tag = mid.group(2);
		if (tag.startsWith("第"))
		{
			tag = tag.substring(1);
		}
		int report = tag.matches("1季报") ? 1 : tag.matches("中报|2季报|半年|半年报") ? 2
				: tag.matches("3季报") ? 3 : tag.matches("^季报$") ? DateUtil
						.getNow().getQuarter() - 1 : 4;
		Unit unit = tag.matches("年报") ? Unit.YEAR : Unit.QUARTER;
		DateRange oldDate = null;
		try
		{
			oldDate = mid.group(1).length() > 0 ? DateCompute
					.getDateInfoFromStr(mid.group(1), backtestTime) : null;
		} catch (NotSupportedException e)
		{
			String errMsg = String.format(MsgDef.NOT_SUPPORTED_DATE_FMT,
					dateString);
			throw new NotSupportedException(errMsg);
		}

		boolean needSplit = oldDate != null && oldDate.countYear() > 0;
		if (needSplit)
		{
			// “连续三年中报”这种的解析
			ArrayList<DateRange> splitYears = DateUtil.getSplitByRange(oldDate,
					1, Unit.YEAR);
			ArrayList<DateRange> splitDates = DateUtil
					.getSplitDateForReportOrQuarter(splitYears, report, true);
			DateInfoNode from = unit == Unit.YEAR ? splitDates.get(0).getFrom()
					.getYearStart() : splitDates.get(0).getFrom()
					.getQuarterStart();
			DateInfoNode to = splitDates.get(splitDates.size() - 1).getTo();
			DateRange range = new DateRange(from, to);
			range.setDisperseDates(splitDates);
			range.setDateUnit(unit);
			range.setHasYear(true);
			range.setCanBeAdjust(false);
			range.setIsReport(true);
			range.setLength(splitDates.size());
			return range;
		}

		int year = oldDate == null ? DateUtil.getNow().getYear() : oldDate
				.getRangeType().equals(OperDef.QP_GT) ? oldDate.getFrom()
				.getYear() : oldDate.getTo().getYear();
		boolean hasYear = oldDate != null;
		
		//处理只有季报  并且跨年的情况
		if(report==0){
			report=4;
			year--;
		}
		
		DateRange range = DateUtil.getReportWithYearNum(year, report);
		range.setDateUnit(unit);
		range.setCanBeAdjust(false);
		range.setHasYear(hasYear);
		return range;
	}
}
