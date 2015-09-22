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


// 其他泛化时间范围
public class ParseRegularDateRange implements Parser
{
	public DateRange doParse(String dateString, String backtestTime) throws NotSupportedException
	{
		if (!DatePatterns.FROM_ONE_DAY_TO_ANTHER_DAY_NO36.matcher(dateString)
				.matches())
		{
			return null;
		}
		// 匹配“2012年1季至3季”
		if (Param.DEBUG_DATEINFO)
		{
			System.out.println("FROM_ONE_DAY_TO_ANTHER_DAY");
		}
		Matcher mid = DatePatterns.FROM_ONE_DAY_TO_ANTHER_DAY_NO36
				.matcher(dateString);
		mid.matches();
		String date1 = mid.group(1);
		String date2 = mid.group(2);
		DateRange dr1;
		DateRange dr2;
		try
		{
			dr1 = DateCompute.getDateInfoFromStr(date1, backtestTime);
			dr2 = DateCompute.getDateInfoFromStr(date2, backtestTime);
		} catch (NotSupportedException e)
		{
			throw new NotSupportedException(MsgDef.NOT_SUPPORTED_DATE_FMT,
					dateString);
		}
		if (dr1 == null || dr2 == null)
			return null;
		DateInfoNode from1 = dr1.getFrom();
		DateInfoNode from2 = dr2.getFrom();
		DateInfoNode to1 = dr1.getTo();
		DateInfoNode to2 = dr2.getTo();
		if (!dr2.hasYear())
		{
			// 若后面的时间未解析出确定的年份，则取前面时间的年份
			int fromY = to1.getYear();
			from2.setYear(fromY);
			to2.setYear(fromY);
		}
		// 12月4日至今涨幅小于20% 这种类型年减一
		else if (DatePatterns.TODAY_NO19.matcher(date2).matches())
		{
			if (dr1.getTo().isAfter(dr2.getFrom()))
			{
				if (!dr1.hasYear())
				{
					dr1.getFrom().setYear(dr1.getFrom().getYear() - 1);
					if (dr1.getFrom().getYear() != dr1.getTo().getYear())
					{
						dr1.getTo().setYear(dr1.getTo().getYear() - 1);
					}
				}
			}

		}
		Unit unit = DateUtil.isBigerUnit(dr1.getDateUnit(), dr2.getDateUnit()) ? dr2
				.getDateUnit() : dr1.getDateUnit();
		DateRange range = new DateRange(from1, to2);
		range.setDateUnit(unit);
		range.setCanBeAdjust(dr2.isCanBeAdjust());
		range.setIsLength(dr1.isLength() && dr2.isLength());
		
		if(!range.getFrom().equals(range.getTo())){
			range.setIsLength(true);
			range.calculateLength();
		}
		
		return range;
	}
}
