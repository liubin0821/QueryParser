package com.myhexin.qparser.util.cfgdate;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.myhexin.qparser.phrase.util.Consts;

/**
 * 王璐报告期时间优化需求
 * 
 * @author 刘小峰 liuxiaofeng@myhexin.com
 * @Version 创建时间 2015-1-31
 *
 */
public class DateTimeCfgReportPeriodOptimizeGetter extends DateTimeCfgReportPeriodGetter{

	
	//王璐报告期时间优化需求
	//需求描述: 当查询单指标时,无时间参数时,返回今年已出季报+前三年报数据,wanglu在刘小峰电脑边确认
	@Override
	public String getDatePeriod(String indexName, String propName, String dateTime) {
				
		//当前时间
		Calendar cal = Calendar.getInstance();
		//int current_year = cal.get(Calendar.YEAR);
		int current_month = cal.get(Calendar.MONTH) + 1;
		int current_day = cal.get(Calendar.DAY_OF_MONTH);
			
				
		List<String> retTimes = new ArrayList<String>();
		
		//TODO 不考虑其他情况,取最新, 再考虑不用取最新的情况
		if(Consts.DEBUG) {
			System.out.println("current_month=" + current_month);
			System.out.println("current_day=" + current_day);
		}
				
		CfgSimpleDate defaultPeriod =  getCfgDefaultPeriod(indexName, propName, current_month, current_day);
		int defaultYear = defaultPeriod.getYear();
		if(Consts.DEBUG) {
			System.out.println("defaultPeriod=" + defaultPeriod.toString());
		}
			
		List<CfgSimpleDate> list = getCfgPeriod(indexName, propName,current_month, current_day, defaultPeriod);
		for(CfgSimpleDate cfg : list) {
			if(cfg.getYear() != defaultYear) continue;
			String dtStr = formatString(cfg);
			if(retTimes.contains(dtStr)==false) {
				retTimes.add(dtStr);
			}
		}
		
		for(int i=1;i<4;i++) {
			retTimes.add((defaultYear-i) + "1231");
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
	
}
