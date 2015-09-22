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
import com.myhexin.qparser.except.NotSupportedException;
import com.myhexin.qparser.except.UnexpectedException;


/**
 * 处理当天、当月、当季度、当年等相对时间 如果前面存在确切的时间，则根据前面确切的时间获得相对时间 如果前面不存在确切的时间，则根据当前时间获得相对时间
 * 
 * @param dateString
 * @return
 * @throws NotSupportedException
 * @throws UnexpectedException
 */
public class ParseThatRegionDate implements Parser
{
	public DateRange doParse(String dateString, String backtestTime) throws NotSupportedException
	{
		if (!DatePatterns.THAT_REGION_NO48.matcher(dateString).matches())
		{
			return null;
		}
		// 匹配表述中包含“当年、当季度、当月”的日期，如“2009年当年”
		if (Param.DEBUG_DATEINFO)
		{
			System.out.println("THAT_REGION");
		}
		Matcher mid = DatePatterns.THAT_REGION_NO48.matcher(dateString);
		mid.matches();
		String dateStr = mid.group(1);
		boolean hasOldDate = dateStr.length() > 0;
		DateRange oldRange;
		try
		{
			oldRange = hasOldDate ? DateCompute.getDateInfoFromStr(dateStr, backtestTime)
					: null;
		} catch (NotSupportedException e)
		{
			throw new NotSupportedException(MsgDef.NOT_SUPPORTED_DATE_FMT,
					dateString);
		}
		if (oldRange != null)
		{
			return oldRange;
		}
		int year = oldRange == null ? DateUtil.getNow().getYear() : oldRange
				.getTo().getYear();
		int month = oldRange == null ? DateUtil.getNow().getMonth() : oldRange
				.getTo().getMonth();
		int day = oldRange == null ? DateUtil.getNow().getDay() : oldRange
				.getTo().getDay();
		Unit unit = null;
		DateInfoNode pointhead = new DateInfoNode();
		DateInfoNode pointend = new DateInfoNode();
		String tagUnit = mid.group(2);
		// ("^(.*?)当(日|天|月|季|季度|年)$");
		if (tagUnit.matches("年"))
		{
			// TODO:当前时间未到3月，则前年作为截止时间，先前推
			unit = Unit.YEAR;
			pointhead = new DateInfoNode(year, 1, 1);
			pointend = new DateInfoNode(year, 12, 31);
		}
		else if (tagUnit.matches("季|季度"))
		{
			unit = Unit.QUARTER;
			DateInfoNode dateTmp = oldRange == null ? DateUtil.getNow()
					: oldRange.getTo();
			int seasonstart = DateUtil.getQuarterStart(dateTmp.getQuarter());
			int seasonend = DateUtil.getQuarterEnd(dateTmp.getQuarter());
			int monthday = DateUtil.getMonthDayCount(year, seasonend);
			pointhead = new DateInfoNode(year, seasonstart, 1);
			pointend = new DateInfoNode(year, seasonend, monthday);
		}
		else if (tagUnit.matches("月"))
		{
			unit = Unit.MONTH;
			pointhead = new DateInfoNode(year, month, 1);
			pointend = new DateInfoNode(year, month, DateUtil.getMonthDayCount(year,
					month));
		}
		else if (tagUnit.matches("日|天"))
		{
			unit = Unit.DAY;
			pointhead = new DateInfoNode(year, month, day);
			pointend = new DateInfoNode(year, month, day);
			;
		}
		DateRange range = new DateRange(pointhead, pointend);
		range.setHasYear(oldRange != null && oldRange.hasYear());
		range.setDateUnit(unit);
		range.setCanBeAdjust(false);
		return range;
	}
}
