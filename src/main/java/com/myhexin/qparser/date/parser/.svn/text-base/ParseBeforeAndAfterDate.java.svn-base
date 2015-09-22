package com.myhexin.qparser.date.parser;

import java.util.regex.Matcher;

import com.myhexin.qparser.date.DateCompute;
import com.myhexin.qparser.date.DatePatterns;
import com.myhexin.qparser.date.DateRange;
import com.myhexin.qparser.date.DateUtil;
import com.myhexin.qparser.define.EnumDef.Unit;
import com.myhexin.qparser.define.MsgDef;
import com.myhexin.qparser.except.NotSupportedException;
import com.myhexin.qparser.except.UnexpectedException;

public class ParseBeforeAndAfterDate implements Parser
{
	public DateRange doParse(String dateString, String backtestTime) throws NotSupportedException,
			UnexpectedException
	{
		if (!DatePatterns.BEFORE_AND_AFTER_NO38.matcher(dateString).matches())
		{
			return null;
		}
		Matcher mid = DatePatterns.BEFORE_AND_AFTER_NO38.matcher(dateString);
		mid.matches();
		String dateStr1 = mid.group(1);
		String dateStr2 = mid.group(3);
		String tagStr1 = mid.group(2);
		String tagStr2 = mid.group(4);
		if (!(tagStr1.matches("[以之]?前") && tagStr2.matches("[以之]?后") || tagStr2
				.matches("[以之]?前") && tagStr1.matches("[以之]?后")))
		{
			// 不能两个都是“之后”或者“之前”
			throw new NotSupportedException(MsgDef.NOT_SUPPORTED_DATE_FMT,
					dateString);
		}
		boolean date1IsEarlier = tagStr1.matches("[以之]?后");
		DateRange dr1;
		DateRange dr2;
		try
		{
			dr1 = DateCompute.getDateInfoFromStr(dateStr1, backtestTime);
			dr2 = DateCompute.getDateInfoFromStr(dateStr2, backtestTime);
		} catch (NotSupportedException e)
		{
			throw new NotSupportedException(MsgDef.NOT_SUPPORTED_DATE_FMT,
					dateString);
		}
		if (dr1 == null || dr2 == null)
			return null;
		// 返回的是较早时间的to的后一天与较晚时间的from的前一天。如“2005年之后2010年之前”应该是“2006-1-1至2009-12-31”
		DateRange range = date1IsEarlier ? new DateRange(DateUtil.getNewDate(
				dr1.getTo(), DateUtil.CHANGE_BY_DAY, 1), DateUtil.getNewDate(
				dr2.getFrom(), DateUtil.CHANGE_BY_DAY, -1)) : new DateRange(
				DateUtil.getNewDate(dr2.getTo(), DateUtil.CHANGE_BY_DAY, 1),
				DateUtil.getNewDate(dr1.getFrom(), DateUtil.CHANGE_BY_DAY, -1));
		Unit unit = DateUtil.isBigerUnit(dr1.getDateUnit(), dr2.getDateUnit()) ? dr2
				.getDateUnit() : dr1.getDateUnit();
		range.setDateUnit(unit);
		range.setCanBeAdjust(dr1.isCanBeAdjust() && dr2.isCanBeAdjust());
		range.setIsLength(dr1.isLength() && dr2.isLength());
		return range;
	}
}
