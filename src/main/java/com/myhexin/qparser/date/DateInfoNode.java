package com.myhexin.qparser.date;

import java.util.Calendar;

import com.myhexin.qparser.except.UnexpectedException;
import com.myhexin.qparser.time.info.TimePoint;

public class DateInfoNode implements Cloneable{
    /*
     * 时间信息节点
     */
    public DateInfoNode(){}
    
    public DateInfoNode(String yyyyMMdd){
    	int year = 0;
    	int month = 0;
    	int day = 0;
    	try{
    		year = Integer.parseInt(yyyyMMdd.substring(0,4));
    		month = Integer.parseInt(yyyyMMdd.substring(4,6));
    		day = Integer.parseInt(yyyyMMdd.substring(6));
    	}catch(Exception e){}
        this.setDateInfo(year, month, day);
    }
    
    public DateInfoNode(int year,int month,int day){
        this.setDateInfo(year, month, day);
    }
    
    public void setDateInfo(int year,int month,int day){
        this.setWeek(year,month,day);
        this.setYear(year);
        this.setMonth(month);
        this.setDay(day);
    }
    
    public DateInfoNode(Calendar cal){
    	if(cal!=null) {
    		int year = cal.get(Calendar.YEAR);
    		int month = cal.get(Calendar.MONTH)+1;
    		int day = cal.get(Calendar.DAY_OF_MONTH);
            this.setDateInfo(year, month, day);
    	}else{
    		Calendar cal1 = Calendar.getInstance();
    		 this.setDateInfo(cal1.get(Calendar.YEAR), cal1.get(Calendar.MONTH)+1,  cal1.get(Calendar.DAY_OF_MONTH));
    	}
    }
    
    private void setWeek(int year,int month,int day) {
        if(month==1) {month=13;year--;}
        if(month==2) {month=14;year--;}
        int week=(day+2*month+3*(month+1)/5+year+year/4-year/100+year/400)%7;
        int weektag = 0;
        switch(week){
            case 0: weektag=1; break;
            case 1: weektag=2; break;
            case 2: weektag=3; break;
            case 3: weektag=4; break;
            case 4: weektag=5; break;
            case 5: weektag=6; break;
            case 6: weektag=7; break;
        }
        this.Week=weektag; 
    }
    
    public void setYear(int year) {
        this.Year = year;
    }
    
    public void setMonth(int month) {
        this.Month = month;
    }
    
    public void setDay(int day) {
        this.Day = day;
    }
    /**
     * 是所属周的最后一天
     * @return 为周六，则返回true
     */
    public boolean isWeekEnd() {
        return getWeek()==6;
    }
    
    /**
     * 为所属月的最后一天
     * @return 是当月最后一天，则返回true
     */
    public boolean isMonthEnd() {
        return Day == getMonthday();
    }
    /**
     * 为所属季度的最后一天
     * @return 若为3月31日或6月30日或9月30日或12月31日，则返回true
     */
    public boolean isQuarterEnd() {
        return (Month == 3 || Month == 12) && Day == 31
                || (Month == 6 || Month == 9) && Day == 30;
    }
    
    /**
     * 为所属半年的最后一天
     * @return 若为6月30日或12月31日，则返回true
     */
    public boolean isHalfYearEnd() {
        return Month == 12 && Day == 31 || Month == 6 && Day == 30;
    }
    /**
     * 取得当前时间的最近季度报告期
     * @return 当前时间的最近季度报告期
     */
    public DateInfoNode getLatestReportQuarter() {
        return isQuarterEnd()?this:getLastQuarterEnd();
    }
    /**
     * 取得当前时间的最近半年报告期
     * @return 当前时间的最近半年报告期
     */
    public DateInfoNode getLatestReportHalfYear() {
        return isHalfYearEnd()?this:getLastHalfYearEnd();
    }
    /**
     * 取得当前时间的最近的年报告期
     * @return 当前时间的最近的年报告期
     */
    public DateInfoNode getLatestReportYear() {
        return isYearEnd()?this:getLastYearEnd();
    }
    /**
     * 是否为年的最后一天
     * @return 若为12月31日，则返回true
     */
    public boolean isYearEnd() {
        return Month == 12 && Day == 31 ;
    }
    
    
    public int getQuarter(){
        return DateUtil.getQuarterFromMonth(Month);
    }
    
