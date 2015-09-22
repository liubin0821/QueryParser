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
 * 处理上下半年、上下半季度、上下半月、上下半周等情况 如果前面存在确切的时间，则根据前面确切的时间获得相对时间
 * 如果前面不存在确切的时间，则根据当前时间获得相对时间
 * 
 * @param dateString
 * @return
 * @throws NotSupportedException
 * @throws UnexpectedException
 */
public class ParseHalfRegionDate implements Parser
{
	public DateRange doParse(String dateString, String backtestTime) throws NotSupportedException,
			UnexpectedException
	{
		if (!DatePatterns.HALF_A_REGION_NO47.matcher(dateString).matches())
		{
			return null;
		}
		// 匹配表述中包含“半年、半季度、半月”的日期，如“2009年上半年”
		if (Param.DEBUG_DATEINFO)
		{
			System.out.println("HALF_A_REGION");
		}
		Matcher mid = DatePatterns.HALF_A_REGION_NO47.matcher(dateString);
		mid.matches();
		String dateStr = mid.group(1);
		boolean isFirstHalf = mid.group(2).equals("上")
				|| mid.group(2).equals("前");
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
		int year = oldRange == null ? DateUtil.getNow().getYear() : oldRange
				.getTo().getYear();
		int month = oldRange == null ? DateUtil.getNow().getMonth() : oldRange
				.getTo().getMonth();
		Unit unit = null;
		DateInfoNode pointhead = new DateInfoNode();
		DateInfoNode pointend = new DateInfoNode();
		String tagUnit = mid.group(3);
		// ("^(.*?)(上|下|前|后)半(年|个?年度|个?季度|个?月|周|个?星期|个?礼拜)$");
		if (tagUnit.matches("年") || tagUnit.matches("个?年度"))
		{
			// TODO:当前时间未到3月，则前年作为截止时间，先前推
			unit = Unit.MONTH;
			pointhead = isFirstHalf ? new DateInfoNode(year, 1, 1)
					: new DateInfoNode(year, 7, 1);
			pointend = isFirstHalf ? new DateInfoNode(year, 6, 30)
					: new DateInfoNode(year, 12, 31);
		}
		else if (tagUnit.matches("个?月"))
		{
			unit = Unit.DAY;
			pointhead = isFirstHalf ? new DateInfoNode(year, month, 1)
					: new DateInfoNode(year, month, 16);
			pointend = isFirstHalf ? new DateInfoNode(year, month, 15)
					: new DateInfoNode(year, month, DateUtil.getMonthDayCount(year,
							month));
		}
		else if (tagUnit.matches("个?季度"))
		{
			unit = Unit.DAY;
			DateInfoNode dateTmp = oldRange == null ? DateUtil.getNow()
					: oldRange.getTo();
			int seasonstart = DateUtil.getQuarterStart(dateTmp.getQuarter());
			int seasonend = DateUtil.getQuarterEnd(dateTmp.getQuarter());
			int monthday = DateUtil.getMonthDayCount(year, seasonend);
			pointhead = isFirstHalf ? new DateInfoNode(year, seasonstart, 1)
					: new DateInfoNode(year, seasonstart + 1, 16);
			pointend = isFirstHalf ? new DateInfoNode(year, seasonstart + 1, 15)
					: new DateInfoNode(year, seasonend, monthday);
		}
		else if (tagUnit.matches("周|个?星期|个?礼拜"))
		{// 星期
			unit = Unit.DAY;
			DateInfoNode dateTmp = oldRange == null ? DateUtil.getNow()
					: oldRange.getTo();
			// 上半周：周日、周一、周二、周三；下半周：周四、周五、周六
			pointhead = isFirstHalf ? dateTmp.getWeekStart() : DateUtil
					.getNewDate(dateTmp.getWeekEnd(), DateUtil.CHANGE_BY_DAY,
							-2);
			pointend = isFirstHalf ? DateUtil.getNewDate(
					dateTmp.getWeekStart(), DateUtil.CHANGE_BY_DAY, 3)
					: dateTmp.getWeekEnd();
		}
		DateRange range = new DateRange(pointhead, pointend);
		range.setHasYear(oldRange != null && oldRange.hasYear());
		range.setDateUnit(unit);
		range.setCanBeAdjust(false);
		return range;
	}
}
