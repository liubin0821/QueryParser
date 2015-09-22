package com.myhexin.qparser.time.parse;

import java.util.regex.Matcher;

import com.myhexin.qparser.Param;
import com.myhexin.qparser.Query.Type;
import com.myhexin.qparser.conf.ModifyDefInfo;
import com.myhexin.qparser.conf.ModifyDefInfo.TimeModifyInfo;
import com.myhexin.qparser.define.EnumDef.BaseTimeUnit;
import com.myhexin.qparser.define.EnumDef.ReportType;
import com.myhexin.qparser.define.MiscDef;
import com.myhexin.qparser.define.MsgDef;
import com.myhexin.qparser.except.NotSupportedException;
import com.myhexin.qparser.except.QPException;
import com.myhexin.qparser.except.UnexpectedException;
import com.myhexin.qparser.node.TimeNode;
import com.myhexin.qparser.time.info.TimePoint;
import com.myhexin.qparser.time.info.TimeRange;
import com.myhexin.qparser.time.tool.TimeAdder;
import com.myhexin.qparser.time.tool.TimeUtil;

/**
 * 根据字符串获取时间
 */
public class TimeStrParser {
    
    public static TimeRange parse(String timeStr) throws 
            NotSupportedException {
        TimeRange range = null;
        try {
            range = parseHMS(timeStr);
            // 由数字及时间单位表示的单个日期
            range = range == null ? parseHM(timeStr) : range;
            range = range == null ? parseHour(timeStr) : range;
            range = range == null ? parseStates(timeStr) : range;//尾盘
            range = range == null ? parseLengthTime(timeStr) : range;
            range = range == null ? parseFromTime2AntherTime(timeStr) : range;
            range = range == null ? parseBeforeAndAfterTime(timeStr) : range;
            range = range == null ? parseBeforeTime(timeStr) : range;
            range = range == null ? parseAfterTime(timeStr) : range;
        } catch (QPException e) {
            throw new NotSupportedException(MsgDef.NOT_SUPPORTED_TIME_FMT, timeStr);
        }
        if (range == null || !range.isLegal()) {
            throw new NotSupportedException(MsgDef.NOT_SUPPORTED_TIME_FMT, timeStr);
        }
        return range;
    }
    
    private static TimeRange parseBeforeTime(String timeStr)
            throws NotSupportedException {
        Matcher mid = TimePatterns.IN_ONE_TIME_BEFORE.matcher(timeStr);
        if (!mid.matches())
            return null;
        if (Param.DEBUG_DATEINFO) {
            System.out.println("IN_ONE_TIME_BEFORE");
        }
        String timePart = mid.group(1);
        TimeRange timeRange = parse(timePart);
        TimeRange range = new TimeRange(TimeUtil.getMin(), timeRange.getFrom());
        return range;
    }

    private static TimeRange parseAfterTime(String timeStr)
            throws NotSupportedException {
        Matcher mid = TimePatterns.IN_ONE_TIME_AFTER.matcher(timeStr);
        if (!mid.matches())
            return null;
        if (Param.DEBUG_DATEINFO) {
            System.out.println("IN_ONE_TIME_AFTER");
        }
        String timePart = mid.group(1);
        TimeRange timeRange = parse(timePart);
        TimeRange range = new TimeRange(timeRange.getTo(), TimeUtil.getMax());
        return range;
    }

    private static TimeRange parseBeforeAndAfterTime(String timeStr) throws NotSupportedException {
        Matcher mid = TimePatterns.BEFORE_AND_AFTER
                .matcher(timeStr);
        if (!mid.matches())
            return null;
        if (Param.DEBUG_DATEINFO) {
            System.out.println("BEFORE_AND_AFTER");
        }
        String firstTimePart = mid.group(1);
        String firstTagPart = mid.group(2);
        String secTimePart = mid.group(3);
        String secTagPart = mid.group(4);
        boolean firstTagIsAfter = isAfterTag(firstTagPart);
        boolean secTagIsAfter = isAfterTag(secTagPart);
        if(firstTagIsAfter&&secTagIsAfter||!firstTagIsAfter&&!secTagIsAfter){
            throw new NotSupportedException("无法支持相同方向上的范围");
        }
        
        TimeRange firstRange = parse(firstTimePart);
        TimeRange secRange = parse(secTimePart);
        
        TimePoint from = firstTagIsAfter?firstRange.getTo():secRange.getTo();
        TimePoint to = firstTagIsAfter?secRange.getFrom():firstRange.getFrom();
        if(from.isAfter(to)){
            throw new NotSupportedException("范围错误");
        }
        TimeRange range = new TimeRange(from, to);
        if(firstRange.isCanNotAdjust()||secRange.isCanNotAdjust()){
            range.setCanNotAdjust();
        }
        return range;
    }

