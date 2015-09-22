package com.myhexin.qparser.time.info;

import com.myhexin.qparser.except.QPException;
import com.myhexin.qparser.time.tool.TimeUtil;

/**
 * 记录一个时间点，为24小时制。
 */
public class TimePoint implements Cloneable{
    
    private int hour = 0;
    private int min = 0;
    private int sec = 0;

    /**
     * @param hour 小时
     * @param min 分钟
     */
    public TimePoint(int hour, int min) {
       this.hour =hour;
       this.min=min;
    }
    
    /**
     * @param hour 小时
     * @param min 分钟
     * @param sec 秒
     */
    public TimePoint(int hour, int min,int sec) {
       this.hour =hour;
       this.min=min;
       this.sec = sec;
    }
    
    public int getHour() {
        return hour;
    }

    public int getMin() {
        return min;
    }

    public int getSec() {
        return sec;
    }
    
    public String toString() { 
        String hourStr = String.format(hour < 10 ? "0%d" : "%d", hour);
        String minStr = String.format(min < 10 ? "0%d" : "%d", min);
        String secStr = String.format(sec < 10 ? "0%d" : "%d", sec);
        String rtn = String.format("%s:%s:%s", hourStr, minStr, secStr);
        return rtn;
    }
    
	public String toStringWithOutSecond() {
		String hourStr = String.format(hour < 10 ? "0%d" : "%d", hour);
		String minStr = String.format(min < 10 ? "0%d" : "%d", min);
		String rtn = String.format("%s:%s", hourStr, minStr);
		return rtn;
	}

    /**
     * 判断当前时间是否晚于比较时间
     * @param compare 比较时间
     * @return 当前时间晚于 compare，则返回true
     */
    public boolean isAfter(TimePoint compare) {
        return TimeUtil.getCalendar(this).after(TimeUtil.getCalendar(compare));
    }
   
   /**
    * 判断当前时间是否早于比较时间
    * @param compare 比较时间
    * @return 当前时间早于 compare，则返回true
    */
    public boolean isEarlier(TimePoint compare) {
        return TimeUtil.getCalendar(this).before(TimeUtil.getCalendar(compare));
    }
    
    public boolean equals(TimePoint compare) {
        if (compare == null)
            return false;
        return this.hour == compare.hour && this.min == compare.min
                && this.sec == compare.sec;
    }
    
    public boolean isLegal() {
        boolean hourIsLegal = hour >= 0 && hour < 24;
        boolean minIsLegal = min >= 0 && min < 60;
        boolean secIsLegal = sec >= 0 && sec < 60;
        return hourIsLegal && minIsLegal && secIsLegal;
    }
    
    public boolean isMax() {
       return this.equals(TimeUtil.getMax());
    }
    
    public boolean isMin() {
        return this.equals(TimeUtil.getMin());
     }
    
    public TimePoint clone() {
        try {
            TimePoint rtn = (TimePoint) super.clone();
            return rtn;
        } catch (CloneNotSupportedException e) {
            throw new InternalError();
        }
    }

    public boolean isInTrade() {
        try {
            boolean isIn = TimeUtil.isInTrade(this);
            return isIn;
        } catch (QPException e) {
            return false;
        }
    }

	public int minus(TimePoint point) {
		if (point == null) {
			return 0;
		}
		int hour = point.getHour();
		int minute = point.getMin();

		if (this.hour < hour) {
			this.hour += 24;
		}
		this.hour -= hour;
		this.min -= minute;

		return this.min + this.hour * 60;

	}
}
