package com.myhexin.qparser.date.parser;

import java.util.regex.Matcher;

import com.myhexin.qparser.Param;
import com.myhexin.qparser.date.DateCompute;
import com.myhexin.qparser.date.DateInfoNode;
import com.myhexin.qparser.date.DatePatterns;
import com.myhexin.qparser.date.DateRange;
import com.myhexin.qparser.date.DateUtil;
import com.myhexin.qparser.define.EnumDef.Unit;
import com.myhexin.qparser.except.NotSupportedException;
import com.myhexin.qparser.except.UnexpectedException;


/**
 * 解析形如“最近5年内”的数字型时间
 * 
 * @param dateString
 *            需解析的 时间表述
 * @return 解析出的时间范围
 * @throws UnexpectedException
 */
public class ParseNumTypeDateRangeIn implements Parser
{
	public DateRange doParse(String dateString, String backtestTime) throws UnexpectedException, NotSupportedException
	{
		
		if (!DatePatterns.NUM_TYPE_RANGE_NO.matcher(dateString).matches())
		{
			return null;
		}
		if (Param.DEBUG_DATEINFO)
		{
			System.out.println("NUM_TYPE_RANGE_NO");
		}
		Matcher mid = DatePatterns.NUM_TYPE_RANGE_NO.matcher(dateString);
		mid.matches();
		
		DateRange drTmp = null;
		drTmp = DateCompute.getDateInfoFromStr("近" + mid.group(2) + mid.group(3), backtestTime);
		if (drTmp == null)
			return null;
		
		drTmp.setDateUnit(Unit.DAY);
		drTmp.setLength(DateUtil.daysBetween(drTmp.getFrom(), drTmp.getTo())+1);
		
		return drTmp;
	}
}
