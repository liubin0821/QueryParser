package com.myhexin.qparser.node;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.lang.StringUtils;

import com.myhexin.qparser.Query;
import com.myhexin.qparser.date.DateFrequencyInfo;
import com.myhexin.qparser.date.DateInfoNode;
import com.myhexin.qparser.date.DateRange;
import com.myhexin.qparser.date.DateUtil;
import com.myhexin.qparser.define.EnumConvert;
import com.myhexin.qparser.define.EnumDef.CompareType;
import com.myhexin.qparser.define.EnumDef.DateType;
import com.myhexin.qparser.define.EnumDef.NodeType;
import com.myhexin.qparser.define.EnumDef.ReportType;
import com.myhexin.qparser.define.EnumDef.Unit;
import com.myhexin.qparser.except.BadDictException;
import com.myhexin.qparser.except.UnexpectedException;
import com.myhexin.qparser.number.NumRange;
import com.myhexin.qparser.util.Pair;
import com.myhexin.qparser.util.Util;

public class DateNode extends SemanticNode {
	public DateNode(){
		type = NodeType.DATE;
	}
	
	public DateNode(String text) {
		super(text);
		type = NodeType.DATE;
	}

	@Override
	public void parseNode(HashMap<String, String> k2v, Query.Type qtype)
			throws BadDictException {
	}

	/**
	 * 判断该时间是否为一天
	 * 
	 * @return 若为一天，则返回true，否则返回false
	 * @throws UnexpectedException
	 */
	public boolean isExactlyOneDay() throws UnexpectedException {
		if (getDateinfo() == null) {
			return false;
		}
		String type = getRangeType();
		Unit unit = getUnitOfDate();

		if (type.equals("=") || unit == Unit.DAY && isSequence) {
			// 若起止时间相同，或单位为“天”且为连续时间，则该时间非时间范围
			return true;
		}
		return false;
	}

	public Unit getUnitOfDate() throws UnexpectedException {
		if (dateinfo != null && dateinfo.getDateUnit() != null) {
			return dateinfo.getDateUnit();
		}
		String unit = null;
		unit = this.getUnitStrOfDate();
		if (unit == null) {
			return Unit.UNKNOWN;
		}
		return EnumConvert.getUnitFromStr(unit);
	}

	public String getUnitStrOfDate() throws UnexpectedException {
		String dateUnitStr = null;
		String text = this.text;
		Unit unit = this.getDateinfo() == null ? null : this.getDateinfo()
				.getDateUnit();
		if (unit != null) {
			// 若DateRange已有识别的单位，则用DateRange的
			dateUnitStr = EnumConvert.getStrFromUnit(unit);
			if (dateUnitStr == null) {
				throw new UnexpectedException(
						"DateNode does not have right Unit:%s", unit);
			}
		} else if ( text.indexOf("号") != -1 || text.indexOf("日") != -1 || text.indexOf("天") != -1 || text.indexOf("交易日") != -1 || text.indexOf("即时") != -1 ) { //即时委托
			dateUnitStr = "日";
		} else if (text.endsWith("周")) {
			dateUnitStr = "周";
		} else if (text.indexOf("周") != -1) {
			dateUnitStr = "日";
		} else if (text.indexOf("月") != -1) {
			dateUnitStr = "月";
		} else if (text.indexOf("季") != -1 || text.indexOf("中报") != -1) {
			dateUnitStr = "季";
		} else if (text.indexOf("年") != -1) {
			dateUnitStr = "年";
		}
		if (dateUnitStr != null) {
			return dateUnitStr;
		}
		if (this.dateinfo == null) {
			throw new UnexpectedException("DateNode can not get unit:%s",this.text);
		}
		dateUnitStr = DateUtil.getUnitOfDateByDateRange(this.dateinfo);
		return dateUnitStr;
	}

	
	public void setDateinfo(DateInfoNode dateinfo) {
		if(this.dateinfo==null) 
			this.dateinfo = new DateRange();
		this.dateinfo.setFrom(dateinfo);
		this.dateinfo.setTo(dateinfo);
	}
	
