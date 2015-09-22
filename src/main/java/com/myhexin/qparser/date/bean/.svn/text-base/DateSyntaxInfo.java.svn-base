package com.myhexin.qparser.date.bean;

import java.util.regex.Pattern;

public class DateSyntaxInfo {

	private int id;
	private Pattern pattern;
	private String patternRegex;
	private String clazzName;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getPatternRegex() {
		return patternRegex;
	}
	public void setPatternRegex(String patternRegex) {
		this.patternRegex = patternRegex;
		pattern = Pattern.compile(patternRegex);
	}
	public String getClazzName() {
		return clazzName;
	}
	public void setClazzName(String clazzName) {
		this.clazzName = clazzName;
	}

	public boolean match(String text) {
		if(pattern!=null && pattern.matcher(text).matches()) {
			return true;
		}else{
			return false;
		}
	}
	
}
