package com.myhexin.qparser.date;

import java.util.List;

import com.myhexin.qparser.define.EnumDef.DateType;
import com.myhexin.qparser.define.EnumDef.Unit;
import com.myhexin.qparser.define.OperDef;

public class DateRange implements Cloneable{
    private DateInfoNode from = new DateInfoNode();
    private DateInfoNode to = new DateInfoNode(); 
    
    private static DateInfoNode DEFAULT_FROM;
    private static DateInfoNode DEFAULT_TO; 
    
    
    /* 2015-09-21
     * liuxiaofeng, 对于有些日期的问法,他的时间不是FROM-TO这种形式的
     * 转condition的时候,如果这里有内容,先用这个
     * 比如
     * 1.连续送股最多
     * 送股是季度报, 那么日期应该是 2015-06-30, 2015-03-31, 2014-12-31
     * 
     * 2. 连续3年净利润
     * 净利润是季度报指标,但是问句中提到了是年,那么日期应该是2014-12-31, 2013-12-31,2012-12-31
     * 
     * 
     * 
     */
    private List<DateInfoNode> dateinfos = null;
    public List<DateInfoNode> getDateinfos() {
		return dateinfos;
	}
	public void setDateinfos(List<DateInfoNode> dateinfos) {
		this.dateinfos = dateinfos;
	}

	/**
     * 标记是否有明确的年份，以后会被maxUnit所取代
     */
    private boolean hasYear = true;
    /**
     * 标记是否为长度时间，如“5天”
     */
    private boolean isLength = false;
    /**
     * 标记该时间可否被调整，若不可被调整，在后续时间调整部分会有较多限制。
     */
    private boolean canBeAdjust = true;
    /**
     * 标记是否明确说明为交易日
     */
    private boolean isTradeDay = false;
    /**
     * 标记是否为报告期
     */
    private boolean isReport = false;
    /**
     * 时间范围的最小单位，如“2000年1月1日”的最小单位为“DAY”
     */
    private Unit dateUnit = null;
    /**
     * 时间范围的最大单位，如“2000年1月1日”的最小单位为“YEAR”
     */
    private Unit maxUnit = null;
    /**
     * 标记时间类型。
     * 如“明年”，类型为“FUTURE”。
     * 如“去年”，类型为“BEFORE”。
     * 如“今年”，类型为“BOTH”。
     */
    private DateType dateType = null;
    /**
     * 记录解析时取得的最小单位的个数。
     * 若未记录时，值为0
     */
    private int length = 0;
    /**
     * 在解析如“连续3年中报”时，由于该时间由多个时间点组成，
     * 解析的同时，将这些点保存到此变量中，以供后续拆分使用
     */
    private List<DateRange> disperseDates = null;
    /**
     * 用于标记是否如“前一天”一样，为相对时间
     */
    private boolean isRelative = false;
    /**
     * 用于标识是否含有显式的年份，还是推导出的
     */
    private boolean hasExplicitYear = true;
    public boolean isExplicitYear()
    {
    	return this.hasExplicitYear;
    }
    public void setHasExplicitYear(boolean b)
    {
    	this.hasExplicitYear = b;
    }
    
    static{
        DEFAULT_FROM = new DateInfoNode();
        DEFAULT_TO = new DateInfoNode();
        DEFAULT_FROM.setDateInfo(1980, 1, 1);
        DEFAULT_TO.setDateInfo(2050, 12, 31);
    }
    
    public DateRange(){
        from = DEFAULT_FROM;
        to = DEFAULT_TO;
        dateType = DateType.BOTH;
    }
    
    public DateRange(DateInfoNode fromDin, DateInfoNode toDin) {
        DateInfoNode fromTmp = fromDin == null ? DEFAULT_FROM : fromDin;
        DateInfoNode toTmp = toDin == null ? DEFAULT_TO : toDin;
        this.setDateRange(fromTmp,toTmp);
    }
    
    public void setDateRange(DateInfoNode from, DateInfoNode to) {
        from = from == null ? DEFAULT_FROM : from;
        to = to == null ? DEFAULT_TO : to;
        if (!DateUtil.isEarly(from, to)) {
            setFrom(to);
            setTo(from);
        } else {
            setFrom(from);
            setTo(to);
        }
        setDateTypeByRange();
    }
    
    
    /**
     * dateType 不许被设置，需根据当前时间范围判断而来
     * @param dateType
     */
    private void setDateTypeByRange() {
        DateInfoNode now = DateUtil.getNow();
        if (from.isAfter(now) || from.equals(now)) {
            this.dateType = DateType.FUTURE;
        } else if (to.isEarlier(now) || to.equals(now)) {
            this.dateType = DateType.BEFORE;
        } else {
            this.dateType = DateType.BOTH;
        }
    }

    public DateRange clone() {
        try {
            DateRange rtn = (DateRange) super.clone();
            rtn.from = from.clone();
            rtn.to = to.clone();
            return rtn;
        } catch (CloneNotSupportedException e) {
            throw new InternalError();
        }
        
    }
    