	public void setDateinfo(DateInfoNode fromDateinfo, DateInfoNode toDateinfo) {
		if (this.dateinfo == null)
			this.dateinfo = new DateRange();
		this.dateinfo.setFrom(fromDateinfo);
		this.dateinfo.setTo(toDateinfo);
	}

	public void setDateinfo(DateRange dateinfo) {
		this.dateinfo = dateinfo;
		if(dateinfo != null)
			this.splitDates = (ArrayList<DateRange>) dateinfo.getDisperseDates();
	}

	public DateRange getDateinfo() {
		return dateinfo;
	}

	/**
	 * 对1月1号到1月2号，返回的天数为1
	 * 
	 * @return
	 * @throws UnexpectedException
	 */
	public int countDay() throws UnexpectedException {
		if (this.dateinfo == null) {
			throw new UnexpectedException("Date 还未解析成功：%s", this.text);
		}
		return DateUtil.daysBetween(dateinfo.getFrom(), dateinfo.getTo());
	}

	/**
	 * 对2010年到2011年，返回的年数为1
	 * 
	 * @return
	 * @throws UnexpectedException
	 */
	public int countYear() throws UnexpectedException {
		if (this.dateinfo == null) {
			throw new UnexpectedException("Date 还未解析成功：%s", this.text);
		}
		return DateUtil.yearsBetween(dateinfo.getFrom(), dateinfo.getTo());
	}

	/**
	 * 对1月到2月，返回的月数为1
	 * 
	 * @return
	 * @throws UnexpectedException
	 */
	public int countMonth() throws UnexpectedException {
		if (this.dateinfo == null) {
			throw new UnexpectedException("Date 还未解析成功：%s", this.text);
		}
		return DateUtil.monthsBetween(dateinfo.getFrom(), dateinfo.getTo());
	}

	public int getRangeLen(Unit pu) throws UnexpectedException {
		if (pu == Unit.DAY) {
			int dayLen = countDay() + 1;
			return dayLen;
		} else if (pu == Unit.MONTH) {
			int monthLen = countMonth() + 1;
			return monthLen;
		} else if (pu == Unit.YEAR) {
			int yearLen = countYear() + 1;
			return yearLen;
		} else if (pu == Unit.WEEK) {
			int dayLen = countDay() + 1;
			return dayLen % 7 == 0 ? dayLen / 7 : dayLen / 7 + 1;
		} else if (pu == Unit.QUARTER) {
			int monthLen = countMonth() + 1;
			return monthLen % 3 == 0 ? monthLen / 3 : monthLen / 3 + 1;
		} else {
			throw new UnexpectedException("暂不支持时间对单位%s长度的计算");
		}
	}

	public ArrayList<DateRange> splitRangeToListOfDateRangePlusOne()
			throws UnexpectedException {
		return splitRangeToListOfDateRangePlusOne(null).second;
	}

	/**
	 * 对当前日期节点的日期信息，按日期的最小单位进行拆解。 拆分后，在前面加上一个最小单位时间段
	 * 
	 * @param reportType
	 * @return 拆解出的DateRange的ArrayList
	 * @throws UnexpectedException
	 */
	public Pair<String, ArrayList<DateRange>> splitRangeToListOfDateRangePlusOne(
			ReportType reportType) throws UnexpectedException {
		ArrayList<DateRange> dates = splitRangeToListOfDateRangeByReportType(reportType);
		if (dates == null) {
			return null;
		}
		String unitStr = getUnitStrCompareWithReportType(reportType);
		// 用拆分出的第一个时间的From作为前推的起始点
		DateRange addRange = getDateRangeAsPlusOne(dates.get(0).getFrom(),
				unitStr, reportType);
		dates.add(0, addRange);
		return new Pair<String, ArrayList<DateRange>>(unitStr, dates);
	}