    private static TimeRange parseFromTime2AntherTime(String timeStr)
            throws NotSupportedException {
        Matcher mid = TimePatterns.FROM_ONE_TIME_TO_ANTHER_TIME
                .matcher(timeStr);
        if (!mid.matches())
            return null;
        if (Param.DEBUG_DATEINFO) {
            System.out.println("FROM_ONE_TIME_TO_ANTHER_TIME");
        }
        String firstPart = mid.group(1);
        String secPart = mid.group(2);
        TimeRange firstRange = parse(firstPart);
        if(firstPart.contains("下午")&&!secPart.contains("上午")&&!secPart.contains("下午"))//下午
        	secPart = "下午"+secPart;
        TimeRange secRange = parse(secPart);
        TimeRange range = new TimeRange(firstRange.getFrom(), secRange.getTo());
        if(firstRange.isCanNotAdjust()||secRange.isCanNotAdjust()){
            range.setCanNotAdjust();
        }
        return range;
    }

    /**
     * TODO:解析携带伪时间的字符串
     * @param timeStr
     * @param classLabel
     * @param reportType
     * @return
     * @throws NotSupportedException
     */
    public static TimeRange parseFakeTime(String timeStr, String classLabel,
            ReportType reportType, Type type) throws NotSupportedException {
        checkFakeTimeParam(timeStr, classLabel, reportType, type);
        TimeRange range = null;

        try {
         // 先假设为正常分时时间，尝试解析。若解析不出，则尝试按伪时间解析
            range = parse(timeStr);
        } catch (QPException e) {
            ;// No Op
        }
        try {
         // 假设为配置中有的伪时间，尝试解析。若解析不出，则尝试其他范围类型伪时间
            range = range == null ? parseOnlyFakeTime(timeStr, classLabel,
                    reportType, type) : range;
        } catch (QPException e) {
            ;// No Op
        }
        try {
            range = range == null ? parseFromTime2AntherFakeTime(timeStr,
                    classLabel, reportType, type) : range;
            range = range == null ? parseBeforeAndAfterFakeTime(timeStr,
                    classLabel, reportType, type) : range;
            range = range == null ? parseBeforeFakeTime(timeStr, classLabel,
                    reportType, type) : range;
            range = range == null ? parseAfterFakeTime(timeStr, classLabel,
                    reportType, type) : range;
        } catch (QPException e) {
            throw new NotSupportedException(MsgDef.NOT_SUPPORTED_TIME_FMT,
                    timeStr);
        }
        if (range == null || !range.isLegal()) {
            throw new NotSupportedException(MsgDef.NOT_SUPPORTED_TIME_FMT,
                    timeStr);
        }
        return range;

    }
  
    private static TimeRange parseAfterFakeTime(String timeStr,
            String classLabel, ReportType reportType, Type type) {
        Matcher mid = TimePatterns.IN_ONE_TIME_AFTER.matcher(timeStr);
        if (!mid.matches())
            return null;
        if (Param.DEBUG_DATEINFO) {
            System.out.println("IN_ONE_TIME_AFTER");
        }
        String timePart = mid.group(1);
        TimeRange timeRange = null;
        // 先按照伪时间假设解析，如果行不通，在按照正常的时间解析
        try {
            timeRange = parseFakeTime(timePart, classLabel, reportType, type);
        } catch (NotSupportedException e) {
            ;// No Op
        }

        TimeRange range = new TimeRange(timeRange.getTo(), TimeUtil.getMax());
        return range;
    }

