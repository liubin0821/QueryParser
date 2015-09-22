package com.myhexin.server.vo;

public abstract class ParserParamAbstract implements Param{
	private String queryText;
	private String queryType;
	
	public String getQueryText() {
		return queryText;
	}
	public void setQueryText(String queryText) {
		this.queryText = queryText;
	}
	public String getQueryType() {
		return queryType;
	}
	public void setQueryType(String queryType) {
		this.queryType = queryType;
	}
}