    /**
     * 计算所属季度的最后一天
     * @return 所属季度的最后一天
     */
    public DateInfoNode getQuarterEnd() {
        int quarter = getQuarter();
        DateInfoNode quarterEnd = quarter == 1 ? new DateInfoNode(
                Year, 3, 31) : quarter == 2 ? new DateInfoNode(
                        Year, 6, 30) : quarter == 3 ? new DateInfoNode(
                                Year, 9, 30) : new DateInfoNode(Year, 12,
                31);
        return quarterEnd;
    }
    /**
     * 计算所属季度的第一天
     * @return 所属季度的第一天
     */
    public DateInfoNode getQuarterStart() {
        int quarter = getQuarter();
        DateInfoNode quarterStart = quarter == 1 ? new DateInfoNode(Year, 1, 1)
                : quarter == 2 ? new DateInfoNode(Year, 4, 1)
                        : quarter == 3 ? new DateInfoNode(Year, 7, 1)
                                : new DateInfoNode(Year, 10, 1);
        return quarterStart;
    }
    /**
     * 计算下个季度的最后一天
     * @return 下个季度最后一天
     */
    public DateInfoNode getNextQuarterEnd() {
        int quarter = getQuarter();
        DateInfoNode next = quarter == 1 ? new DateInfoNode(Year, 6, 30)
                : quarter == 2 ? new DateInfoNode(Year, 9, 30)
                        : quarter == 3 ? new DateInfoNode(Year, 12, 31)
                                : new DateInfoNode(Year + 1, 3, 31);
        return next;
    }
    
    /**
     * 计算上个季度的最后一天
     * @return 上个季度最后一天
     */
    public DateInfoNode getLastQuarterEnd() {
        int quarter = getQuarter();
        DateInfoNode last = quarter == 1 ? new DateInfoNode(Year - 1, 12, 31)
                : quarter == 2 ? new DateInfoNode(Year, 3, 31)
                        : quarter == 3 ? new DateInfoNode(Year, 6, 30)
                                : new DateInfoNode(Year, 9, 30);
        return last;
    }
    
    /**
     * 计算下个月最后一天
     * @return 下个月最后一天
     */
    public DateInfoNode getNextMonthEnd() throws UnexpectedException {
        DateInfoNode next = DateUtil.getNewDate(this, DateUtil.CHANGE_BY_MONTH,
                1).getMonthEnd();
        return next;
    }
    
    /**
     * 计算上个月最后一天
     * @return 上个月最后一天
     */
    public DateInfoNode getLastMonthEnd() throws UnexpectedException {
        DateInfoNode last = DateUtil.getNewDate(this, DateUtil.CHANGE_BY_MONTH,
                -1).getMonthEnd();
        return last;
    }
    
    /**
     * 计算本月最后一天
     * @return 本月最后一天
     */
    public DateInfoNode getMonthEnd() {
        DateInfoNode monthEnd = new DateInfoNode(Year, Month, getMonthday());
        return monthEnd;
    }
    
    /**
     * 计算本月第一天
     * @return 本月1日
     */
    public DateInfoNode getMonthStart() {
        DateInfoNode monthStart = new DateInfoNode(Year, Month, 1);
        return monthStart;
    }
    
    /**
     * 计算下周的最后一天，即下个周六
     * @return 下周周六
     * @throws UnexpectedException
     */
    public DateInfoNode getNextWeekEnd() throws UnexpectedException {
        DateInfoNode next = DateUtil.getNewDate(this, DateUtil.CHANGE_BY_DAY,
                7).getWeekEnd();
        return next;
    }
    
    /**
     * 计算上周的最后一天，即下个周六
     * @return 上周周六
     * @throws UnexpectedException
     */
    public DateInfoNode getLastWeekEnd() throws UnexpectedException {
        DateInfoNode last = DateUtil.getNewDate(this, DateUtil.CHANGE_BY_DAY,
                -7).getWeekEnd();
        return last;
    }
    
    /**
     * 取得当前日期所在周的周六，即当周最后一天
     * @return 当前日期所在周的周六
     * @throws UnexpectedException
     */
    public DateInfoNode getWeekEnd() throws UnexpectedException {
        DateInfoNode weekEnd =getWeek() == 7 ? DateUtil.getNewDate(this,
                DateUtil.CHANGE_BY_DAY, 6) : DateUtil.getNewDate(this,
                        DateUtil.CHANGE_BY_DAY, 6 - getWeek());
        return weekEnd;
    }
    
    /**
     * 计算所属周的第一天，即礼拜日
     * @return 所属周的礼拜日
     * @throws UnexpectedException
     */
    public DateInfoNode getWeekStart() throws UnexpectedException {
        DateInfoNode weekStart = getWeek() == 7 ? this : DateUtil.getNewDate(this,
                DateUtil.CHANGE_BY_DAY, -getWeek() );
        return weekStart;
    }

    /**
     * 计算所属半年的下个半年的最后一天
     * @return 即若现在为7月之前，则返回所属年的12月31日，若非，则返回下一年的6月30日
     */
    public DateInfoNode getNextHalfYearEnd() {
        int month = getMonth();
        DateInfoNode next = month > 6 ? new DateInfoNode(Year + 1, 6, 30)
                : new DateInfoNode(Year, 12, 31);
        return next;
    }
    
    /**
     * 计算所属半年的上个半年的最后一天
     * @return 即若现在为7月之前，则返回去年12月31日，若非，则返回6月30日
     */
    public DateInfoNode getLastHalfYearEnd() {
        DateInfoNode next = Month > 6 ? new DateInfoNode(Year, 6, 30)
                : new DateInfoNode(Year - 1, 12, 31);
        return next;
    }
    
