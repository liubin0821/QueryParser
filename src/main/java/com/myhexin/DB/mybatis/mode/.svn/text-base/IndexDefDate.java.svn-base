package com.myhexin.DB.mybatis.mode;

import java.io.Serializable;
import java.util.Calendar;

import com.myhexin.qparser.date.DateUtil;


/**
 * 指标默认时间计算表达式
 * 具体情况table: configFile.parser_index_defdate
 * 
 * 
 * @author 刘小峰 liuxiaofeng@myhexin.com
 * @Version 创建时间 2015-07-13
 *
 */
public class IndexDefDate implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8839204167748804597L;
	private String id;
	private String indexName;
	private String domain;
	//private String hhmmss_use_today;
	private int hh=-1;
	private int mm=-1;
	private int ss=-1;
	
	//语义网中的reportType
	private String reportType;
	
	//时间推理计算公式,-1:往前推一天,-2:往前推2天,+1:往后推1天
	private String calcExpr;
	
	//比语义网中优先级更高的reportType
	private String reportType2;
	
	//默认的from,to时间区间
	private String defFrom;
	private String defTo;
	
	public boolean hasDef() {
		if(defFrom!=null && defTo!=null && defFrom.length()>0 && defTo.length()>0) {
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * 根据defFrom, defTo计算默认时间
	 * 
	 * 
	 * @param backtestTime
	 * @return
	 */
	public Calendar[] getDateByDef(Calendar backtestTime) {
		//fixed_yyyy, fixed_mm, fixed_dd, calc_yyyy, calc_mm, calc_dd
		//sample: fixed_yyyy=2015,fixed_mm=06,fixed_dd=30;
		if(defFrom!=null && defTo!=null && defFrom.length()>0 && defTo.length()>0) {
			String[] from_fields = defFrom.split(",");
			String[] to_fields = defTo.split(",");
			if(from_fields!=null && from_fields.length==3 && to_fields!=null && to_fields.length==3) {
				Calendar from  = getDateByDef(from_fields, backtestTime);
				Calendar to  = getDateByDef(to_fields, backtestTime);
				
				System.out.println(from.getTime());
				System.out.println(to.getTime());
				
				return new Calendar[]{from, to};
			}
		}
		return null;
	}
	
	private int _getDateByDef_getInt(String value) {
		try{
			return Integer.parseInt(value);
		}catch(Exception e){}
		return -1;
	}
	
	
	private final static int INVALID_DEF = -199999;
	private final static int MonthDayStart = -199991;
	private final static int MonthDayEnd = -199992;
	private Calendar getDateByDef(String[] fields, Calendar backtestTime) {
		Calendar current = Calendar.getInstance();
		if(backtestTime!=null) {
			current.setTimeInMillis(backtestTime.getTimeInMillis());
		}
		
		int year = current.get(Calendar.YEAR);
		int month = current.get(Calendar.MONTH);
		int day = current.get(Calendar.DAY_OF_MONTH);

		int fixedY = INVALID_DEF;
		int fixedM = INVALID_DEF;
		int fixedD = INVALID_DEF;
		int calcY = INVALID_DEF;
		int calcM = INVALID_DEF;
		int calcD = INVALID_DEF;
		
		for(String s : fields) {
			if(s.startsWith("fixed_yyyy=")) {
				fixedY = _getDateByDef_getInt(s.substring("fixed_yyyy=".length()));
			}else if(s.startsWith("fixed_mm=")) {
				fixedM = _getDateByDef_getInt(s.substring("fixed_mm=".length()));
			}else if(s.startsWith("fixed_dd=")) {
				fixedD = _getDateByDef_getInt(s.substring("fixed_dd=".length()));
			}else if(s.startsWith("calc_yyyy=")) {
				calcY = _getDateByDef_getInt(s.substring("calc_yyyy=".length()));
			}else if(s.startsWith("calc_mm=")) {
				calcM = _getDateByDef_getInt(s.substring("calc_mm=".length()));
			}else if(s.startsWith("calc_dd=")) {
				calcD = _getDateByDef_getInt(s.substring("calc_dd=".length()));
			}
		}
		
		if(fixedY!=INVALID_DEF) {
			current.set(Calendar.YEAR, fixedY);
		}else if(calcY!=INVALID_DEF) {
			current.set(Calendar.YEAR, year + calcY);
		}
		
		if(fixedM!=INVALID_DEF) {
			current.set(Calendar.DAY_OF_MONTH, 1);
			current.set(Calendar.MONTH, fixedM-1);
		}else if(calcM!=INVALID_DEF) {
			current.set(Calendar.DAY_OF_MONTH, 1);
			current.set(Calendar.MONTH, month + calcM);
		}
		
		if(fixedD!=INVALID_DEF) {
			if(fixedD == MonthDayStart)
				current.set(Calendar.DAY_OF_MONTH, 1);
			else if(fixedD == MonthDayEnd) {
				int year1 = current.get(Calendar.YEAR);
				int month1 = current.get(Calendar.MONTH);
				int day1= DateUtil.getMonthEndDay(year1, month1);
				current.set(Calendar.DAY_OF_MONTH, day1);
			}else{
				current.set(Calendar.DAY_OF_MONTH,fixedD );
			}
		}else if(calcD!=INVALID_DEF) {
			current.set(Calendar.DAY_OF_MONTH, day + calcD);
		}
		return current;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getIndexName() {
		return indexName;
	}
	public void setIndexName(String indexName) {
		this.indexName = indexName;
	}
	public String getReportType() {
		//reportType2高优先级
		if(reportType2!=null && reportType2.length()>0) {
			return reportType2;
		}
		
		return reportType;
	}
	public void setReportType(String reportType) {
		this.reportType = reportType;
	}
	public String getCalcExpr() {
		return calcExpr;
	}
	public void setCalcExpr(String calcExpr) {
		this.calcExpr = calcExpr;
	}
	
	public String getDomain() {
		return domain;
	}
	public void setDomain(String domain) {
		this.domain = domain;
	}
	
	public int getHh() {
		return hh;
	}
	public int getMm() {
		return mm;
	}
	public int getSs() {
		return ss;
	}
	public void setHhmmss_use_today(String hhmmss_use_today) {
		//this.hhmmss_use_today = hhmmss_use_today;
		if(hhmmss_use_today!=null && hhmmss_use_today.length()==6){
			hh = Integer.parseInt(hhmmss_use_today.substring(0,2));
			mm = Integer.parseInt(hhmmss_use_today.substring(2,4));
			ss = Integer.parseInt(hhmmss_use_today.substring(4,6));
		}else{
			hh = -1;
			mm= -1;
			ss = -1;
		}
		
	}
	public String toString() {
		return indexName + "," + reportType + "," + calcExpr;
	}

	public void setReportType2(String reportType2) {
		this.reportType2 = reportType2;
	}
	public void setDefFrom(String defFrom) {
		this.defFrom = defFrom;
	}
	public void setDefTo(String defTo) {
		this.defTo = defTo;
	}
	
	/*public static void main(String[] args) {
		IndexDefDate info = new IndexDefDate();
		info.setHhmmss_use_today("080100");
		System.out.println(info.getHh() + ", " + info.getMm() + "," + info.getSs());
	}*/
	
	
}
