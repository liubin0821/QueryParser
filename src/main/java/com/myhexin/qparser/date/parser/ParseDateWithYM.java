package com.myhexin.qparser.date.parser;

import java.util.regex.Matcher;

import com.myhexin.qparser.Param;
import com.myhexin.qparser.date.DateInfoNode;
import com.myhexin.qparser.date.DatePatterns;
import com.myhexin.qparser.date.DateRange;
import com.myhexin.qparser.date.DateUtil;
import com.myhexin.qparser.define.EnumDef.Unit;




public class ParseDateWithYM implements Parser
{
	public DateRange doParse(String dateString, String backtestTime)
	{
		int year;
		Matcher dateMatcher = null;
		if (DatePatterns.YEAR_MONTH.matcher(dateString).matches())
		{
			// 匹配既有年份也有月份，但无几号的日期，如“2012年3月”
			if (Param.DEBUG_DATEINFO)
			{
				System.out.println("YEAR_MONTH_NO3");
			}
			dateMatcher = DatePatterns.YEAR_MONTH.matcher(dateString);
			dateMatcher.matches();
			year = Integer.valueOf(dateMatcher.group(1));

		}
		else if (DatePatterns.YEAR_MONTH_2.matcher(dateString).matches())
		{
			// 匹配年份只有两位数字，而且有月份的日期，如“09年3月”
			// 这种日期在前期处理的时候可能已经被补全，但直接调用本方法的其他地方可能未对残缺的年份进行补全
			// 故此处不可删除
			if (Param.DEBUG_DATEINFO)
			{
				System.out.println("YEAR_MONTH_2_NO4");
			}
			dateMatcher = DatePatterns.YEAR_MONTH_2.matcher(dateString);
			dateMatcher.matches();
			String yearStr = dateMatcher.group(1);
			year = yearStr.startsWith("9") ? Integer.valueOf("19" + yearStr)
					: Integer.valueOf("20" + yearStr);
		}
		else
		{
			return null;
		}
		int month = Integer.valueOf(dateMatcher.group(2));
		int day = DateUtil.getMonthDayCount(year, month);
		DateInfoNode pointhead = new DateInfoNode(year, month, 1);
		DateInfoNode pointend = new DateInfoNode(year, month, day);
		DateRange range = new DateRange(pointhead, pointend);
		range.setDateUnit(Unit.MONTH);
		range.setLength(1);
		return range;
	}
}