    public DateInfoNode getFrom() {
        return from;
    }
    public void setFrom(DateInfoNode from) {
            this.from = from;    
            setDateTypeByRange();
    }

    public DateInfoNode getTo() {
        return to;
    }

    public void setTo(DateInfoNode to) {
            this.to = to;
            setDateTypeByRange();
    }    
    
    public String toDateString(){
        String dateStr = String.format("%s TO %s", from.toString(),to.toString());
        if(disperseDates==null){
            return dateStr;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(dateStr);
        for(int i=0;i<disperseDates.size();i++){
            sb.append("\n\t").append(disperseDates.get(i).toString());
        }
        return sb.toString();
    }
    
    public String toString(){
        String dateStr = String.format("%s -- %s", from.toString(),to.toString());
        return dateStr;
    }
    
    public String getRangeType(){
        if(from.equals(to)) {
            return OperDef.QP_EQ;
        } else if(from.getYear()==1980) {
            return OperDef.QP_LT;
        } else if(to.getYear() == 2050) {
            return OperDef.QP_GT;
        } else {
            return OperDef.QP_IN;
        }   
    }

    public void setDateUnit(Unit dateUnit) {
        this.dateUnit = dateUnit;
    }

    public Unit getDateUnit() {
        return dateUnit;
    }

    public void setHasYear(boolean hasYear) {
        this.hasYear = hasYear;
    }

    public boolean hasYear() {
        return hasYear;
    }

    public void setIsLength(boolean isLength) {
        this.isLength = isLength;
    }

    public boolean isLength() {
        return isLength;
    }

  

    public void setLength(int length) {
        this.length = length;
    }

    public int getLength() {
		//chenhao 每次都重新计算下长度,因为发现解析出来的时间长度会不对
		return calculateLength();
    }
    
    public final int calculateLength(){
    	if(isTradeDay)
			length = DateUtil.tradeDateBetween(from, to);
		else
			length =  DateUtil.daysBetween(from, to) + 1;
    	return length;
    }

    public void setIsReport(boolean isReport) {
        this.isReport = isReport;
    }

    public boolean isReport() {
        return isReport;
    }

    public void setIsTradeDay(boolean isTradeDay) {
        this.isTradeDay = isTradeDay;
    }

    public boolean isTradeDay() {
        return isTradeDay;
    }

    public DateType getDateType() {
        return dateType;
    }

    public void setCanBeAdjust(boolean canBeAdjust) {
        this.canBeAdjust = canBeAdjust;
    }

    public boolean isCanBeAdjust() {
        return canBeAdjust;
    }

    /**
     * 对1月1号到1月2号，返回的天数为1
     * @return
     */
    public int countDay() {
        return DateUtil.daysBetween(from,to);
    }
    
    /**
     *对2010年到2011年，返回的年数为1
     * @return
     */
    public int countYear() {
        return DateUtil.yearsBetween(from,to);
    }
    
    /**
     * 对1月到2月，返回的月数为1 
     * @return
     */
    public int countMonth(){
        return DateUtil.monthsBetween(from,to);
    }
    
    public void setDisperseDates(List<DateRange> disperseDates) {
        boolean isEmpty = disperseDates==null||disperseDates.isEmpty();
        this.disperseDates = isEmpty?null:disperseDates;
    }

    public List<DateRange> getDisperseDates() {
        return disperseDates;
    }

    public void setRelative(boolean isRelative) {
        this.isRelative = isRelative;
    }

    public boolean isRelative() {
        return isRelative;
    }

    public void setMaxUnit(Unit maxUnit) {
        this.maxUnit = maxUnit;
    }

    public Unit getMaxUnit() {
        return maxUnit;
    }
    
	public boolean contains(DateRange dr) {
		if (from.compareTo(dr.from) <= 0 && to.compareTo(dr.to) >= 0) {
			return true;
		}
		return false;
	}
	
	/*
     * 比较各属性值是否相等
     * text			内容
     * isCombined	是否被合并
     * isBound		是否被绑定
     * props		属性是否相同
     * from、to
     * isTradeDay
     * isReport
     * dateUnit
     * dateType
     * length
     * isRelative
     */
    public boolean equals(Object obj) {
    	if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof DateRange))
			return false;
		final DateRange dr = (DateRange) obj;
		
		if (!this.from.equals(dr.from) || !this.to.equals(dr.to))
			return false;
		if (this.isTradeDay != dr.isTradeDay)
			return false;
		if (this.isReport != dr.isReport)
			return false;
		if (this.dateUnit != dr.dateUnit)
			return false;
		if (this.dateType != dr.dateType)
			return false;
		if (this.length != dr.length)
			return false;
		if (this.isRelative != dr.isRelative)
			return false;
		return true;
    }
    
    //合并优先级 Xiaofeng 2014-10-27
    private int mergePriority;
	public int getMergePriority() {
		return mergePriority;
	}
	public void setMergePriority(int mergePriority) {
		this.mergePriority = mergePriority;
	}
    
}
