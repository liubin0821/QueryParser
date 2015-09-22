package com.myhexin.qparser.util.cfgdate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import com.myhexin.qparser.phrase.util.Consts;

/**
 * 报告期时间逻辑
 * 
 * @author 刘小峰 liuxiaofeng@myhexin.com
 * @Version 创建时间 2015-1-31
 *
 */
public class DateTimeCfgReportPeriodGetter implements DateTimeCfgGetter{

	@Override
	public String getDatePeriod(String indexName, String propName, String dateTime) {
				
		//当前时间
		Calendar cal = Calendar.getInstance();
		//int current_year = cal.get(Calendar.YEAR);
		int current_month = cal.get(Calendar.MONTH) + 1;
		int current_day = cal.get(Calendar.DAY_OF_MONTH);
			
				
		List<String> retTimes = new ArrayList<String>();
		
		if(dateTime!=null) {
			SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");
			try {
				String[] dateTimes = dateTime.split(",");
				if(dateTimes!=null && dateTimes.length>0) {
					//isRangeDateTimes = true;
					//比一个多,是连续时间
					//取到最大的那个时间
					for(String dt : dateTimes) {
						Date dateTimeValue = fmt.parse(dt);
						cal.setTime(dateTimeValue);
						int tmp_year = cal.get(Calendar.YEAR);
						int tmp_month = cal.get(Calendar.MONTH) + 1;
						int tmp_day = cal.get(Calendar.DAY_OF_MONTH);
								
						if(tmp_month%3!=0 ) {
							tmp_month = (tmp_month/3+1)*3;
						}
						if(tmp_month==3 || tmp_month==12) tmp_day=31;
						if(tmp_month==6 || tmp_month==9) tmp_day=30;
						StringBuilder builder = new StringBuilder();
						builder.append(tmp_year);
						if(tmp_month<10) {
							builder.append('0').append(tmp_month);
						}else{
							builder.append(tmp_month);
						}
						builder.append(tmp_day);
						String dtStr = builder.toString();
						if(retTimes.contains(dtStr)==false) {
						retTimes.add(dtStr);
					}
								
					//判断是不是最大时间
					/*if(year<tmp_year) {
						year = tmp_year;
						month = tmp_month;
						day = tmp_day;
					}else if(year==tmp_year) {
						if(month < tmp_month) {
							month = tmp_month;
							day = tmp_day;
						}else if(month == tmp_month) {
							if(day < tmp_day) {
								day = tmp_day;
							}
						}
					}*/
				}
			}
			
			/*else{
			//是单个时间
			Date  dateTimeValue = fmt.parse(dateTime);
			cal.setTime(dateTimeValue);
			year = cal.get(Calendar.YEAR);
			month = cal.get(Calendar.MONTH)+1;
			day = cal.get(Calendar.DAY_OF_MONTH);
			}*/
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
				
		//TODO 不考虑其他情况,取最新, 再考虑不用取最新的情况
		if(Consts.DEBUG) {
			System.out.println("current_month=" + current_month);
			System.out.println("current_day=" + current_day);
		}
				
		CfgSimpleDate defaultPeriod =  getCfgDefaultPeriod(indexName, propName, current_month, current_day);
		if(Consts.DEBUG) {
			System.out.println("defaultPeriod=" + defaultPeriod.toString());
		}
			
		List<CfgSimpleDate> list = getCfgPeriod(indexName, propName,current_month, current_day, defaultPeriod);
		for(CfgSimpleDate cfg : list) {
			String dtStr = formatString(cfg);
			if(retTimes.contains(dtStr)==false) {
				retTimes.add(dtStr);
			}
		}
				
		if(Consts.DEBUG) {
			System.out.println("retTimes=" + retTimes);
		}
		Collections.sort(retTimes, new Comparator<String>(){
		@Override
		public int compare(String s1, String s2) {
			if(s1!=null && s2!=null ) {
				return s2.compareTo(s1);
			}
			if(s1==null) return 1;
			if(s2==null) return -1;
			return 0;
		}});
				
		StringBuilder builder = new StringBuilder();
		for(String time : retTimes) {
			builder.append(time).append(',');
		}
				
		if(builder!=null && builder.length()>0) {
			return builder.substring(0, builder.length()-1);
		}else{
			return null;
		}
	}
	

	private boolean isThisPeriod(CfgRptPeriodDefault defaultRptPeriod, int month, int day) {
		if(defaultRptPeriod.getFrom()!=null && defaultRptPeriod.getTo()!=null)
		{
			
			//System.out.println(month + "-" + day + ": " + defaultRptPeriod.toString());
			
			boolean fromFlag = (defaultRptPeriod.getFrom().getMonth()<month || (defaultRptPeriod.getFrom().getMonth()==month && defaultRptPeriod.getFrom().getDay()<=day ));
			boolean toFlag = (defaultRptPeriod.getTo().getMonth()>month || (defaultRptPeriod.getTo().getMonth()==month && defaultRptPeriod.getTo().getDay()>=day ));
			if(fromFlag && toFlag) {
				return true;
			}
		}
		return false;
	}
	
	protected String formatString(CfgSimpleDate cfg )
	{
		StringBuilder builder = new StringBuilder();
		if(cfg.getYear()==0){
			builder.append("0000");
		}
		else 
			builder.append(cfg.getYear());
		if(cfg.getMonth()<10){
			builder.append('0');
			builder.append(cfg.getMonth());
		}else {
			builder.append(cfg.getMonth());
		}
		
		if(cfg.getDay()<10){
			builder.append('0');
			builder.append(cfg.getDay());
		}else {
			builder.append(cfg.getDay());
		}
		return builder.toString();
	}
	
	private CfgSimpleDate getDefaultDefault(CfgRptPeriodDefault defaultRptPeriod, int year, int month, int day) {
		if(isThisPeriod(defaultRptPeriod, month, day)) {
			CfgSimpleDate default_val = defaultRptPeriod.getDefaultDate();
			if("-".equals(defaultRptPeriod.getSign_val() ) )
				year = year - defaultRptPeriod.getOffset_val();
			else{
				year = year + defaultRptPeriod.getOffset_val();
			}
			default_val.setYear(year);
			//System.out.println("Found : " + default_val);
			return default_val;
		}else{
			return null;
		}
	}
	
	/*
	 * 根据month, day取到默认报告期
	 */
	protected CfgSimpleDate getCfgDefaultPeriod(String indexName, String propName, int month, int day) {
		//如果是非法日期,赋值为一个默认当前时间
		Calendar cal = Calendar.getInstance();
		int year = cal.get(Calendar.YEAR);
		if(month>12 || month <1 || day <1 || day>31) {
			month = cal.get(Calendar.MONTH) + 1;
			day = cal.get(Calendar.DAY_OF_MONTH);
		}
		
		List<CfgRptPeriodDefault> defaultRptPeriods = CfgDateLogicInstance.getInstance().getDefaultPeriodObj(indexName,propName);
		if(defaultRptPeriods==null) {
			defaultRptPeriods = CfgDateLogicInstance.getInstance().getDefaultPeriodObj(indexName);
		}
		if(defaultRptPeriods==null) {
			defaultRptPeriods = CfgDateLogicInstance.getInstance().getDefaultPeriodObj(propName);
		}
		CfgSimpleDate defaultValue = null;
		if(defaultRptPeriods!=null) {
			for(CfgRptPeriodDefault defaultRptPeriod : defaultRptPeriods) {
				defaultValue = getDefaultDefault(defaultRptPeriod, year, month, day);
				if(defaultValue!=null) {
					break;
				}
			}
		}
		
		if(defaultValue==null) {
			//返回一个默认的
			month = cal.get(Calendar.MONTH);
			month = (month/3 + 1)*3;
			if(month<=3) return new CfgSimpleDate(year-1, 12,31); 
			else if(month<=6) return new CfgSimpleDate(year, 3,31); 
			else if(month<=9) return new CfgSimpleDate(year, 6,30); 
			else if(month<=12) return new CfgSimpleDate(year, 9,30); 
			else return new CfgSimpleDate(year-1, 12,31); //应该不会有这种情况
		}else{
			return defaultValue;
		}
		
		
		
		
		
		//System.out.println(indexName + ","+  propName);
		/*List<CfgRptPeriodDefault>  defaultRptPeriodList = CfgDateLogicInstance.getInstance().getDefaultRptPeriodList();
		CfgSimpleDate both_default_val = null;
		CfgSimpleDate default_val = null;
		for(CfgRptPeriodDefault defaultRptPeriod : defaultRptPeriodList) {
			//System.out.println(" " + defaultRptPeriod.toString());
			if(defaultRptPeriod.isMatchIndexPropBoth(indexName,  propName) ) {
				both_default_val = getDefaultDefault(defaultRptPeriod, year, month, day);
				if(both_default_val!=null) {
					break;
				}
			}else if(defaultRptPeriod.isMatchIndexPropBoth(indexName,  propName) ) {
				default_val = getDefaultDefault(defaultRptPeriod, year, month, day);
			}
		}
		if(both_default_val!=null) {
			return both_default_val;
		}else if(default_val!=null) {
			return default_val;
		}else{
			//返回一个默认的
			month = cal.get(Calendar.MONTH);
			month = (month/3 + 1)*3;
			if(month<=3) return new CfgSimpleDate(year-1, 12,31); 
			else if(month<=6) return new CfgSimpleDate(year, 3,31); 
			else if(month<=9) return new CfgSimpleDate(year, 6,30); 
			else if(month<=12) return new CfgSimpleDate(year, 9,30); 
			else return new CfgSimpleDate(year-1, 12,31); //应该不会有这种情况
		}*/
	}
	
	/*
	 * 根据默认报告期, 和year, month, day到配置中找到其他报告期时间
	 * 
	 * @param year
	 * @param month
	 * @param day
	 * @param defaultPeriodDate
	 * @return
	 */
	protected List<CfgSimpleDate> getCfgPeriod(String indexName, String propName, int month, int day, CfgSimpleDate defaultPeriodDate) {
		//匹配配置的时间范围,然后返回报告期
		List<CfgSimpleDate> list = new ArrayList<CfgSimpleDate>();
		CfgSimpleDate prev = null;
		CfgSimpleDate next = null;
		List<CfgDateEntity>  dateLogiclist = CfgDateLogicInstance.getInstance().getDateLogiclist();
		for(CfgDateEntity cfg : dateLogiclist) {
			if(!cfg.isMatchIndexProp(indexName, propName)) {
				continue;
			}
			
			if(cfg.isThisPeriod(month, day)) {
				if("Y".equals(cfg.getDt_previous() )) {
					prev = defaultPeriodDate.getPrevious();
				}
				
				if("Y".equals(cfg.getDt_next() )  ){
					next = defaultPeriodDate.getNext();
				}
			}
		}
		
		if(next!=null) list.add(next);
		if(defaultPeriodDate!=null) list.add(defaultPeriodDate);
		if(prev!=null) list.add(prev);
		
		return list;
	}

}