	private DateRange getDateRangeAsPlusOne(DateInfoNode dateInfoNode,
			String unitStr, ReportType reportType) throws UnexpectedException {
		DateInfoNode addTo = DateUtil.getNewDate(dateInfoNode,
				DateUtil.CHANGE_BY_DAY, -1);
		DateInfoNode addFrom = null;
		if (unitStr.equals("日")) {
			// 现默认“日”的均找交易日
			addFrom = DateUtil.rollTradeDate(dateInfoNode, -1);
			addTo = DateUtil.rollTradeDate(dateInfoNode, -1);
		} else if (unitStr.equals("周")) {
			addFrom = DateUtil.getNewDate(dateInfoNode, DateUtil.CHANGE_BY_DAY,
					-7);
		} else if (unitStr.equals("月")) {
			addFrom = DateUtil.getNewDate(dateInfoNode,
					DateUtil.CHANGE_BY_MONTH, -1);
		} else if (unitStr.equals("季")) {
			addTo = addTo.getLatestReportQuarter();
			addFrom = addTo.getQuarterStart();
		} else if (unitStr.equals("半年")) {
			addFrom = DateUtil.getNewDate(dateInfoNode,
					DateUtil.CHANGE_BY_MONTH, -6);
		} else if (unitStr.equals("年") && !dateinfo.isReport()) {
			addFrom = DateUtil.getNewDate(dateInfoNode,
					DateUtil.CHANGE_BY_YEAR, -1);
		} else {
			assert (false);
		}

		DateRange addRange = new DateRange(addFrom, addTo);
		return addRange;
	}

	private String getUnitStrCompareWithReportType(ReportType reportType)
			throws UnexpectedException {
		Unit unit = this.getUnitOfDate();
		String unitStr = this.getUnitStrOfDate();
		;
		boolean shouldReturnReportType = unit == Unit.DAY || unit == Unit.MONTH
				&& reportType != ReportType.DAILY
				&& reportType != ReportType.FUTURE_DAILY;
		shouldReturnReportType |= unit == Unit.QUARTER
				&& (reportType == ReportType.YEAR || reportType == ReportType.HALF_YEAR);
		if (reportType == null || !shouldReturnReportType) {
			return unitStr;
		}
		if (reportType == ReportType.DAILY
				|| reportType == ReportType.FUTURE_DAILY) {
			unitStr = "日";
		} else if (reportType == ReportType.QUARTER
				|| reportType == ReportType.FUTURE_QUARTER) {
			unitStr = "季";
		} else if (reportType == ReportType.MONTH) {
			unitStr = "月";
		} else if (reportType == ReportType.HALF_YEAR) {
			unitStr = "半年";
		} else if (reportType == ReportType.YEAR) {
			unitStr = "年";
		}
		return unitStr;
	}

	/**
	 * 对当前日期节点的日期信息，按日期的最小单位进行拆解。
	 * 
	 * @return 拆解出的DateRange的ArrayList
	 * @throws UnexpectedException
	 * @see DateRange
	 */
	public ArrayList<DateRange> splitRangeToListOfDateRange()
			throws UnexpectedException {
		if (dateinfo == null) {
			throw new UnexpectedException(
					"DateNode does not have DateRange:%s", text);
		} else if (splitDates != null) {
			return splitDates;
		}
		String unitStr = this.getUnitStrOfDate();
		;

		if (unitStr == null) {
			return null;
		}
		int change = 1;
		Unit unit2Change = null;
		if (unitStr.equals("年")) {
			unit2Change = Unit.YEAR;
			if (countYear() == 0 && !isSequence) {
				return null;
			}
		} else if (unitStr.equals("月")) {
			unit2Change = Unit.MONTH;
			if (countMonth() > 24 || countMonth() == 0 && !isSequence) {
				return null;
			}
		} else if (unitStr.equals("季")) {
			unit2Change = Unit.MONTH;
			if (countMonth() < 3 && !isSequence || countMonth() == 0) {
				return null;
			}
			change = 3;
		} else if (unitStr.equals("周")) {
			unit2Change = Unit.DAY;
			if (countDay() < 6 && !isSequence || countDay() == 0) {
				return null;
			}
			change = 7;
		} else if (unitStr.equals("日")) {
			unit2Change = Unit.DAY;
			if (countDay() > 100 || countDay() == 0) {
				return null;
			}
		} else {
			;// No Op
		}
		ArrayList<DateRange> drs = DateUtil.getSplitByRange(dateinfo, change,
				unit2Change);
		drs.trimToSize();
		if (drs.size() < 2) {
			// 若拆分的时间段数小于2，则拆分无效,若该时间为连续型，则可将单位下降一级，再次尝试拆分
			return null;
		} else {
			return drs;
		}
	}