    private static TimeRange parseBeforeFakeTime(String timeStr,
            String classLabel, ReportType reportType, Type type)
            throws NotSupportedException {
        Matcher mid = TimePatterns.IN_ONE_TIME_BEFORE.matcher(timeStr);
        if (!mid.matches())
            return null;
        if (Param.DEBUG_DATEINFO) {
            System.out.println("IN_ONE_TIME_BEFORE");
        }
        String timePart = mid.group(1);
        TimeRange timeRange = null;
        // 先按照伪时间假设解析，如果行不通，在按照正常的时间解析
        try {
            timeRange = parseFakeTime(timePart, classLabel, reportType, type);
        } catch (NotSupportedException e) {
            ;// No Op
        }
        timeRange = timeRange == null ? parse(timePart) : timeRange;
        TimeRange range = new TimeRange(TimeUtil.getMin(), timeRange.getFrom());
        return range;
    }

    private static TimeRange parseBeforeAndAfterFakeTime(String timeStr,
            String classLabel, ReportType reportType, Type type)
            throws NotSupportedException {
        Matcher mid = TimePatterns.BEFORE_AND_AFTER.matcher(timeStr);
        if (!mid.matches())
            return null;
        if (Param.DEBUG_DATEINFO) {
            System.out.println("BEFORE_AND_AFTER");
        }
        String firstTimePart = mid.group(1);
        String firstTagPart = mid.group(2);
        String secTimePart = mid.group(3);
        String secTagPart = mid.group(4);
        boolean firstTagIsAfter = isAfterTag(firstTagPart);
        boolean secTagIsAfter = isAfterTag(secTagPart);
        if (firstTagIsAfter && secTagIsAfter || !firstTagIsAfter
                && !secTagIsAfter) {
            throw new NotSupportedException("无法支持相同方向上的范围");
        }

        TimeRange firstRange = null;
        TimeRange secRange = null;
        // 先按照伪时间假设解析，如果行不通，在按照正常的时间解析
        try {
            firstRange = parseFakeTime(firstTimePart, classLabel, reportType,
                    type);
            secRange = parseFakeTime(secTimePart, classLabel, reportType, type);
        } catch (NotSupportedException e) {
            ;// No Op
        }
        firstRange = firstRange == null ? parse(firstTimePart) : firstRange;
        secRange = secRange == null ? parse(secTimePart) : secRange;

        TimePoint from = firstTagIsAfter ? firstRange.getTo() : secRange
                .getTo();
        TimePoint to = firstTagIsAfter ? secRange.getFrom() : firstRange
                .getFrom();
        if (from.isAfter(to)) {
            throw new NotSupportedException("范围错误");
        }
        TimeRange range = new TimeRange(from, to);
        if(firstRange.isCanNotAdjust()||secRange.isCanNotAdjust()){
            range.setCanNotAdjust();
        }
        return range;
}

    private static TimeRange parseFromTime2AntherFakeTime(String timeStr,
            String classLabel, ReportType reportType, Type type)
            throws NotSupportedException {
        Matcher mid = TimePatterns.FROM_ONE_TIME_TO_ANTHER_TIME
                .matcher(timeStr);
        if (!mid.matches())
            return null;
        if (Param.DEBUG_DATEINFO) {
            System.out.println("FROM_ONE_TIME_TO_ANTHER_TIME");
        }
        String firstPart = mid.group(1);
        String secPart = mid.group(2);
        TimeRange firstRange = null;
        TimeRange secRange = null;
        // 先按照伪时间假设解析，如果行不通，在按照正常的时间解析
        try {
            firstRange = parseFakeTime(firstPart, classLabel, reportType, type);
            secRange = parseFakeTime(secPart, classLabel, reportType, type);
        } catch (NotSupportedException e) {
            ;// No Op
        }
        firstRange = firstRange == null ? parse(firstPart) : firstRange;
        secRange = secRange == null ? parse(secPart) : secRange;

        TimeRange range = new TimeRange(firstRange.getFrom(), secRange.getTo());
        if(firstRange.isCanNotAdjust()||secRange.isCanNotAdjust()){
            range.setCanNotAdjust();
        }
        return range;
    }