    /**
     * 返回所属半年的最后一天
     * @return 即若当前日期为7月之前，则返回6月30日，若非，则返回12月31日
     */
    public DateInfoNode getHalfYearEnd() {
        DateInfoNode halfYearEnd = Month > 6 ? new DateInfoNode(Year, 12, 31)
                : new DateInfoNode(Year, 6, 30);
        return halfYearEnd;
    }
    
    /**
     * 返回所属半年的第一天
     * @return 即若当前日期为7月之前，则返回1月1日，若非，则返回7月1日
     */
    public DateInfoNode getHalfYearStart() {
        DateInfoNode halfYearStart = Month > 6 ? new DateInfoNode(Year, 7, 1)
                : new DateInfoNode(Year, 1, 1);
        return halfYearStart;
    }
    
    
    /**
     * 返回所属日期下一年的最后一天
     * @return 所属日期下一年的最后一天
     */
    public DateInfoNode getNextYearEnd() {
        DateInfoNode next = new DateInfoNode(Year + 1, 12, 31);
        return next;
    }
    
    /**
     * 返回所属日期上一年的最后一天
     * @return 所属日期上一年的最后一天
     */
    public DateInfoNode getLastYearEnd() {
        DateInfoNode last = new DateInfoNode(Year - 1, 12, 31);
        return last;
    }
    
    /**
     * 返回最近的一个交易日
     * @return 所属日期上一年的最后一天
     * @throws UnexpectedException 
     */
    public DateInfoNode getLastTradeDay() throws UnexpectedException {
        DateInfoNode last = DateUtil.rollTradeDate(this, 0);
        return last;
    }
   
    /**
     * 返回所属年份的最后一天，即12月31日
     * @return 所属年份的最后一天
     */
    public DateInfoNode getYearEnd() {
        DateInfoNode yearEnd = new DateInfoNode(Year, 12, 31);
        return yearEnd;
    }
    
    /**
     * 返回所属年份的第一天，即1月1日
     * @return 所属年份的第一天
     */
    public DateInfoNode getYearStart() {
        DateInfoNode yearStart = new DateInfoNode(Year, 1, 1);
        return yearStart;
    }
    
    /**
     * 判断当前日期是否为交易日
     * @return 若为交易日，返回true
     */
    public boolean isTradeDate() {
        return DateUtil.isTradeDate(toString(""));
    }
    
    public int getYear() {
        return Year;
    }

    public int getMonth() {
        return Month;
    }

    public int getDay() {
        return Day;
    }

    public int getWeek() {
        return Week;
    }

    public int compareTo(DateInfoNode other) {
        return Year < other.Year ? -1 :
            Year > other.Year ? 1 :
            Month < other.Month ? -1 :
            Month > other.Month ? 1 :
            Day < other.Day ? -1 :
            Day > other.Day ? 1 :
            0;
    }

    public String toString(String delimiter){
        return String.format("%4d%s%02d%s%02d", Year, delimiter, Month,
                delimiter, Day);
    }
    
    public static String toString(Calendar cal, String delimiter){
    	if(cal==null) cal=Calendar.getInstance();
        return String.format("%4d%s%02d%s%02d", cal.get(Calendar.YEAR), delimiter, cal.get(Calendar.MONTH) +1,
                delimiter, cal.get(Calendar.DAY_OF_MONTH));
    }
    
	public static TimePoint ToTimePoint(Calendar cal) {
		return new TimePoint(cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE));
	}

    public String toString() { return toString("-"); }
    
    /**
     * 若两个时间的年月日均一致，则认为两者相同
     * @param compare 比较的日期
     * @return 若两者一致，返回true
     */
    public boolean equals(DateInfoNode compare){
        return Year == compare.Year && Month == compare.Month
                && Day == compare.Day;
    }
    
    public DateInfoNode clone() {
        DateInfoNode rtn;
        try {
            rtn = (DateInfoNode) super.clone();
            return rtn;
        } catch (CloneNotSupportedException e) {
            throw new InternalError();
        }
    }
    
    /**
     * 判断当前时间是否晚于比较时间
     * @param compare 比较时间
     * @return 当前时间晚于 compare，则返回true
     */
    public boolean isAfter(DateInfoNode compare) {
        return DateUtil.changeDateInfoNodeToCalendar(this).after(DateUtil.changeDateInfoNodeToCalendar(compare));
    }
   
   /**
    * 判断当前时间是否早于比较时间
    * @param compare 比较时间
    * @return 当前时间早于 compare，则返回true
    */
    public boolean isEarlier(DateInfoNode compare) {
        return DateUtil.changeDateInfoNodeToCalendar(this).before(DateUtil.changeDateInfoNodeToCalendar(compare));
    }
    /**
     * 计算所属月份的天数
     * @return 所属月份的天数
     */
    public int getMonthday(){
        return DateUtil.getMonthDayCount(Year, Month);
    }
    private int Year;
    private int Month;
    private int Day;
    private int Week;
    
}
