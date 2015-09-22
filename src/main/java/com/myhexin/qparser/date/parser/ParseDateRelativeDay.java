package com.myhexin.qparser.date.parser;

import java.util.regex.Matcher;

import com.myhexin.qparser.Param;
import com.myhexin.qparser.date.DateCompute;
import com.myhexin.qparser.date.DatePatterns;
import com.myhexin.qparser.date.DateRange;
import com.myhexin.qparser.date.DateUtil;
import com.myhexin.qparser.define.EnumDef.Unit;
import com.myhexin.qparser.except.UnexpectedException;



// 其他相对时间
public class ParseDateRelativeDay implements Parser
{
	public DateRange doParse(String dateString, String backtestTime) throws UnexpectedException
	{
		if (!DatePatterns.RELATIVE_DAY_20.matcher(dateString).matches())
		{
			return null;
		}
		if (Param.DEBUG_DATEINFO)
		{
			System.out.println("RELATIVE_DAY_20");
		}
		Matcher dateMatcher = DatePatterns.RELATIVE_DAY_20.matcher(dateString);
		dateMatcher.matches();
		String keyWord = dateMatcher.group(1);
		int change = keyWord.equals("昨") ? -1 : keyWord.equals("前") ? -2
				: keyWord.equals("上") ? -1 : keyWord.equals("大前") ? -3
						: keyWord.equals("明") ? 1 : keyWord.equals("后") ? 2
								: keyWord.equals("大后") ? 3 : Integer.MAX_VALUE;
		if (change == Integer.MAX_VALUE)
		{
			throw new UnexpectedException("未知相对日期类型");
		}
		
		DateRange range = null;
		if(backtestTime==null)
			range = DateUtil.getDateFromNowByChangeNumberAndUnit(DateUtil.CHANGE_BY_DAY, change);
		else{
			range = DateUtil.getDateFromNowByChangeNumberAndUnit(backtestTime, DateUtil.CHANGE_BY_DAY, change);
		}
		range.setDateUnit(Unit.DAY);
		range.setCanBeAdjust(false);
		range.setLength(1);
		return range;
	}
}
