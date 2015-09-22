package com.myhexin.qparser.phrase;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Set;

import com.myhexin.qparser.Query;
import com.myhexin.qparser.node.Environment;
import com.myhexin.qparser.node.SemanticNode;

public class ParserAnnotation implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// 在日志中写每个插件的运行使用的时间,性能调试用
	private boolean isLogTime = false;
	private boolean stopProcessFlag = false;
	private boolean isWriteLog;
	private Set<String> pluginSkipSet;
	
	
	private String tokenizeText;
	private String segmentedText;
	private String queryText;
	private Query query;
	private Query.Type queryType;
	private Calendar backtestTime = null;
	private ArrayList<SemanticNode> nodes;
	private ArrayList<ArrayList<SemanticNode>> qlist;
	private Environment env;
	
	
	private String chunkedText;
	private List<String> chunkedTextList;
	
	
	
	public String getTokenizeText() {
		return tokenizeText;
	}
	public void setTokenizeText(String tokenizeText) {
		this.tokenizeText = tokenizeText;
	}
	public ArrayList<ArrayList<SemanticNode>> getQlist() {
		return qlist;
	}
	public void setQlist(ArrayList<ArrayList<SemanticNode>> qlist) {
		this.qlist = qlist;
	}
	public Set<String> getPluginSkipSet() {
		return pluginSkipSet;
	}
	public void setPluginSkipSet(Set<String> pluginSkipSet) {
		this.pluginSkipSet = pluginSkipSet;
	}
	public ArrayList<SemanticNode> getNodes() {
		return nodes;
	}
	public void setNodes(ArrayList<SemanticNode> nodes) {
		this.nodes = nodes;
	}
	public boolean isWriteLog() {
		return isWriteLog;
	}
	public void setWriteLog(boolean isWriteLog) {
		this.isWriteLog = isWriteLog;
	}
	public String getQueryText() {
		return queryText;
	}
	public void setQueryText(String queryText) {
		this.queryText = queryText;
	}
	public Query.Type getQueryType() {
		return queryType;
	}
	public void setQueryType(Query.Type queryType) {
		this.queryType = queryType;
	}
	public String getChunkedText() {
		return chunkedText;
	}
	public void setChunkedText(String chunkedText) {
		this.chunkedText = chunkedText;
	}
	public List<String> getChunkedTextList() {
		return chunkedTextList;
	}
	public void setChunkedTextList(List<String> chunkedTextList) {
		this.chunkedTextList = chunkedTextList;
	}
	public Environment getEnv() {
		return env;
	}
	public void setEnv(Environment env) {
		this.env = env;
	}
	public Query getQuery() {
		return query;
	}
	public void setQuery(Query query) {
		this.query = query;
	}
	public String getSegmentedText() {
		return segmentedText;
	}
	public void setSegmentedText(String segmentedText) {
		this.segmentedText = segmentedText;
	}
	public boolean isStopProcessFlag() {
		return stopProcessFlag;
	}
	public void setStopProcessFlag(boolean stopProcessFlag) {
		this.stopProcessFlag = stopProcessFlag;
	}
	public Calendar getBacktestTime() {
		if(backtestTime==null) {
			backtestTime = Calendar.getInstance();
		}
		return backtestTime;
	}
	public void setBacktestTime(Calendar backtestTime) {
		this.backtestTime = backtestTime;
	}
	public boolean isLogTime() {
		return isLogTime;
	}
	public void setLogTime(boolean isLogTime) {
		this.isLogTime = isLogTime;
	}

}
