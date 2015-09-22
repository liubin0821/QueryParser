package com.myhexin.qparser.util.cfgdate;

import java.util.Calendar;
import java.util.List;

import com.myhexin.qparser.date.DateInfoNode;
import com.myhexin.qparser.date.DateUtil;
import com.myhexin.qparser.except.UnexpectedException;

/**
 * 龙虎榜时效时间,6点之前取昨天的,6点之后取当天的
 * 
 * @author 刘小峰 liuxiaofeng@myhexin.com
 * @Version 创建时间 2015-1-31
 *
 */
public class DateTimeCfgDragonTigerListGetter implements DateTimeCfgGetter{

	private boolean isThisPeriod(CfgRptPeriodDefault defaultRptPeriod, int hh, int mm, int ss) {
		if(defaultRptPeriod.getFrom()!=null && defaultRptPeriod.getTo()!=null){
			CfgSimpleDate from = defaultRptPeriod.getFrom();
			CfgSimpleDate to = defaultRptPeriod.getTo();
			boolean fromFlag = (from.getHour() <hh || (from.getHour()==hh && from.getMinute()< mm) || (from.getHour()==hh && from.getMinute()==mm && from.getSecond()<=ss) );
			boolean toFlag = (to.getHour() >hh || (to.getHour()==hh && to.getMinute()> mm) || (to.getHour()==hh && to.getMinute()==mm && to.getSecond()>=ss) );
			
			if(fromFlag && toFlag) {
				return true;
			}
			
		}
		return false;
	}
	
	
	private String getDefault(Calendar cal, CfgRptPeriodDefault defaultRptPeriod, int hh, int mm, int ss) {
		if(isThisPeriod(defaultRptPeriod, hh, mm,ss)) {
			if("-".equals(defaultRptPeriod.getSign_val() ) )
				cal.add(Calendar.DAY_OF_MONTH, 0-defaultRptPeriod.getOffset_val());
			int year = cal.get(Calendar.YEAR);
			int month = cal.get(Calendar.MONTH)+1;
			int day = cal.get(Calendar.DAY_OF_MONTH);
			DateInfoNode node = new DateInfoNode(year, month,day);
			//System.out.println("calc time = " + node.toString());
			try {
				DateInfoNode newDate = DateUtil.rollTradeDate(node, 0);
				//System.out.println("calc time = " + newDate.toString());
				return formatTime(newDate);
			} catch (UnexpectedException e) {
				e.printStackTrace();
				return formatTime(node);
			}
		}else{
			return null;
		}
	}
	
	@Override
	public String getDatePeriod(String indexName, String propName, String dateTime) {
		
		
		
		List<CfgRptPeriodDefault> defaultRptPeriods = CfgDateLogicInstance.getInstance().getDefaultPeriodObj(indexName,propName);
		if(defaultRptPeriods==null) {
			defaultRptPeriods = CfgDateLogicInstance.getInstance().getDefaultPeriodObj(indexName);
		}
		if(defaultRptPeriods==null) {
			defaultRptPeriods = CfgDateLogicInstance.getInstance().getDefaultPeriodObj(propName);
		}
		String defaultValue = null;
		if(defaultRptPeriods!=null) {
			for(CfgRptPeriodDefault defaultRptPeriod : defaultRptPeriods) {
				Calendar cal = Calendar.getInstance();
				int hh = cal.get(Calendar.HOUR_OF_DAY);
				int mm = cal.get(Calendar.MINUTE);
				int ss = cal.get(Calendar.SECOND);
				
				defaultValue = getDefault(cal, defaultRptPeriod, hh,mm,ss);
				if(defaultValue!=null) {
					break;
				}
			}
		}
		
		if(defaultValue==null) {
			Calendar cal = Calendar.getInstance();
			defaultValue = formatTime(new DateInfoNode(cal));
		}
		
		return defaultValue;
		
		/*
		List<CfgRptPeriodDefault>  defaultRptPeriodList = CfgDateLogicInstance.getInstance().getDefaultRptPeriodList();
		String both = null;
		String single = null;
		for(CfgRptPeriodDefault defaultRptPeriod : defaultRptPeriodList) {
			if(defaultRptPeriod.isMatchIndexPropBoth(indexName,  propName) ) {
				both = getDefault(cal, defaultRptPeriod, hh,mm,ss);
				break;
			}else if(defaultRptPeriod.isMatchIndexPropEither(indexName,  propName) ) {
				single = getDefault(cal, defaultRptPeriod, hh,mm,ss);
			}
		}
		if(both!=null) {
			return both;
		}else if(single!=null) {
			return single;
		}else{
			return formatTime(new DateInfoNode(cal));
		}
		*/
		
	}

	private String formatTime(DateInfoNode node ) {
		StringBuilder b = new StringBuilder();
		b.append(node.getYear());
		
		if(node.getMonth()<10) {
			b.append('0').append(node.getMonth());
		}else{
			b.append(node.getMonth());
		}
		if(node.getDay()<10) {
			b.append('0').append(node.getDay());
		}else{
			b.append(node.getDay());
		}
		
		return b.toString();
	}


}
