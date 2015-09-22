package com.myhexin.qparser.util.cfgdate;

import java.util.Calendar;

public class CfgSimpleDate {
	public CfgSimpleDate(String s) {
		try{
			month = Integer.parseInt(s.substring(0,2));
			day = Integer.parseInt(s.substring(2));
		}catch(Exception e) {
			
		}
	}
	
	private String[] splitFormat(String format) {
		if(format!=null && format.length()>=2) {
			String[] formats = new String[format.length()/2];
			for(int i=0;i<formats.length;i++) {
				formats[i] = format.substring(i*2, i*2+2);
			}
			return formats;
		}else{
			return null;
		}
	}
	
	
	private int intval(String s, int start, int end) {
		try{
			return Integer.parseInt(s.substring(start,end));
		}catch(Exception e) {
			return 0;
		}
	}
	
	public CfgSimpleDate(String format, String s) {
		try{
			
			if(s.equals("yyMMdd")) {
				Calendar cal = Calendar.getInstance();
				year = cal.get(Calendar.YEAR);
				month = cal.get(Calendar.MONTH)+1;
				day = cal.get(Calendar.DAY_OF_MONTH);
			}else{
				String[] formats = splitFormat(format);
				if(formats!=null) {
					for(int i=0;i<formats.length;i++) {
						if(formats[i]!=null && formats[i].equals("yy")) {
							year = intval(s, i*2,i*2+2);
						}else if(formats[i]!=null && formats[i].equals("MM")) {
							month =intval(s, i*2,i*2+2);
						}else if(formats[i]!=null && formats[i].equals("dd")) {
							day = intval(s, i*2,i*2+2);
						}else if(formats[i]!=null && formats[i].equals("hh")) {
							hour = intval(s, i*2,i*2+2);
						}else if(formats[i]!=null && formats[i].equals("mm")) {
							minute = intval(s, i*2,i*2+2);
						}else if(formats[i]!=null && formats[i].equals("ss")) {
							second = intval(s, i*2,i*2+2);
						}
					}
				}
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public CfgSimpleDate getPrevious() {
		if(month<=3) return new CfgSimpleDate(year-1, 12,31);
		else if(month<=6) return new CfgSimpleDate(year, 3,31);
		else if(month<=9) return new CfgSimpleDate(year, 6,30);
		else if(month<=12) return new CfgSimpleDate(year, 9,30);
		else return new CfgSimpleDate(year, month,day); //应该不会出现这种情况
	}
	
	public CfgSimpleDate getNext() {
		if(month<=3) return new CfgSimpleDate(year, 6,30);
		else if(month<=6) return new CfgSimpleDate(year, 9,30);
		else if(month<=9) return new CfgSimpleDate(year, 12,31);
		else if(month<=12) return new CfgSimpleDate(year+1, 3,31);
		else return new CfgSimpleDate(year, month,day); //应该不会出现这种情况
	}
	
	public CfgSimpleDate(){}
	public CfgSimpleDate(int year, int month, int day){
		this.year = year;
		this.month = month;
		this.day = day;
	}
	
	private int year;
	private int month;
	private int day;
	
	private int hour;
	private int minute;
	private int second;
	
	
	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public int getMonth() {
		return month;
	}

	public void setMonth(int month) {
		this.month = month;
	}

	public int getDay() {
		return day;
	}

	public void setDay(int day) {
		this.day = day;
	}
	
	public int getHour() {
		return hour;
	}

	public void setHour(int hour) {
		this.hour = hour;
	}

	public int getMinute() {
		return minute;
	}

	public void setMinute(int minute) {
		this.minute = minute;
	}

	public int getSecond() {
		return second;
	}

	public void setSecond(int second) {
		this.second = second;
	}

	public String toString() {
		StringBuilder builder = new StringBuilder();
		if(year==0){
			builder.append("0000");
		}
		else 
			builder.append(year);
		if(month<10){
			builder.append('0');
			builder.append(month);
		}else {
			builder.append(month);
		}
		
		if(day<10){
			builder.append('0');
			builder.append(day);
		}else {
			builder.append(day);
		}
		builder.append(" ");
		
		if(hour<10){
			builder.append('0');
			builder.append(hour);
		}else {
			builder.append(hour);
		}
		
		if(minute<10){
			builder.append('0');
			builder.append(minute);
		}else {
			builder.append(minute);
		}
		
		if(second<10){
			builder.append('0');
			builder.append(second);
		}else {
			builder.append(second);
		}
		return builder.toString();
	}
}
