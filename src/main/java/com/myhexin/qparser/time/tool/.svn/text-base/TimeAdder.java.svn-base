package com.myhexin.qparser.time.tool;

import java.util.Calendar;

import com.myhexin.qparser.define.EnumDef.BaseTimeUnit;
import com.myhexin.qparser.except.UnexpectedException;
import com.myhexin.qparser.time.info.TimePoint;

public class TimeAdder {
    /**
     * 根据时间基点和需变化的单位和单位长度，获取新时间
     * @param node 时间基点
     * @param dateUnit 时间变化单位 包括{@link BaseDateUnit.YEAR}
     *            {@link BaseDateUnit.MONTH}
     *            {@link BaseDateUnit.DAY}
     * @param n2Change 时间变化单位个数，正则向前，负则向后
     * @return 新的DateInfoNode
     * @throws UnexpectedException 
     */
    public static TimePoint getNewTime(TimePoint orgTime,
            BaseTimeUnit timeUnit, int n2Change) throws UnexpectedException {
        if (orgTime == null || timeUnit == null)
            throw new UnexpectedException("Param is NULL");
        Calendar date = TimeUtil.getCalendar(orgTime);
        date.add(parseCalendarUnit(timeUnit), n2Change);
        int hour = date.get(Calendar.HOUR_OF_DAY);
        int min = date.get(Calendar.MINUTE);
        int sec = date.get(Calendar.SECOND);
        TimePoint newNode = new TimePoint(hour, min, sec);
        return newNode;
    }
    /**
     * 根据给定的基本时间单位推断是天、月还是年
     * @param dateUnit  给定基本单位时间
     * @return 天、月或年
     * @throws UnexpectedException
     **/
    private static int parseCalendarUnit(BaseTimeUnit timeUnit)
            throws UnexpectedException {
        if (timeUnit == null) {
            throw new UnexpectedException("Unexpected Time Unit:NULL");
        }
        switch (timeUnit) {
        case HOUR:
            return Calendar.HOUR_OF_DAY;
        case MIN:
            return Calendar.MINUTE;
        case SEC:
            return Calendar.SECOND;
        default:
            throw new UnexpectedException("Unexpected Time Unit:%s", timeUnit);
        }
    }
}
