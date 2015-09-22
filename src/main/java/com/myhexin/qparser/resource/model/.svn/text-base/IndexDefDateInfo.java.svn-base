package com.myhexin.qparser.resource.model;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.myhexin.DB.mybatis.MybatisHelp;
import com.myhexin.DB.mybatis.mode.IndexDefDate;
import com.myhexin.qparser.ApplicationContext.ApplicationContextHelper;
import com.myhexin.qparser.ApplicationContext.BeanNameConsts;
import com.myhexin.qparser.date.DateInfoNode;
import com.myhexin.qparser.date.DateUtil;
import com.myhexin.qparser.define.EnumDef.ReportType;
import com.myhexin.qparser.except.UnexpectedException;

/**
 * 指数name->IdList mapping
 * 
 * 
 * 
 * @author 刘小峰 liuxiaofeng@myhexin.com
 * @Version 创建时间 2015-5-12
 * 
 */
public class IndexDefDateInfo implements ResourceInterface{
	//private static org.slf4j.Logger logger_ = org.slf4j.LoggerFactory.getLogger(Parser.class.getName());
	private static MybatisHelp mybatisHelp = ApplicationContextHelper.getBean(BeanNameConsts.MYBATIS_HELP);
	private static IndexDefDateInfo instance = new IndexDefDateInfo();
	private Map<String, IndexDefDate> indexDefDateMap;
	private IndexDefDateInfo() {
		
	}
	
	public static IndexDefDateInfo getInstance() {
		return instance;
	}
	
	public String getIndexDefDate(String indexName) {
		if(indexDefDateMap==null) {
			return null;
		}
		
		IndexDefDate defDate = indexDefDateMap.get(indexName);
		if(defDate!=null) {
			return defDate.toString();
		}
		return null;
	}
	
	@Override
	public void reload() {
		// 加载指数指标ID名称映射
		List<IndexDefDate> indexDefDateList = mybatisHelp.getDateParserInfoMapper().selectIndexDefDate();
		Map<String, IndexDefDate> tmp_indexDefDateMap = new HashMap<String, IndexDefDate>();
		for(IndexDefDate date : indexDefDateList) {
			if(date.getDomain()!=null && date.getDomain().trim().length()>0) {
				tmp_indexDefDateMap.put(date.getDomain() + "@" + date.getIndexName(), date);
			}else{
				tmp_indexDefDateMap.put(date.getIndexName(), date);
			}
			
		}
		indexDefDateMap = tmp_indexDefDateMap;
	}
	
	public Calendar[] getDate(String indexName, ReportType reportType, String domain, Calendar backtestTime) {
		if(indexDefDateMap==null) {
			return new Calendar[]{backtestTime};
		}
		
		IndexDefDate defDate = null;
		if(domain!=null && indexName!=null) {
			defDate = indexDefDateMap.get(domain + "@" + indexName);
			
			if(defDate==null) {
				defDate = indexDefDateMap.get(indexName);
			}
		}
		
		
		if(defDate==null) {
			int idx = indexName.lastIndexOf("@");
			if(idx>=0) {
				String new_name = indexName.substring(idx+1);
				defDate = indexDefDateMap.get(new_name);
			}
		}
		
		if(defDate==null && reportType!=null) {
			defDate = new IndexDefDate();
			defDate.setIndexName(indexName);
			defDate.setCalcExpr("0");
			defDate.setReportType(String.valueOf(reportType));
		}
		
		if(defDate==null) {
			return new Calendar[]{backtestTime};
		}
		
		/**
		 * 检查有没有默认时间配置
		 * 
		 */
		if(defDate!=null && defDate.hasDef()) {
			return defDate.getDateByDef(backtestTime);
		}
		
		ReportType type = null;
		try{
			type = ReportType.valueOf(defDate.getReportType());
		}catch(Exception e){
		}
		
		if(type==null) {
			return new Calendar[]{backtestTime};
		}
		int calcValue = 0;
		try{
			calcValue = Integer.parseInt(defDate.getCalcExpr());
		}catch(Exception e){}
		
		
		 //YEAR, HALF_YEAR, QUARTER, MONTH, FUTURE_QUARTER, DAILY, TRADE_DAILY, NATURAL_DAILY, FUTURE_DAILY, NOW
	    //,FUTURE_YEAR,TIME
		Calendar retCal = null;
		switch(type) {
			case QUARTER: retCal = calcQuarter(type, calcValue, backtestTime); break;
			case MONTH: retCal = calcMonth(type, calcValue, backtestTime); break;
			case YEAR: retCal = calcYear(type, calcValue, backtestTime); break;
			case FUTURE_YEAR: retCal = calcFutureYear(type, calcValue, backtestTime); break;
			case HALF_YEAR: retCal = calcHalfYear(type, calcValue, backtestTime); break;
			case FUTURE_QUARTER: retCal = calcFutherQuarter(type, calcValue, backtestTime); break;
			case DAILY:  retCal = calcNaturalDaily(type, calcValue, backtestTime); break;
			case TRADE_DAILY: retCal = calcTradeDaily(type, calcValue, backtestTime, defDate.getHh(),defDate.getMm(),defDate.getSs() ); break;
			case NATURAL_DAILY:  retCal = calcNaturalDaily(type, calcValue, backtestTime); break;
			case FUTURE_DAILY:  retCal = calcTradeDaily(type, calcValue, backtestTime,defDate.getHh(),defDate.getMm(),defDate.getSs());	 break;
			default: retCal = backtestTime; break;
		}
		
		if(retCal!=null) {
			return new Calendar[]{retCal};
		}else{
			return new Calendar[]{backtestTime};
		}
	}
	