    private static void checkFakeTimeParam(String timeStr,String classLabel,
            ReportType reportType, Type type) throws NotSupportedException {
        if (classLabel == null && reportType == null || type == null) {
            throw new NotSupportedException("未知领域及对应指标，无法解析伪时间(Time):%s",
                    timeStr);
        } else if (reportType == null) {
            /*try {
                ClassOnto classOnto = MemOnto.getOnto(classLabel,
                        ClassOnto.class, type);
                if (classOnto == null) {
                    throw new NotSupportedException(
                            "未知领域及对应指标，无法解析伪时间(Time):%s", timeStr);
                }
            } catch (UnexpectedException e) {
                throw new NotSupportedException("未知领域及对应指标，无法解析伪时间(Time):%s",
                        timeStr);
            }*/
        }
    }

    private static TimeRange parseOnlyFakeTime(String timeStr,
            String classLabel, ReportType reportType, Type type)
            throws UnexpectedException, NotSupportedException {
        TimeModifyInfo modifyInfo = ModifyDefInfo.getModifyTimeInfo(timeStr,
                classLabel, reportType, type);
        if (modifyInfo == null)
            throw new NotSupportedException("伪时间(Time)未在该领域下的该指标或报告期下定义:%s",
                    timeStr);

        TimeNode addTimeValue = modifyInfo.getModifyTimeValue();
        TimeRange range = parse(addTimeValue.getText());
        return range;
    }

    private static TimeRange parseLengthTime(String timeStr) throws NotSupportedException, UnexpectedException {
        TimeRange range = parseLengthTime_1(timeStr);
        range = range == null ? parseLengthTime_2(timeStr) : range;
        range = range == null ? parseLengthTime_3(timeStr) : range;
        range = range == null ? parseLengthTime_4(timeStr) : range;
        range = range == null ? parseLengthTime_5(timeStr) : range;
        range = range == null ? parseLengthTime_6(timeStr) : range;
        range = range == null ? parseAmOrPmOpenOrClose(timeStr) : range;
        return range;
    }
    
    private static TimeRange parseLengthTime_6(String timeStr) throws NotSupportedException {
        Matcher mid = TimePatterns.LENGTH_TYPE_RANGE_6.matcher(timeStr);
        if (!mid.matches())
            return null;
        if (Param.DEBUG_DATEINFO) {
            System.out.println("LENGTH_TYPE_RANGE_6");
        }
        String startTimePart = mid.group(1) + mid.group(2);
        String numPart = mid.group(3);
        String unitPart = mid.group(4);
        String tagStr = "之后";
        if(startTimePart.contains("尾盘"))	tagStr="之前";//尾盘特殊处理,支持"尾盘30分钟",默认往前推
        String remakeTimeStr = String.format("%s%s%s%s", startTimePart,tagStr,numPart,unitPart);
        TimeRange range = parse(remakeTimeStr);
        return range;
    }

    private static TimeRange parseLengthTime_5(String timeStr)
            throws UnexpectedException, NotSupportedException {
        Matcher mid = TimePatterns.LENGTH_TYPE_RANGE_5.matcher(timeStr);
        if (!mid.matches())
            return null;
        if (Param.DEBUG_DATEINFO) {
            System.out.println("LENGTH_TYPE_RANGE_5");
        }
        String startTimePart = mid.group(1);
        String tagStr = mid.group(2);
        String numPart = mid.group(3);
        String unitPart = mid.group(4);
        boolean isHalf = "半".equals(numPart);
        BaseTimeUnit timeUnit = TimeUtil.getTimeUnit(unitPart);
        if (isHalf && timeUnit != BaseTimeUnit.HOUR) {
            throw new NotSupportedException("暂不支持 “半”修饰“小时”外的时间(Time)单位");
        }
        int num = isHalf ? 30 : Integer.valueOf(numPart);
        timeUnit = isHalf ? BaseTimeUnit.MIN : timeUnit;
        boolean isAfter = isAfterTag(tagStr);
        TimeRange startTimeRange = parseAmOrPmOpenOrClose(startTimePart);
        if (startTimeRange == null) {
            throw new NotSupportedException(MsgDef.NOT_SUPPORTED_TIME_FMT, timeStr);
        }
        TimePoint startTime = isAfter ? startTimeRange.getTo() : startTimeRange
                .getFrom();
        num = isAfter ? -num : num;
        TimePoint newTime = TimeAdder.getNewTime(startTime, timeUnit, num);
        TimeRange range = isAfter ? new TimeRange(newTime, startTime)
                : new TimeRange(startTime, newTime);
        range.setCanNotAdjust();
        return range;

    }

    
    
