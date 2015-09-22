package com.myhexin.qparser.date;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimeRange {

	public class TimeInfo {
				
		private int hour = 0;
		private int minute = 0;
		private int second = 0;
		
		public TimeInfo()
		{
			Date date = new Date();
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			hour = calendar.get(Calendar.HOUR_OF_DAY);
			minute = calendar.get(Calendar.MINUTE);
			second = calendar.get(Calendar.SECOND);
		}
		
		public TimeInfo(String timestr) throws ParseException
		{
			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
			Date date = sdf.parse(timestr);
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			hour = calendar.get(Calendar.HOUR_OF_DAY);
			minute = calendar.get(Calendar.MINUTE);
			second = calendar.get(Calendar.SECOND);
		}
		
		public Calendar toCalendar()
		{
			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.HOUR_OF_DAY, hour);
			calendar.set(Calendar.MINUTE, minute);
			calendar.set(Calendar.SECOND, second);
			return calendar;
		}
		
		public boolean isEarlier(TimeInfo other)
		{
			Calendar c1 = toCalendar();
			Calendar c2 = other.toCalendar();
			return c1.before(c2);
		}
		
		public boolean isLater(TimeInfo other)
		{
			Calendar c1 = toCalendar();
			Calendar c2 = other.toCalendar();
			return c1.after(c2);
		}
		
		public boolean isEquals(TimeInfo other)
		{
			Calendar c1 = toCalendar();
			Calendar c2 = other.toCalendar();
			return c1.equals(c2);
		}
		
		public String toString()
		{
			String str = String.format("%d:%d:%d", this.hour, this.minute, this.second);
			return str;
		}
		
		public boolean isTradeDay()
		{
			SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
			Date date = new Date();
			String string = format.format(date);
			return DateUtil.isTradeDate(string);
		}
	}
	
	private TimeInfo from_ = null;
	private TimeInfo to_ = null;
	private boolean bcheckOnlyTradeDay_ = false;
	
	public TimeRange()
	{
		
	}
	
	public TimeRange(String from, String to) throws ParseException
	{
		if (from == null)
		{
			from = "00:00:00";
		}
		if (to == null)
		{
			to = "23:59:59";
		}
		this.from_ = new TimeInfo(from);
		this.to_ = new TimeInfo(to);
	}
	
	public boolean containsTime(TimeInfo info)
	{
		if (!this.from_.isLater(info) && !this.to_.isEarlier(info))
		{
			return true;
		}
		return false;
	}
	
	public boolean getOnlyCheckTradeDay()
	{
		return this.bcheckOnlyTradeDay_;
	}
	
	public void setOnlyCheckTradeDay(boolean b)
	{
		this.bcheckOnlyTradeDay_ = b;
	}
	
	public String toString()
	{
		String str = String.format("%s-%s", this.from_, this.to_);
		return str;
	}
}