	/**
	 * 按时间最短单位对时间进行拆解。 若其间可拆解数量小于2，则返回null
	 * 
	 * @return
	 * @return String[]表示的时间段
	 * @throws UnexpectedException
	 */

	public ArrayList<String[]> splitRangeToListOfString()
			throws UnexpectedException {
		ArrayList<String[]> dateStr = null;
		ArrayList<DateRange> drs = splitRangeToListOfDateRange();
		if (drs == null) {
			return null;
		}
		dateStr = new ArrayList<String[]>();
		for (DateRange dr : drs) {
			String[] date = new String[2];
			date[0] = dr.getFrom().toString();
			date[1] = dr.getTo().toString();
			dateStr.add(date);
		}
		return dateStr;
	}

	public boolean hasSeveralDateCycle() throws UnexpectedException {
		return hasSeveralDateCycle(null);
	}

	/**
	 * 是否含有多个时间周期
	 * 
	 * @param reportType
	 * @param growthType
	 * @return
	 * @throws UnexpectedException
	 */
	public boolean hasSeveralDateCycle(ReportType reportType)
			throws UnexpectedException {
		if (isSequence || splitDates != null && splitDates.size() > 1) {
			// 若为连续，则默认其有多个时间周期
			// 或如“连续3年中报”，此种在解析时已经确认可拆分的，也是认为其有多个时间周期
			return true;
		}
		int cycles = 1;
		try {
			cycles = getRangeLen(getUnitOfDate());
		} catch (UnexpectedException e) {
			cycles = 0;
		}
		if (cycles > 1 && reportType == null) {
			return true;
		} else if (cycles <= 1
				&& (dateinfo.getLength() == 1 || reportType == null)) {
			// 当时间范围在解析是记录的长度为1，如“2010年”，则为非多个周期的时间
			return false;
		}
		switch (reportType) {
		case YEAR:
			return getRangeLen(Unit.YEAR) > 1;
		case HALF_YEAR:
			return getRangeLen(Unit.MONTH) > 6;
		case QUARTER:
			return getRangeLen(Unit.MONTH) > 3;
		case DAILY:
			return getRangeLen(Unit.DAY) > 1;
		case FUTURE_QUARTER:
			return getRangeLen(Unit.MONTH) > 3;
		case FUTURE_DAILY:
			return getRangeLen(Unit.DAY) > 1;
		default:
			assert (false);
		}
		return false;
	}

	/**
	 * 取得DateNode的起始日期。 若扩展日期信息不为空，则取扩展信息的起始日期； 若否，则若原始日期信息不为空，则取原始信息的起始日期；
	 * 否则，获取失败
	 * 
	 * @return 若获取成功，返回取得的起始日期的字符串形式信息，如“2012-01-01”；若失败，则返回null
	 */
	public String getFrom() {
		if(expandedRange!=null && expandedRange.getFrom()!=null) {
			return expandedRange.getFrom().toString();
		}else if(dateinfo != null && dateinfo.getFrom()!=null) {
			return dateinfo.getFrom().toString("");
		}else {
			return null;
		}
		
		///return expandedRange == null ? (dateinfo == null ? null : dateinfo.getFrom().toString("")) : expandedRange.getFrom().toString("");
	}