    private static TimeRange parseLengthTime_4(String timeStr)
            throws UnexpectedException, NotSupportedException {
        Matcher mid = TimePatterns.LENGTH_TYPE_RANGE_4.matcher(timeStr);
        if (!mid.matches())
            return null;
        if (Param.DEBUG_DATEINFO) {
            System.out.println("LENGTH_TYPE_RANGE_4");
        }
        String startTimePart = mid.group(1) + mid.group(2);
        String tagStr = mid.group(4);
        String numPart = mid.group(3);
        String unitPart = mid.group(4);
        boolean isHalf = "半".equals(numPart);
        BaseTimeUnit timeUnit = TimeUtil.getTimeUnit(unitPart);
        if (isHalf && timeUnit != BaseTimeUnit.HOUR) {
            throw new NotSupportedException("暂不支持 “半”修饰“小时”外的时间(Time)单位");
        }
        int num = isHalf ? 30 : Integer.valueOf(numPart);
        timeUnit = isHalf ? BaseTimeUnit.MIN : timeUnit;
        boolean isAfter = isAfterTag(tagStr);
        num = isAfter ? num : -num;
        TimeRange startTimeRange = parseAmOrPmOpenOrClose(startTimePart);
        if (startTimeRange == null) {
            throw new NotSupportedException(MsgDef.NOT_SUPPORTED_TIME_FMT, timeStr);
        }
        TimePoint startTime = startTimeRange.getFrom();
        TimePoint newTime = TimeAdder.getNewTime(startTime, timeUnit, num);
        TimeRange range = isAfter ? new TimeRange(startTime, newTime)
                : new TimeRange(newTime, startTime);
        range.setCanNotAdjust();
        return range;
    }

    
    
