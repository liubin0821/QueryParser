package com.myhexin.qparser.date.bean;

import java.util.Calendar;


public class DateValue {

	
	
	
	//
	public int year=-1;
	public int month=-1;
	public int day=-1;
	
	public static DateValue today() {
		DateValue t = new DateValue();
		Calendar cal = Calendar.getInstance();
		t.year = cal.get(Calendar.YEAR);
		t.month = cal.get(Calendar.MONTH)+1;
		t.day = cal.get(Calendar.DAY_OF_MONTH);
		return t;
	}
	
	
	public String toString() {
		return year+"/"+month+"/"+day;
	}
}
