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
public class ParseNumTypeDateRange implements Parser
{
	public DateRange doParse(String dateString, String backtestTime) throws UnexpectedException
	{
		if (!DatePatterns.NUM_TYPE_RANGE_NO31.matcher(dateString).matches())
		{
			return null;
		}
		if (Param.DEBUG_DATEINFO)
		{
			System.out.println("NUM_TYPE_RANGE_NO31");
		}
		DateRange range = new DateRange();
		Matcher mid = DatePatterns.NUM_TYPE_RANGE_NO31.matcher(dateString);
		mid.matches();
		Unit unit = null;
		int length = 0;
		int change = 0;
		boolean isLenght = true;
		boolean isHalf = mid.group(2).equals("半");
		if (isHalf)
		{
			String tag = null;
			if (mid.group(3).matches("年") || mid.group(3).matches("个?年度"))
			{
				tag = DateUtil.CHANGE_BY_MONTH;
				change = -6;
				unit = Unit.DAY;
			}
			else if (mid.group(3).matches("个?月"))
			{
				tag = DateUtil.CHANGE_BY_DAY;
				change = -15;
				unit = Unit.DAY;
			}
			else if (mid.group(3).matches("季|个?季度"))
			{
				tag = DateUtil.CHANGE_BY_DAY;
				change = -45;
				unit = Unit.DAY;
			}
			else if (mid.group(3).matches("周|个?礼拜|个?星期"))
			{
				tag = DateUtil.CHANGE_BY_DAY;
				change = -4;
				unit = Unit.DAY;
			}
			else
			{
				tag = DateUtil.CHANGE_BY_DAY;
				change = -1;
				unit = Unit.DAY;
			}
			DateInfoNode to = null;
			if(backtestTime==null){
				to = DateUtil.getLatestTradeDate();
			}else{
				to = DateUtil.getDateInfoNode(backtestTime);
				to = DateUtil.rollTradeDate(to, 0);
			}
			DateInfoNode from = DateUtil.getNewDate(
					DateUtil.getNewDate(to, tag, change),
					DateUtil.CHANGE_BY_DAY, 1);
			range.setDateRange(from, to);
			range.setIsLength(true);
			range.setLength(-change);
			range.setDateUnit(unit);
			return range;
		}

		String headTag = mid.group(1);
		change = -Integer.valueOf(mid.group(2));
		length = Integer.valueOf(mid.group(2));
		DateInfoNode pointhead = new DateInfoNode();
		DateInfoNode pointend = new DateInfoNode();
		String tagUnit = mid.group(3);
		if (tagUnit.matches("年") || tagUnit.matches("个?年度"))
		{
			// TODO:当前时间未到3月，则前年作为截止时间，先前推
			unit = Unit.YEAR;
			// 若为“近1年”，则截止日期为最近的一个交易日
			pointend = DateUtil.getLatestTradeDate();
			if(backtestTime==null){
				pointend = DateUtil.getLatestTradeDate();
			}else{
				pointend = DateUtil.getDateInfoNode(backtestTime);
				pointend = DateUtil.rollTradeDate(pointend, 0);
			}
			pointhead = DateUtil.getNewDate(DateUtil.getNewDate(pointend,
					DateUtil.CHANGE_BY_YEAR, change),
					DateUtil.CHANGE_BY_DAY, 1);
		}
		else if (tagUnit.matches("个?月"))
		{
			unit = Unit.MONTH;
			DateRange drTmp = null;
			boolean needCheck = headTag.length() == 0 && tagUnit.equals("月")
					&& mid.group(2).matches("^0?[1-9]|1[0-2]$");
			if (needCheck)
			{
				//sb 
				// TODO：处理歧义，还需产品再定
				try
				{
					drTmp = DateCompute.getDateInfoFromStr(mid.group(2)
							+ mid.group(3), backtestTime);
				} catch (NotSupportedException e)
				{
					// 不可能~
					assert (false);
				}
			}
			if (mid.group(4).matches("内|以内"))
			{
				//unit = Unit.DAY;
				return drTmp;
			}
			//pointend = DateUtil.getLatestTradeDate();
			if(backtestTime==null){
				pointend = DateUtil.getLatestTradeDate();
			}else{
				pointend = DateUtil.getDateInfoNode(backtestTime);
				pointend = DateUtil.rollTradeDate(pointend, 0);
			}
			DateInfoNode headTmp = DateUtil.getNewDate(DateUtil.getNewDate(
					pointend, DateUtil.CHANGE_BY_MONTH, change),
					DateUtil.CHANGE_BY_DAY, 1);
			pointhead = drTmp == null ? headTmp : drTmp.getFrom();
		}
		else if (tagUnit.matches("日|天"))
		{
			unit = Unit.DAY;
			// 根据开头词决定是用昨天为截止日期还是用最近的交易日。
			// 如“前一天”若没有其他时间作为它的标杆时间，那就应该是指昨天
			if(headTag.matches("上|前|之前")) {
				pointend = DateUtil.getNewDate(DateUtil.getNow(), DateUtil.CHANGE_BY_DAY, -1);
				pointhead = DateUtil.getNewDate(pointend, DateUtil.CHANGE_BY_DAY,change + 1);
				
			}else{
				if(backtestTime==null){
					pointend = DateUtil.getLatestTradeDate();
				}else{
					pointend = DateUtil.getDateInfoNode(backtestTime);
					pointend = DateUtil.rollTradeDate(pointend, 0);
				}
				pointhead = DateUtil.rollTradeDate(pointend,change + 1);
				//pointhead = DateUtil.getNewDate(pointend, DateUtil.CHANGE_BY_DAY,change + 1);
			}
			
			//pointend = headTag.matches("上|前|之前") ? DateUtil.getNewDate(DateUtil.getNow(), DateUtil.CHANGE_BY_DAY, -1) : DateUtil.getLatestTradeDate();
			//pointhead = DateUtil.getNewDate(pointend, DateUtil.CHANGE_BY_DAY,change + 1);
			// TODO:连续，最近5日内，使用交易日浮动
		}
		else if (tagUnit.matches("季|个?季度"))
		{
			// TODO:现对“季度”未调整
			// 1234季度
			if (headTag.length() == 0 && tagUnit.matches("季|季度"))
				return null;
			unit = Unit.QUARTER;
			change = 3 * change;
			pointend = DateUtil.getNow().getLastQuarterEnd();
			int yearc = DateUtil.getNewDate(pointend, DateUtil.CHANGE_BY_MONTH,
					change + 1).getYear();
			int monthc = DateUtil.getNewDate(pointend,
					DateUtil.CHANGE_BY_MONTH, change + 1).getMonth();
			pointhead.setDateInfo(yearc, monthc, 1);
			range.setHasExplicitYear(false); // ygf
		}
		else
		{// 星期
			unit = Unit.WEEK;
			if(backtestTime==null){
				pointend = DateUtil.getLatestTradeDate();
			}else{
				pointend = DateUtil.getDateInfoNode(backtestTime);
				pointend = DateUtil.rollTradeDate(pointend, 0);
			}
			pointhead = DateUtil.getNewDate(pointend, DateUtil.CHANGE_BY_DAY,
					change * 7 + 1);
		}
		range.setDateRange(pointhead, pointend);
		range.setIsLength(isLenght);
		range.setDateUnit(unit);
		range.setLength(length);
		return range;
	}
}