    private static TimeRange parseLengthTime_3(String timeStr)
            throws UnexpectedException, NotSupportedException {
        Matcher mid = TimePatterns.LENGTH_TYPE_RANGE_3.matcher(timeStr);
        if (!mid.matches())
            return null;
        if (Param.DEBUG_DATEINFO) {
            System.out.println("LENGTH_TYPE_RANGE_3");
        }
        String startTimePart = mid.group(1) + mid.group(2);
        String tagStr = mid.group(3);
        String numPart = mid.group(4);
        String unitPart = mid.group(5);
        boolean isHalf = "半".equals(numPart);
        BaseTimeUnit timeUnit = TimeUtil.getTimeUnit(unitPart);
        if (isHalf && timeUnit != BaseTimeUnit.HOUR) {
            throw new NotSupportedException("暂不支持 “半”修饰“小时”外的时间(Time)单位");
        }
        int num = isHalf ? 30 : Integer.valueOf(numPart);
        timeUnit = isHalf ? BaseTimeUnit.MIN : timeUnit;
        boolean isAfter = isAfterTag(tagStr);
        num = isAfter ? num : -num;
        TimeRange startTimeRange = parseAmOrPmOpenOrClose(startTimePart);
        if (startTimeRange == null) {
            throw new NotSupportedException(MsgDef.NOT_SUPPORTED_TIME_FMT, timeStr);
        }
        TimePoint startTime = startTimeRange.getFrom();
        TimePoint newTime = TimeAdder.getNewTime(startTime, timeUnit, num);
        TimeRange range = isAfter ? new TimeRange(startTime, newTime)
                : new TimeRange(newTime, startTime);
        range.setCanNotAdjust();
        return range;
    }
   
    
    private static TimeRange parseAmOrPmOpenOrClose(String timeStr)
            throws NotSupportedException, UnexpectedException {
        Matcher mid = TimePatterns.AM_OR_PM_OPEN_OR_CLOSE.matcher(timeStr);
        if (!mid.matches() || timeStr.isEmpty())
            return null;
        if (Param.DEBUG_DATEINFO) {
            System.out.println("AM_OR_PM_OPEN_OR_CLOSE");
        }
        String amOrPm = mid.group(1);
        String openOrClose = mid.group(2);
        TimeRange amOpen = parse(MiscDef.AM_OPEN);
        TimeRange amClose = parse(MiscDef.AM_CLOSE);
        TimeRange pmOpen = parse(MiscDef.PM_OPEN);
        TimeRange pmClose = parse(MiscDef.PM_CLOSE);
        TimeRange weiPan = parse(MiscDef.STATE_WP);
        TimeRange range = null;
        if (("上午".equals(amOrPm) || amOrPm.isEmpty())
                && "开盘".equals(openOrClose)) {
            range = amOpen;
        } else if ("下午".equals(amOrPm) && "开盘".equals(openOrClose)) {
            range = pmOpen;
        } else if (("下午".equals(amOrPm) || amOrPm.isEmpty())
                && "收盘".equals(openOrClose)) {
            range = pmClose;
        } else if ("上午".equals(amOrPm) && "收盘".equals(openOrClose)) {
            range = amClose;
        } else if ("上午".equals(amOrPm) && openOrClose.isEmpty()) {
            range = new TimeRange(amOpen.getFrom(), amClose.getTo());
        } else if ("下午".equals(amOrPm) && openOrClose.isEmpty()) {
            range = new TimeRange(pmOpen.getFrom(), pmClose.getTo());
        } else if("尾盘".equals(openOrClose)){
        	range = weiPan;
        }else {
            throw new NotSupportedException(MsgDef.NOT_SUPPORTED_TIME_FMT, timeStr);
        }
        range.setCanNotAdjust();
        return range;
    }

    private static TimeRange parseLengthTime_2(String timeStr)
            throws UnexpectedException, NotSupportedException {
        Matcher mid = TimePatterns.LENGTH_TYPE_RANGE_2.matcher(timeStr);
        if (!mid.matches())
            return null;
        if (Param.DEBUG_DATEINFO) {
            System.out.println("LENGTH_TYPE_RANGE_2");
        }
        String numPart = mid.group(1);
        String unitPart = mid.group(2);
        String tagStr = mid.group(3);
        boolean isHalf = "半".equals(numPart);
        BaseTimeUnit timeUnit = TimeUtil.getTimeUnit(unitPart);
        if (isHalf && timeUnit != BaseTimeUnit.HOUR) {
            throw new NotSupportedException("暂不支持 “半”修饰“小时”外的时间(Time)单位");
        }
        TimePoint now = TimeUtil.getNow();
        int num = isHalf ? 30 : Integer.valueOf(numPart);
        timeUnit = isHalf ? BaseTimeUnit.MIN : timeUnit;
        boolean isAfter = isAfterTag(tagStr);
        TimePoint point = TimeAdder.getNewTime(now, timeUnit, isAfter ? num : -num);
        TimeRange range = new TimeRange(point, point);
        return range;
    }

    private static boolean isAfterTag(String tagStr) {
        if (tagStr == null || tagStr.isEmpty())
            return false;
        return tagStr.matches("以后|之后|后");
    }

    private static TimeRange parseLengthTime_1(String timeStr) throws NotSupportedException, UnexpectedException {
        Matcher mid = TimePatterns.LENGTH_TYPE_RANGE_1.matcher(timeStr);
        if (!mid.matches())
            return null;
        if (Param.DEBUG_DATEINFO) {
            System.out.println("LENGTH_TYPE_RANGE_1");
        }
        String numPart = mid.group(1);
        String unitPart = mid.group(2);
        boolean isHalf = "半".equals(numPart);
        BaseTimeUnit timeUnit = TimeUtil.getTimeUnit(unitPart);
        if (isHalf && timeUnit != BaseTimeUnit.HOUR) {
            throw new NotSupportedException("暂不支持 “半”修饰“小时”外的时间(Time)单位");
        }
        TimePoint now = TimeUtil.getNow();
        int num = isHalf ? 30 : Integer.valueOf(numPart);
        timeUnit = isHalf ? BaseTimeUnit.MIN : timeUnit;
        TimePoint from = TimeAdder.getNewTime(now, timeUnit, -num);
        TimeRange range = new TimeRange(from, now);
        range.setIsLength();
        range.setLength(num);
        range.setUnit(timeUnit);
        return range;
    }

