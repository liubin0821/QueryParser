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


public class ParseNumTypeDateWithMarker implements Parser
{
	public DateRange doParse(String dateString, String backtestTime) throws NotSupportedException,
			UnexpectedException
	{
		if (!DatePatterns.NUM_TYPE_RANGE_WITH_MARKER_NO35.matcher(dateString)
				.matches())
		{
			return null;
		}
		if (Param.DEBUG_DATEINFO)
		{
			System.out.println("NUM_TYPE_RANGE_WITH_MARKER");
		}
		Matcher mid1 = DatePatterns.NUM_TYPE_RANGE_WITH_MARKER_NO35
				.matcher(dateString);
		mid1.matches();
		String dateTo = mid1.group(2);
		int dateLen = -Integer.valueOf(mid1.group(4));
		String dateUnit = mid1.group(5);
		Unit unit = null;
		int length = Integer.valueOf(mid1.group(4));
		DateRange dr;
		try
		{
			dr = DateCompute.getDateInfoFromStr(dateTo, backtestTime);
		} catch (NotSupportedException e)
		{
			throw new NotSupportedException(MsgDef.NOT_SUPPORTED_DATE_FMT,
					dateString);
		}
		if (dr == null)
			return null;
		DateInfoNode to = dr.getTo();
		String unittag;

		if (dateUnit.equals("年"))
		{
			unit = Unit.YEAR;
			unittag = DateUtil.CHANGE_BY_YEAR;
		}
		else if (dateUnit.matches("个?月"))
		{
			unit = Unit.MONTH;
			unittag = DateUtil.CHANGE_BY_MONTH;
		}
		else if (dateUnit.matches("个?季度"))
		{
			unit = Unit.QUARTER;
			unittag = DateUtil.CHANGE_BY_MONTH;
			dateLen = dateLen * 3;
		}
		else if (dateUnit.matches("周|个?礼拜|个?星期"))
		{
			unit = Unit.WEEK;
			unittag = DateUtil.CHANGE_BY_DAY;
			dateLen = dateLen * 7;
		}
		else
		{
			unit = Unit.DAY;
			unittag = DateUtil.CHANGE_BY_DAY;
		}
		DateInfoNode from = DateUtil.getNewDate(to, unittag, dateLen);
		to = DateUtil.getNewDate(to, DateUtil.CHANGE_BY_DAY, -1);
		DateRange range = new DateRange(from, to);
		range.setIsLength(true);
		range.setDateUnit(unit);
		range.setLength(length);
		return range;
	}
}