	private Calendar calcNaturalDaily(ReportType type, int calcValue, Calendar backtestTime) {
		Calendar current = Calendar.getInstance();
		if(backtestTime!=null) {
			current.setTimeInMillis(backtestTime.getTimeInMillis());
		}
		current.add(Calendar.DAY_OF_MONTH, calcValue);
		return current;
	}
	
	private Calendar calcTradeDaily(ReportType type, int calcValue, Calendar backtestTime, int hh, int mm, int ss) {
		Calendar current = Calendar.getInstance();
		//取这几个时间的代码要放在这里，免得setTimeInMillis把时间都设置成0000000
		int c_hh = current.get(Calendar.HOUR_OF_DAY);
		int c_mm = current.get(Calendar.MINUTE);
		int c_ss = current.get(Calendar.SECOND);
		
		if(backtestTime!=null) {
			current.setTimeInMillis(backtestTime.getTimeInMillis());
		}
		
		//是TRADE_DAILY,并且calcValue=-1的情况,才做这个时间点的计算逻辑
		//如果超过hh,mm,ss时间点,用今天做完交易日期
		if(calcValue==-1 && (hh!=-1 && mm!=-1 && ss!=-1)) {
			//当前时间大于配置的hhmmss,那么用今天
			if(c_hh>hh) {
				calcValue+=1;
			}else if(c_hh==hh) {
				if(c_mm>mm) {
					calcValue+=1;
				}else if(c_mm==mm) {
					if(c_ss>ss) {
						calcValue+=1;
					}
				}
			}
		}
		
		
		try {
			DateInfoNode infoNode = new DateInfoNode(current);
			infoNode = DateUtil.rollTradeDate(infoNode, calcValue);
			current.set(Calendar.DAY_OF_MONTH, 1);
			current.set(Calendar.YEAR, infoNode.getYear());
			current.set(Calendar.MONTH, infoNode.getMonth()-1);
			current.set(Calendar.DAY_OF_MONTH, infoNode.getDay());
		} catch (UnexpectedException e) {
			e.printStackTrace();
		}
		return current;
	}
	
