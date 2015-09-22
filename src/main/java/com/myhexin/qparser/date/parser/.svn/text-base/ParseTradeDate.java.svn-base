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
 * 对带有“交易日”的日期进行解析
 * 
 * @param dateString
 *            需解析的时间表达字符串
 * @return 解析出的时间范围
 * @throws NotSupportedException
 * @throws UnexpectedException
 */
public class ParseTradeDate implements Parser
{
	public DateRange doParse(String dateString, String backtestTime) throws NotSupportedException,
			UnexpectedException
	{

		if (!dateString.contains("交易日"))
			return null;

		DateInfoNode latest = DateUtil.getLatestTradeDate();
		DateRange range = null;
		boolean isLength = true;
		if (DatePatterns.TRADE_DATE_1.matcher(dateString).matches())
		{
			if (Param.DEBUG_DATEINFO)
			{
				System.out.println("TRADE_DATE_1");
			}
			Matcher mid = DatePatterns.TRADE_DATE_1.matcher(dateString);
			mid.matches();
			if (mid.group(1).equals("这"))
			{
				// 今天若为交易日，则不管是否到16点，都算这个交易日
				latest = DateUtil.isTradeDate(DateUtil.getNow().toString("")) ? DateUtil
						.getNow() : latest;
				range = new DateRange(latest, latest);
			}
			else if (mid.group(1).equals("上"))
			{
				range = new DateRange(DateUtil.rollTradeDate(DateUtil.getNow(),
						-1), DateUtil.rollTradeDate(DateUtil.getNow(), -1));
			}
			else if (mid.group(1).equals("上上"))
			{
				range = new DateRange(DateUtil.rollTradeDate(DateUtil.getNow(),
						-2), DateUtil.rollTradeDate(DateUtil.getNow(), -2));
			}
			else if (mid.group(1).equals("下"))
			{
				range = new DateRange(DateUtil.rollTradeDate(DateUtil.getNow(),
						1), DateUtil.rollTradeDate(DateUtil.getNow(), 1));
			}
			else
			{
				assert (false);
			}
			isLength = false;
		}
		else if (DatePatterns.TRADE_DATE_2.matcher(dateString).matches())
		{
			if (Param.DEBUG_DATEINFO)
			{
				System.out.println("TRADE_DATE_2");
			}
			Matcher mid = DatePatterns.TRADE_DATE_2.matcher(dateString);
			mid.matches();
			int nChange = Integer.valueOf(mid.group(1));
			latest = DateUtil.rollTradeDate(DateUtil.getNow(), 1);
			range = new DateRange(latest, DateUtil.rollTradeDate(latest,
					nChange));
		}
		else if (DatePatterns.TRADE_DATE_3.matcher(dateString).matches())
		{
			if (Param.DEBUG_DATEINFO)
			{
				System.out.println("TRADE_DATE_3");
			}
			Matcher mid = DatePatterns.TRADE_DATE_3.matcher(dateString);
			mid.matches();
			int nChange = -Integer.valueOf(mid.group(1));
			range = new DateRange(DateUtil.rollTradeDate(latest, nChange + 1),
					latest);
		}
		else if (DatePatterns.TRADE_DATE_4.matcher(dateString).matches())
		{
			if (Param.DEBUG_DATEINFO)
			{
				System.out.println("TRADE_DATE_4");
			}
			Matcher mid = DatePatterns.TRADE_DATE_4.matcher(dateString);
			mid.matches();
			int nChange1 = -Integer.valueOf(mid.group(1)) + 1;
			int nChange2 = -Integer.valueOf(mid.group(2)) + 1;
			// 用改变多的那个“2-3个交易日”，用3
			int change = nChange2 < nChange1 ? nChange2 : nChange1;
			range = new DateRange(DateUtil.rollTradeDate(latest, change),
					latest);
		}
		else if (DatePatterns.TRADE_DATE_5.matcher(dateString).matches())
		{
			if (Param.DEBUG_DATEINFO)
			{
				System.out.println("TRADE_DATE_5");
			}
			Matcher mid = DatePatterns.TRADE_DATE_5.matcher(dateString);
			mid.matches();
			String headStr = mid.group(1);
			latest = headStr.matches("前|之前|上") ? DateUtil.rollTradeDate(
					DateUtil.getNow(), -1) : latest;
			int nChange = -Integer.valueOf(mid.group(2));
			// 向前n个交易日是不算起始点的，故要少往前一个
			DateInfoNode from = DateUtil.rollTradeDate(latest, nChange + 1);
			range = new DateRange(from, latest);
		}
		else if (DatePatterns.TRADE_DATE_6.matcher(dateString).matches())
		{
			if (Param.DEBUG_DATEINFO)
			{
				System.out.println("TRADE_DATE_6");
			}
			Matcher mid = DatePatterns.TRADE_DATE_6.matcher(dateString);
			mid.matches();
			int nChange = Integer.valueOf(mid.group(1));
			String typeStr = mid.group(2);
			boolean isFuture = typeStr.indexOf("后") != -1;
			nChange = isFuture ? nChange - 1 : -nChange + 1;
			DateInfoNode endDate = DateUtil.rollTradeDate(latest, nChange);
			if (isFuture)
			{
				range = new DateRange(endDate, null);
			}
			else
			{
				range = new DateRange(null, endDate);
			}
		}
		else if (DatePatterns.TRADE_DATE_WITH_MARKER_7.matcher(dateString)
				.matches())
		{
			if (Param.DEBUG_DATEINFO)
			{
				System.out.println("TRADE_DATE_WITH_MARKER_7");
			}
			Matcher mid = DatePatterns.TRADE_DATE_WITH_MARKER_7
					.matcher(dateString);
			mid.matches();
			String oldDateStr = mid.group(1);
			int change = Integer.valueOf(mid.group(3));
			DateRange oldDate = null;
			try
			{
				oldDate = DateCompute.getDateInfoFromStr(oldDateStr, backtestTime);
			} catch (NotSupportedException e)
			{
				throw new NotSupportedException(MsgDef.NOT_SUPPORTED_DATE_FMT,
						dateString);
			}
			if (oldDate == null)
				return null;
			DateInfoNode to = DateUtil.getNewDate(oldDate.getFrom(),
					DateUtil.CHANGE_BY_DAY, -1);
			DateInfoNode from = DateUtil.rollTradeDate(to, -change);
			range = new DateRange(from, to);
		}
		else
		{
			// 作为时间解析的一部分，无法匹配现有交易日pattern时，直接返回null
			// 直到最终无法解析时在抛异常
			return null;
		}
		range.setDateUnit(Unit.DAY);
		range.setIsTradeDay(true);
		
		//如果单独一天 isLength为false		
		if(!range.getFrom().equals(range.getTo())){
			range.setIsLength(true);
			range.calculateLength();
		}
		
		return range;
	}
} 