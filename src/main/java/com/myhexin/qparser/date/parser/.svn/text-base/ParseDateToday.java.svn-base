package com.myhexin.qparser.date.parser;

import com.myhexin.qparser.Param;
import com.myhexin.qparser.date.DateInfoNode;
import com.myhexin.qparser.date.DatePatterns;
import com.myhexin.qparser.date.DateRange;
import com.myhexin.qparser.date.DateUtil;
import com.myhexin.qparser.define.EnumDef.Unit;
import com.myhexin.qparser.except.UnexpectedException;


// 以“天”为单位的相对当前的时间
public class ParseDateToday implements Parser
{
	public DateRange doParse(String dateString, String backtestTime)
	{
		if (!DatePatterns.TODAY_NO19.matcher(dateString).matches())
		{
			return null;
		}
		// 匹配表示今天的日期，如“今日”
		if (Param.DEBUG_DATEINFO)
		{
			System.out.println("TODAY_NO19");
		}
		DateRange range = null;
		if(backtestTime!=null) {
			try {
				range = DateUtil.getDateFromNowByChangeNumberAndUnit(backtestTime, DateUtil.CHANGE_BY_DAY, 0);
			} catch (UnexpectedException e) {
				range = new DateRange(DateUtil.getNow(), DateUtil.getNow());
			}
		}else{
			 range = new DateRange(DateUtil.getNow(), DateUtil.getNow());
		}
		
		//DateRange range = new DateRange(DateUtil.getNow(), DateUtil.getNow());
		range.setDateUnit(Unit.DAY);
		range.setCanBeAdjust(false);
		range.setLength(1);
		return range;
	}
}
