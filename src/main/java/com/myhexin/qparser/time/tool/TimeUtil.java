package com.myhexin.qparser.time.tool;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.myhexin.qparser.Query.Type;
import com.myhexin.qparser.define.EnumDef.BaseTimeUnit;
import com.myhexin.qparser.define.EnumDef.NodeType;
import com.myhexin.qparser.define.EnumDef.ReportType;
import com.myhexin.qparser.define.EnumDef.TradePointType;
import com.myhexin.qparser.define.MiscDef;
import com.myhexin.qparser.define.MsgDef;
import com.myhexin.qparser.define.OperDef;
import com.myhexin.qparser.except.NotSupportedException;
import com.myhexin.qparser.except.QPException;
import com.myhexin.qparser.except.UnexpectedException;
import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.node.TimeNode;
import com.myhexin.qparser.time.info.TimePoint;
import com.myhexin.qparser.time.info.TimeRange;
import com.myhexin.qparser.time.parse.TimePatterns;
import com.myhexin.qparser.time.parse.TimeStrParser;
import com.myhexin.qparser.util.Util;

public class TimeUtil {

    /**
     * 获取当前系统时间,暂只取到分钟，秒用00
     * 
     * @return
     */
    public static TimePoint getNow() {
        Calendar cnow = Calendar.getInstance();
        TimePoint now = new TimePoint(cnow.get(Calendar.HOUR_OF_DAY),
                cnow.get(Calendar.MINUTE));
        return now;
    }
    
    

    /**
     * TimePoint转换为Calendar
     * 
     * @param node
     *            需转换TimePoint
     * @return 转换出的Calendar
     */
    public static Calendar getCalendar(TimePoint time) {
        if (time == null)
            return null;
        Calendar calendar = Calendar.getInstance();
        calendar.set(2000, 1, 1);
        calendar.set(java.util.Calendar.HOUR_OF_DAY, time.getHour());
        calendar.set(java.util.Calendar.MINUTE, time.getMin());
        calendar.set(java.util.Calendar.SECOND, time.getSec());
        return calendar;
    }

    public static TimePoint getMin() {
        TimePoint min = new TimePoint(0, 0, 0);
        return min;
    }

    public static TimePoint getMax() {
        TimePoint max = new TimePoint(23, 59, 59);
        return max;
    }

    public static BaseTimeUnit getTimeUnit(String unitStr)
            throws NotSupportedException {
        BaseTimeUnit unit = null;
        if (unitStr.matches("个?小时")) {
            unit = BaseTimeUnit.HOUR;
        } else if (unitStr.matches("^分钟|分$")) {
            unit = BaseTimeUnit.MIN;
        } else if (unitStr.matches("秒")) {
            unit = BaseTimeUnit.SEC;
        } else {
            throw new NotSupportedException("Not Supported Time Unit:%s",
                    unitStr);
        }
        return unit;
    }

    public static TimeNode makeTimeNodeFromStr(String valStr, Type type)
            throws NotSupportedException {
        TimeNode valNode = new TimeNode(valStr);
        TimeRange timeRange;
        try {
            timeRange = TimeStrParser.parse(valStr);
        } catch (NotSupportedException e) {
            throw new NotSupportedException(MsgDef.NOT_SUPPORTED_TIME_FMT,
                    valStr);
        }
        valNode.setTimeRange(timeRange);
        return valNode;
    }

    public static TimeNode makeTimeNodeFromStr(String timeStr,
            String classLabel, ReportType reportType, Type type)
            throws NotSupportedException {
        TimeNode valNode = new TimeNode(timeStr);
        TimeRange timeRange = null;
        try {
            timeRange = TimeStrParser.parseFakeTime(timeStr, classLabel,
                    reportType, type);
        } catch (NotSupportedException e) {
            ;// No Op
        }
        try {
            timeRange = timeRange == null ? TimeStrParser.parse(timeStr)
                    : timeRange;
        } catch (NotSupportedException e) {
            throw new NotSupportedException(MsgDef.NOT_SUPPORTED_TIME_FMT,
                    timeStr);
        }
        valNode.setTimeRange(timeRange);
        return valNode;
    }

    public static void main(String[] args) {
        System.out.println(getNow());
    }

    /**
     * 在交易时间中
     * 
     * @param timePoint
     * @return
     * @throws NotSupportedException
     * @throws UnexpectedException 
     */
    public static boolean isInTrade(TimePoint timePoint)
            throws NotSupportedException, UnexpectedException {
        return isInAMTrade(timePoint) || isInPMTrade(timePoint);
    }

