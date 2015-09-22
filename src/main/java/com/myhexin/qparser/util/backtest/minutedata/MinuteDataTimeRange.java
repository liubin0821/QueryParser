package com.myhexin.qparser.util.backtest.minutedata;

/**
 * 用于保存 分时策略的时间区间， 时间区间固定为0 到 240分钟。 0 为 开盘时间上午 9点半 240分钟为 收盘时间 下午15:00
 * 
 * 分时时间由具体的某一分钟 映射到 第几分钟。
 *
 */
public class MinuteDataTimeRange {

    private static int AM_START = 9 * 60 + 30;
    private static int AM_END = 11 * 60 + 30;
    
    private static int PM_START = 13 * 60;
    private static int PM_END = 15 * 60;
    
    private int startTime;

    private int endTime;

    public MinuteDataTimeRange(int sTime, int eTime) {
        if (sTime < 0 || sTime > 240 || eTime < 0 || eTime > 240) {
            throw new IllegalArgumentException("分时 开始时间和结束时间必须 在0 到 240 分钟之内");
        }
        this.startTime = sTime;
        this.endTime = eTime;
    }
    
    public int getStartTime() {
        return startTime;
    }

    public int getEndTime() {
        return endTime;
    }
    
    
    public static int convertToMinuteIndexTime(String minuteLevelTime) {
        String[] temp = minuteLevelTime.split(":");
        int hour = Integer.parseInt(temp[0]);
        int minuteValue = Integer.parseInt(temp[1]);

        int allMin = hour * 60 + minuteValue;
        if (allMin <= AM_START) {
            return 0;
        } else if (allMin > AM_START && allMin <= AM_END) {
            return allMin - AM_START;
        } else if (allMin > AM_END && allMin < PM_START) {
            return 120;
        } else if (allMin > PM_START && allMin <= PM_END) {
            return 120 + allMin - PM_START;
        } 
        return 240;
    }
}
