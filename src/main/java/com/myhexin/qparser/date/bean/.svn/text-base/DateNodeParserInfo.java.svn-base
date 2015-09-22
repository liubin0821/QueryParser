package com.myhexin.qparser.date.bean;

import java.util.regex.Pattern;

public class DateNodeParserInfo {

	private int id;
	private String type;
	private String parserName;
	private String patternStr;
	private Pattern p1; 
	
	public DateNodeParserInfo(){}
	
	public DateNodeParserInfo(String pattern, String type, String parserStr) {
		this.type =type;
		this.parserName = parserStr;
		this.patternStr = pattern;
		p1 = Pattern.compile(pattern);
	}
	
	public boolean match(String s) {
		if(p1!=null && p1.matcher(s).matches())
			return true;
		else
			return false;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getParserName() {
		return parserName;
	}

	public void setParserName(String parserName) {
		this.parserName = parserName;
	}

	public String getPatternStr() {
		return patternStr;
	}

	public void setPatternStr(String patternStr) {
		this.patternStr = patternStr;
		p1 = Pattern.compile(patternStr);
	}

	
}