    /**
     * 在上午交易时间中
     * 
     * @param timePoint
     * @return
     * @throws NotSupportedException
     * @throws UnexpectedException
     */
    public static boolean isInAMTrade(TimePoint timePoint)
            throws NotSupportedException, UnexpectedException {
        TimePoint amOpen = getTradePoint(TradePointType.AM_OPEN);
        TimePoint amClose = getTradePoint(TradePointType.AM_CLOSE);
        boolean isIn = timePoint.equals(amOpen) || timePoint.isAfter(amOpen);
        isIn = isIn
                && (timePoint.equals(amClose) || timePoint.isEarlier(amClose));
        return isIn;
    }

    /**
     * 在下午交易时间中
     * 
     * @param timePoint
     * @return
     * @throws NotSupportedException
     * @throws UnexpectedException
     */
    public static boolean isInPMTrade(TimePoint timePoint)
            throws NotSupportedException, UnexpectedException {
        TimePoint pmOpen = getTradePoint(TradePointType.PM_OPEN);
        TimePoint pmClose = getTradePoint(TradePointType.PM_CLOSE);
        boolean isIn = timePoint.equals(pmOpen) || timePoint.isAfter(pmOpen);
        isIn = isIn
                && (timePoint.equals(pmClose) || timePoint.isEarlier(pmClose));
        return isIn;
    }

    public static TimePoint getLatestTradePoint(TimePoint timePoint) throws NotSupportedException,
            UnexpectedException {
        TimePoint amOpen = getTradePoint(TradePointType.AM_OPEN);
        TimePoint amClose = getTradePoint(TradePointType.AM_CLOSE);
        TimePoint pmClose = getTradePoint(TradePointType.PM_CLOSE);
        if (isInTrade(timePoint))
            return timePoint;
        if (timePoint.isEarlier(amOpen)) {
            throw new NotSupportedException("暂不支持开盘前时间前推");
        }
        return timePoint.isAfter(pmClose)?pmClose:amClose;
    }
    
    public static TimePoint getNextTradePoint(TimePoint timePoint)
            throws NotSupportedException, UnexpectedException {
        TimePoint amOpen = getTradePoint(TradePointType.AM_OPEN);
        TimePoint pmOpen = getTradePoint(TradePointType.PM_OPEN);
        TimePoint pmClose = getTradePoint(TradePointType.PM_CLOSE);
        if (isInTrade(timePoint))
            return timePoint;
        if (timePoint.isAfter(pmClose)) {
            throw new NotSupportedException("暂不支持下午收盘前时间后推");
        }
        return timePoint.isEarlier(amOpen) ? amOpen : pmOpen;
    }

    public static TimePoint getTradePoint(TradePointType pointType)
            throws NotSupportedException, UnexpectedException {
        if (pointType == null) {
            throw new UnexpectedException("Param is NULL");
        }
        TimeRange tradeRange = null;
        try {
            switch (pointType) {
            case AM_OPEN:
                tradeRange = TimeStrParser.parse(MiscDef.AM_OPEN);
                break;
            case AM_CLOSE:
                tradeRange = TimeStrParser.parse(MiscDef.AM_CLOSE);
                break;
            case PM_OPEN:
                tradeRange = TimeStrParser.parse(MiscDef.PM_OPEN);
                break;
            case PM_CLOSE:
                tradeRange = TimeStrParser.parse(MiscDef.PM_CLOSE);
                break;
            default:
                break;
            }
        } catch (QPException e) {
            throw new NotSupportedException("Not Support Type:%s", pointType);
        }
        if (tradeRange == null || !OperDef.EQ.equals(tradeRange.getRangeType())) {
            throw new NotSupportedException("Not Support Type:%s", pointType);
        }
        return tradeRange.getFrom();

    }
    
    public static boolean nextHasTechNodes(ArrayList<SemanticNode> nodes, int nodePos)
            throws UnexpectedException {
        if (nodes == null || nodePos < 0 || nodePos >= nodes.size()) {
            throw new UnexpectedException("Bad Param");
        }
        int nextNodePos = nodePos + 1;
        if (nextNodePos < nodes.size()
                && Util.StringInTechLine(nodes.get(nextNodePos).getText())) {
            return true;
        }
        int skipStep = 0/*TechUtil.existTechOnRightNear(nodes, nodePos)*/;
        if (skipStep != 0) {
            return true;
        }
        return false;
    }
    