    private static TimeRange parseHour(String timeStr) {
        Matcher mid = TimePatterns.ONLY_HOUR_WITH_AM_OR_PM.matcher(timeStr);
        if (!mid.matches())
            return null;
        if (Param.DEBUG_DATEINFO) {
            System.out.println("ONLY_HOUR_WITH_AM_OR_PM");
        }
        String amOrPm = mid.group(1);
        String hourStr = mid.group(2);
        int hour = Integer.valueOf(hourStr);
        hour = reParseHourByAmOrPm(amOrPm, hour);
        TimePoint point = new TimePoint(hour, 0, 0);
        TimeRange range = new TimeRange(point, point);
        range.setCanNotAdjust();
        return range;
    }
    
    private static TimeRange parseStates(String timeStr){
    	Matcher mid = TimePatterns.STATES.matcher(timeStr);
        if (!mid.matches())
            return null;
        if (Param.DEBUG_DATEINFO) {
            System.out.println("STATES");
        }
        String state = mid.group(1);
        TimePoint fromPoint = null;
        TimePoint toPoint = null;
        if(state.equals("尾盘")){
        	fromPoint = new TimePoint(14, 30);
        	toPoint = new TimePoint(15, 0);
        }
        TimeRange range = new TimeRange(fromPoint, toPoint);
        range.setCanNotAdjust();
        return range;
    }
    
    private static TimeRange parseHMS(String timeStr) {
        Matcher mid = TimePatterns.HMS.matcher(timeStr);
        if (!mid.matches())
            return null;
        if (Param.DEBUG_DATEINFO) {
            System.out.println("HMS");
        }
        String amOrPm = mid.group(1);
        String hourStr = mid.group(2);
        String minStr = mid.group(3);
        String secStr = mid.group(4);
        int hour = Integer.valueOf(hourStr);
        int min = Integer.valueOf(minStr);
        int sec = Integer.valueOf(secStr);
        hour = reParseHourByAmOrPm(amOrPm,hour);
        TimePoint point = new TimePoint(hour, min, sec);
        TimeRange range = new TimeRange(point, point);
        range.setCanNotAdjust();
        return range;
    }
    
    private static TimeRange parseHM(String timeStr) {
        TimeRange range = parseNormalHM(timeStr);
        range = range == null ? parseHalfHM(timeStr)
                : range;
        return range;
    }

    private static TimeRange parseNormalHM(String timeStr) {
        Matcher mid = TimePatterns.HM_1.matcher(timeStr);
        if (!mid.matches())
            return null;
        if (Param.DEBUG_DATEINFO) {
            System.out.println("HM_1");
        }
        String amOrPm = mid.group(1);
        String hourStr = mid.group(2);
        String minStr = mid.group(3);
        int hour = Integer.valueOf(hourStr);
        int min = Integer.valueOf(minStr);
        hour = reParseHourByAmOrPm(amOrPm,hour);
        TimePoint point = new TimePoint(hour, min, 0);
        TimeRange range = new TimeRange(point, point);
        range.setCanNotAdjust();
        return range;
    }
    

    private static int reParseHourByAmOrPm(String amOrPm, int hour) {
        boolean isPM = "下午".equals(amOrPm);
        if (!isPM || hour >= 12) {
            return hour;
        }
        return hour + 12;
    }


    private static TimeRange parseHalfHM(String timeStr) {
        Matcher mid = TimePatterns.HM_2.matcher(timeStr);
        if (!mid.matches())
            return null;
        if (Param.DEBUG_DATEINFO) {
            System.out.println("HM_2");
        }
        String repStr = timeStr.replace("半", "30分");
        return parseNormalHM(repStr);
    }

   
}