	public String getTo() {
		
		if(expandedRange!=null && expandedRange.getFrom()!=null) {
			return expandedRange.getTo().toString();
		}else if(dateinfo != null && dateinfo.getTo()!=null) {
			return dateinfo.getTo().toString("");
		}else {
			return null;
		}
		
		//return expandedRange == null ? dateinfo == null ? null : dateinfo.getTo().toString("") : expandedRange.getTo().toString("");
	}
	
	public int getLength() {
		return expandedRange == null ? dateinfo == null ? -1 : dateinfo.getLength() : expandedRange.getLength();
    }

	public String getRangeType() {
		if (dateinfo == null) {
			return null;
		} else {
			return dateinfo.getRangeType();
		}
	}

	public int getFromQuarter() {
		if (dateinfo == null) {
			return -1;
		} else {
			return DateUtil.getQuarterFromMonth(dateinfo.getFrom().getMonth());
		}
	}

	public int getToQuarter() {
		if (dateinfo == null) {
			return -1;
		} else {
			return DateUtil.getQuarterFromMonth(dateinfo.getTo().getMonth());
		}
	}

	public ArrayList<String> getTextOfOldNodes() {
		if (this.oldNodes.size() == 0) {
			return null;
		}
		ArrayList<String> oldText = new ArrayList<String>();
		for (int i = 0; i < this.oldNodes.size(); i++) {
			String text = this.oldNodes.get(i).text;
			oldText.add(text);
		}
		return oldText;
	}

	public void setExpandedRange(DateRange expandedRange) {
		this.expandedRange = expandedRange;
	}

	public DateRange getExpandedRange() {
		return expandedRange;
	}

	public String toString() {
		String str = super.toString();
		if (dateinfo == null) {
			str += "  DateUnit:UNKNOWN";
		} else {
			try {
				str += "  DateUnit:" + this.getUnitStrOfDate();
				str += "  Date:" + String.format("(%s)", dateinfo.toDateString());
				str += "  DateLen:" + this.getLength();
			} catch (UnexpectedException e) {
				str += "  DateUnit:UNKNOWN";
				str += "  Date:" + text;
			}
		}
		return str;
	}

	/*public DateNode clone() {
		DateNode rtn = (DateNode) (super.clone());
		if (dateinfo != null) {
			rtn.dateinfo = dateinfo.clone();
		}
		if (expandedRange != null) {
			rtn.expandedRange = expandedRange.clone();
		}
        if (time != null) {
            rtn.time = time.clone();
        }
		return rtn;
	}*/

	public boolean isFake() {
		return isFake;
	}
	
	

	public void setFake(boolean isFake) {
    	this.isFake = isFake;
    }

	public boolean isTradeDay() {
		return dateinfo.isTradeDay();
	}

	@Override
	public String getPubText() {
		ArrayList<String> oldTexts = getTextOfOldNodes();
		if (oldTexts == null)
			return text;
		else
			return Util.joinStr(oldTexts, "");
	}

	public ArrayList<DateRange> splitRangeToListOfDateRangeByReportType(
			ReportType reportType) throws UnexpectedException {
		Unit unitOfDate = getUnitOfDate();

		// 若时间的单位为“天”，则按报告期拆分
		boolean shouldSplitByReportType = unitOfDate == Unit.DAY;

		// 若时间的单位为“月”或“周”或“季”，则只有报告期不为Daily，则按报告期拆分
		shouldSplitByReportType |= (unitOfDate == Unit.MONTH
				|| unitOfDate == Unit.WEEK || unitOfDate == Unit.QUARTER)
				&& !(reportType == ReportType.DAILY || reportType == ReportType.FUTURE_DAILY);

		// 若时间的单位为“年”，则当时间为连续且长度为1，按报告期拆分。但不能按Daily的拆分，防止过度拆分
		// 如“连续一年涨跌幅大于10%”
		shouldSplitByReportType |= unitOfDate == Unit.YEAR
				&& isSequence
				&& dateinfo.getLength() == 1
				&& !(reportType == ReportType.DAILY || reportType == ReportType.FUTURE_DAILY);

		if (reportType == null || !shouldSplitByReportType) {
			// 若无报告期，或不符合按报告期拆分的条件，则按默认单位拆分
			return splitRangeToListOfDateRange();
		}
		if (splitDates != null) {
			return splitDates;
		}
		return DateUtil.splitDateRangeByReportType(dateinfo, reportType);
	}

