package com.myhexin.qparser.date.parser;

import java.util.regex.Matcher;

import com.myhexin.qparser.Param;
import com.myhexin.qparser.date.DateInfoNode;
import com.myhexin.qparser.date.DatePatterns;
import com.myhexin.qparser.date.DateRange;
import com.myhexin.qparser.define.EnumDef.Unit;
import com.myhexin.qparser.define.MsgDef;
import com.myhexin.qparser.except.NotSupportedException;
import com.myhexin.qparser.except.UnexpectedException;
import com.myhexin.qparser.number.NumUtil;

// 由数字及时间单位表示的单个日期
public class ParseDateWithYMD implements Parser
{
	public DateRange doParse(String dateString, String backtestTime) throws UnexpectedException,
			NotSupportedException
	{
		DateRange range = new DateRange();
		DateInfoNode pointonly = new DateInfoNode();
		Matcher mid = null;
		if (DatePatterns.YEAR_MONTH_DAY_WITH_SPLIT_SIGN.matcher(dateString)
				.matches())
		{
			mid = DatePatterns.YEAR_MONTH_DAY_WITH_SPLIT_SIGN
					.matcher(dateString);
		}
		else if (DatePatterns.YEAR_MONTH_DAY_AS_NUM.matcher(dateString)
				.matches())
		{
			mid = DatePatterns.YEAR_MONTH_DAY_AS_NUM.matcher(dateString);
		}
		else
		{
			return null;
		}
		if (Param.DEBUG_DATEINFO)
		{
			System.out
					.println("YEAR_MONTH_DAY_WITH_SPLIT_SIGN_NO1 or YEAR_MONTH_DAY_AS_NUM_NO2");
		}
		mid.matches();
		if (!NumUtil.checkAsYMD(mid.group(1), mid.group(2), mid.group(3)))
		{
			throw new NotSupportedException(MsgDef.NOT_SUPPORTED_DATE_FMT,
					dateString);
		}
		int year = Integer.valueOf(mid.group(1));
		int month = Integer.valueOf(mid.group(2));
		int day = Integer.valueOf(mid.group(3));
		pointonly.setDateInfo(year, month, day);
		range.setDateRange(pointonly, pointonly);
		range.setDateUnit(Unit.DAY);
		range.setLength(1);
		range.setCanBeAdjust(false);// 应该不进行调整
		return range;
	}
}