    public static boolean isLastKnownNode(int curChunkPos,
            List<SemanticNode> curChunkNodes) throws UnexpectedException {
        if (curChunkNodes == null || curChunkPos < 0
                || curChunkPos >= curChunkNodes.size()) {
            throw new UnexpectedException("Bad Param");
        }
        boolean rtn = curChunkPos == curChunkNodes.size() - 1;
        while (++curChunkPos < curChunkNodes.size()) {
            SemanticNode curNode = curChunkNodes.get(curChunkPos);
            if (curNode.type != NodeType.UNKNOWN) {
                rtn = false;
                break;
            }
        }
        return rtn;
    }
    
    public static boolean isMatchTimePoint(String text){
		boolean isMatchTimePoint = false;
		isMatchTimePoint = isMatchTimePoint || TimePatterns.HMS.matcher(text).matches();
		isMatchTimePoint = isMatchTimePoint || TimePatterns.HM_1.matcher(text).matches();
		isMatchTimePoint = isMatchTimePoint || TimePatterns.HM_2.matcher(text).matches();
		isMatchTimePoint = isMatchTimePoint || TimePatterns.ONLY_HOUR_WITH_AM_OR_PM.matcher(text).matches();
		return isMatchTimePoint;
	}
    
    public static TimePoint getLastTradePoint(TimePoint timePoint) throws NotSupportedException,
    UnexpectedException {
		TimePoint amOpen = getTradePoint(TradePointType.AM_OPEN);
		TimePoint amClose = getTradePoint(TradePointType.AM_CLOSE);
		TimePoint pmOpen = getTradePoint(TradePointType.PM_OPEN);
		TimePoint pmClose = getTradePoint(TradePointType.PM_CLOSE);
		if (timePoint.isEarlier(amOpen)) {
		    throw new NotSupportedException("暂不支持开盘前时间前推");
		}
		if(timePoint.equals(amOpen)){
			return amOpen;
		}else if(timePoint.isAfter(amOpen) && (timePoint.isEarlier(amClose)||timePoint.equals(amClose))){
			return amOpen;
		}else if(timePoint.equals(pmOpen)){
			return amOpen;
		}else if(timePoint.isAfter(pmOpen) && (timePoint.isEarlier(pmClose)||timePoint.equals(pmClose))){
			return pmOpen;
		}
		return null;
    }
    
    /**
     * 时间推算到交易时间
     * @param timeRange
     * @return
     * @throws UnexpectedException 
     * @throws NotSupportedException 
     */
    public static TimeRange adjustTimeToTrade(TimeRange timeRange) throws NotSupportedException, UnexpectedException{
    	TimePoint from = timeRange.getFrom();
    	TimePoint to = timeRange.getTo();
    	TimePoint fromAdjusted = from;
    	TimePoint toAdjusted = to;
		if(isInTrade(from) && isInTrade(to))//在交易时间内
			return timeRange;
		
		TimePoint amOpen = getTradePoint(TradePointType.AM_OPEN);
        TimePoint amClose = getTradePoint(TradePointType.AM_CLOSE);
    	TimePoint pmOpen = getTradePoint(TradePointType.PM_OPEN);
        TimePoint pmClose = getTradePoint(TradePointType.PM_CLOSE);
        
        //获得from与to之间的时间差
        Calendar fromCalendar = getCalendar(from);
        Calendar toCalendar = getCalendar(to);
        int minutes = (int)((toCalendar.getTimeInMillis()-fromCalendar.getTimeInMillis())/60000.0);
        boolean isLength = timeRange.isLength();//true:按时间差推算	false:调整到开收盘的时间点
        
        if(to.isEarlier(amOpen)){//to早于开盘时间	
        	return null;
        }else if(to.isAfter(amClose) && to.isEarlier(pmOpen)){//to晚于上午收盘时间，早于下午开盘时间
        	toAdjusted = getLatestTradePoint(to);
        }else if(to.isAfter(pmClose)){//to晚于下午收盘时间
        	toAdjusted = getLatestTradePoint(to);
        }/*else if(to.equals(amOpen)){//上午开盘时间
        }else if(to.equals(pmOpen)){//下午开盘时间
        	if(millis>0)
        		toAdjusted = amClose;
        }*/
        
        if(isLength){//按时间差推算
        	Calendar toAdjustedCal = getCalendar(toAdjusted);
	        toAdjustedCal.add(Calendar.MINUTE, -1*minutes);
	        java.util.Date date = toAdjustedCal.getTime();
	        fromAdjusted = new TimePoint(date.getHours(), date.getMinutes());
	        if(!isInTrade(fromAdjusted))
	        	fromAdjusted = getLastTradePoint(toAdjusted);
        }else{//按上下午开收盘的时间点推算
        	fromAdjusted = getLastTradePoint(toAdjusted);
        }
        timeRange.reSetRange(fromAdjusted, toAdjusted);
    	return timeRange;
    }
    
    

}