	public void setSplitDates_(ArrayList<DateRange> tradeDates_) {
		this.splitDates = tradeDates_;
	}

	public ArrayList<DateRange> getSplitDates_() {
		return splitDates;
	}

	public void setRelatedDate(DateNode relatedDate) {
		this.relatedDate = relatedDate;
	}

	public DateNode getRelatedDate() {
		return relatedDate;
	}

	public ArrayList<DateRange> splitRangeByUnit(Unit unit) {
		if (splitDates != null) {
			return splitDates;
		}
		ArrayList<DateRange> splitRanges = null;
		try {
			splitRanges = DateUtil.splitDateRangeByUnit(dateinfo, unit);
		} catch (UnexpectedException e) {
			return null;
		}
		return splitRanges;
	}

	public void setFrequencyInfo(DateFrequencyInfo frequencyInfo) {
		this.frequencyInfo = frequencyInfo;
	}

	public DateFrequencyInfo getFrequencyInfo() {
		return frequencyInfo;
	}

	public boolean isPredict() {
		return dateinfo != null && dateinfo.getDateType() == DateType.FUTURE;
	}

	/**
	 * 判断该时间是否为数字型长度时间
	 */
	public boolean isLength() {
		return dateinfo != null && dateinfo.isLength();
	}

	public ArrayList<SemanticNode> oldNodes = new ArrayList<SemanticNode>();
	public CompareType compare = CompareType.EQUAL;
	public boolean needMoreIofo = false;
	public boolean isSequence = false;
	public boolean mayTrade = false;
	private DateRange dateinfo = null;
	private DateRange expandedRange = null;
	private ArrayList<DateRange> splitDates = null;
	private DateNode relatedDate = null;
	private DateFrequencyInfo frequencyInfo = null;
	private boolean isFake = false;
	/** 分时信息 */
    private TimeNode time = null;
    
    /** chunk字符串显示  */
    private String forShow;
	
    /**
	 * @return the forShow
	 */
	public String getForShow() {
		return forShow;
	}

	/**
	 * @param forShow the forShow to set
	 */
	public void setForShow(String forShow) {
		this.forShow = forShow;
	}

	public void setTime(TimeNode time) {
        this.time = time;
    }

    public TimeNode getTime() {
        return time;
    }
	/**
	 * 用于判断该时间节点是否包含起始和结束时间。
	 * @return
	 */
	public boolean hasStartAndEndFromDataInfo(){
		if(dateinfo!=null && dateinfo.getFrom()!=null && dateinfo.getTo()!=null){
			return true;
		}
	    return false;
	}
	
	public boolean isSequence() {
    	return isSequence;
    }

	/**
	 * 返回时间的中文标准说法
	 * 
	 * @author zd<zhangdong@myhexin.com>
	 * @return
	 * @throws UnexpectedException
	 */
	public String getChineseSay() throws UnexpectedException {
		assert (dateinfo != null);
		String rtn = null;
		if (getUnitOfDate() != Unit.DAY) {
			rtn = text;
		} else if (countDay() > 1) {
			rtn = String.format("%s年%s月%s日到%s年%s月%s日", dateinfo.getFrom()
					.getYear(), dateinfo.getFrom().getMonth(), dateinfo
					.getFrom().getDay(), dateinfo.getTo().getYear(), dateinfo
					.getTo().getMonth(), dateinfo.getTo().getDay());
		} else {
			rtn = String.format("%s年%s月%s日", dateinfo.getFrom().getYear(),
					dateinfo.getFrom().getMonth(), dateinfo.getFrom().getDay());
		}
		return rtn;
	}
	