	/* 
	 * 
	 * 根据calcValue计算QUARTER值
	 * 
	 * @param type
	 * @param calcValue
	 * @param backtestTime
	 * @return
	 */
	private Calendar calcQuarter(ReportType type, int calcValue, Calendar backtestTime) {
		Calendar current = Calendar.getInstance();
		if(backtestTime!=null) {
			current.setTimeInMillis(backtestTime.getTimeInMillis());
		}
		
		int year = current.get(Calendar.YEAR);
		int month = current.get(Calendar.MONTH);
		int day = current.get(Calendar.DAY_OF_MONTH);
		
		//要加这一行,否则时间是7/31时,current.add(Calendar.MONTH, -3)计算逻辑有问题
		current.set(Calendar.DAY_OF_MONTH, 1); 
		
		month = (month/3)*3-1; //0,1,2=-1, 3,4,5=2,   6,7,8=5,   9,10,11=8
		if(month==-1) {
			year--;
			month=11;
		}
		current.set(Calendar.YEAR, year);
		current.set(Calendar.MONTH, month);
		current.add(Calendar.MONTH, calcValue*3);
		
		month = current.get(Calendar.MONTH);
		day = DateUtil.getMonthEndDay(year, month);
		current.set(Calendar.DAY_OF_MONTH, day);
		return current;
	}
	
	
	private Calendar calcMonth(ReportType type, int calcValue, Calendar backtestTime) {
		Calendar current = Calendar.getInstance();
		if(backtestTime!=null) {
			current.setTimeInMillis(backtestTime.getTimeInMillis());
		}
		
		int year = current.get(Calendar.YEAR);
		int month = current.get(Calendar.MONTH);
		int day = current.get(Calendar.DAY_OF_MONTH);
		current.set(Calendar.DAY_OF_MONTH, 1); 
		
		current.add(Calendar.MONTH, -1);
		current.add(Calendar.MONTH, calcValue);
		
		month = current.get(Calendar.MONTH);
		day = DateUtil.getMonthEndDay(year, month);
		
		current.set(Calendar.DAY_OF_MONTH, day);
		return current;
	}
	private Calendar calcFutureYear(ReportType type, int calcValue, Calendar backtestTime) {
		Calendar current = Calendar.getInstance();
		if(backtestTime!=null) {
			current.setTimeInMillis(backtestTime.getTimeInMillis());
		}
		current.add(Calendar.YEAR, 1);
		current.add(Calendar.YEAR, calcValue);
		current.set(Calendar.MONTH, 11);
		current.set(Calendar.DAY_OF_MONTH, 31);
		return current;
	}
	
	private Calendar calcYear(ReportType type, int calcValue, Calendar backtestTime) {
		Calendar current = Calendar.getInstance();
		if(backtestTime!=null) {
			current.setTimeInMillis(backtestTime.getTimeInMillis());
		}
		current.add(Calendar.YEAR, -1);
		current.add(Calendar.YEAR, calcValue);
		current.set(Calendar.MONTH, 11);
		current.set(Calendar.DAY_OF_MONTH, 31);
		return current;
	}
	
	private Calendar calcHalfYear(ReportType type, int calcValue, Calendar backtestTime) {
		Calendar current = Calendar.getInstance();
		if(backtestTime!=null) {
			current.setTimeInMillis(backtestTime.getTimeInMillis());
		}
		int year = current.get(Calendar.YEAR);
		int month = current.get(Calendar.MONTH);
		
		int day=1;
		if(month>5) {
			month=5;
		}else{
			year = year-1;
			month=11;
		}
		current.set(Calendar.DAY_OF_MONTH, 1);
		current.set(Calendar.YEAR, year);
		current.set(Calendar.MONTH, month);
		
		if(calcValue!=0)
			current.add(Calendar.MONTH, calcValue*6);
		
		month = current.get(Calendar.MONTH);
		day = DateUtil.getMonthEndDay(year, month);
		current.set(Calendar.MONTH, month);
		current.set(Calendar.DAY_OF_MONTH, day);
		return current;
	}
	
	
	private Calendar calcFutherQuarter(ReportType type, int calcValue, Calendar backtestTime) {
		Calendar current = Calendar.getInstance();
		if(backtestTime!=null) {
			current.setTimeInMillis(backtestTime.getTimeInMillis());
		}
		int year = current.get(Calendar.YEAR);
		int month = current.get(Calendar.MONTH);
		int day = current.get(Calendar.DAY_OF_MONTH);
		current.set(Calendar.DAY_OF_MONTH, 1);
		month = (month/3)*3+2; //0,1,2=2, 3,4,5=5,   6,7,8=8,   9,10,11=11
		
		
		//current.set(Calendar.YEAR, year);
		current.set(Calendar.MONTH, month);
		if(calcValue!=0)
			current.add(Calendar.MONTH, calcValue*3);
		
		month = current.get(Calendar.MONTH);
		day = DateUtil.getMonthEndDay(year, month);
		current.set(Calendar.DAY_OF_MONTH, day);
		return current;
	}
}