	// 返回时间的描述，处理了频度和连续的问题
	public String getDateString() {
		StringBuilder sb = new StringBuilder();
		if (frequencyInfo != null) {
    		NumRange nr = frequencyInfo.getLength();
    		Unit unit = frequencyInfo.getUnit();
    		String temp = "";
    		String unitStr = EnumConvert.getStrFromUnit(unit);
    		if (nr.getLongFrom() == nr.getLongTo())
    			temp = text + "有" + nr.getLongFrom() + unitStr;
    		else if (nr.getLongFrom() != nr.getLongTo()) {
    			if (nr.getLongTo() <= dateinfo.getLength())
    				temp = text + "有" + nr.getLongFrom() + "-" +nr.getLongTo() + unitStr;
    			else
    				temp = text + "有大于等于" + nr.getLongFrom() + unitStr;
    		}
    		sb.append(temp);
    	} else if (isSequence && !text.contains("连续")) {
    		if (dateinfo.getLength() > 1)
    			sb.append("连续" + text);
    		else 
    			sb.append(text + "连续");
    	} else if (compare != CompareType.EQUAL) {
    		if (compare == CompareType.LONGER) {
    			sb.append(text + "以上");
    		} else if (compare == CompareType.SHORTER) {
    			sb.append(text + "以下");
    		}
    	}
    	else {
    		sb.append(text);
    	}
		return sb.toString();
	}
	
	/*
     * 比较各属性值是否相等
     * text			内容
     * isCombined	是否被合并
     * isBound		是否被绑定
     * compare		比较类型
     * isSequence	是否连续
     * dateinfo		时间属性
     * frequencyInfo时间频率属性
     */
	public boolean equals(Object obj) {
    	if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof DateNode))
			return false;
		final DateNode dn = (DateNode) obj;
		
		if (!this.text.equals(dn.text))
			return false;
		if (this.isCombined != dn.isCombined)
			return false;
		if (this.isBoundToIndex() != dn.isBoundToIndex())
			return false;
		if (this.compare != dn.compare)
			return false;
		if (this.isSequence != dn.isSequence)
			return false;
		if ((this.dateinfo == null && dn.dateinfo != null)
				|| (this.dateinfo != null && !this.dateinfo.equals(dn.dateinfo)))
			return false;
		if ((this.frequencyInfo == null && dn.frequencyInfo != null)
				|| (this.frequencyInfo != null && !this.frequencyInfo.equals(dn.frequencyInfo)))
			return false;
			
		return true;
    }

	@Override
	protected DateNode copy() {
		
		DateNode rtn = new DateNode();
		
		rtn.compare=compare;
		if(dateinfo!=null)
			rtn.dateinfo=dateinfo.clone();
		if(expandedRange!=null)
			rtn.expandedRange=expandedRange.clone();
		
		rtn.frequencyInfo=frequencyInfo;
		
		rtn.isFake=isFake;
		rtn.isSequence=isSequence;
		rtn.mayTrade=mayTrade;
		rtn.needMoreIofo=needMoreIofo;
		if(oldNodes!=null) rtn.oldNodes.addAll(oldNodes);
		if(relatedDate!=null) rtn.relatedDate= (DateNode)NodeUtil.copyNode(relatedDate);//.copy();
		
		if(splitDates!=null)
			rtn.splitDates=new ArrayList<DateRange>(splitDates);
		
		if(time!=null)
			rtn.time=(TimeNode) NodeUtil.copyNode(time); //.copy();
		
		if(StringUtils.isNotBlank(forShow)) {
			rtn.forShow = forShow;
		}
		
		//TODO dateNode不需要copy SemanticNode中变量
		super.copy(rtn);
		return rtn;
	}
	

}
