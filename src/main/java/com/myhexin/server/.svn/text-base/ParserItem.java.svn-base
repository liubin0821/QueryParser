package com.myhexin.server;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ParserItem {

	private String initQuery;
	private String qType;
	private boolean isshowdetail; //显示pluginDetail
	private List<String> standardQueryList = null;
	private List<String> standardOutputList = null;
	private List<String> multResultLists = null;
	private List<String> IndexMultPossibilitys = null;
	private List<String> jsonResultList = null;
	private List<String> luaResultList = null;
	private String logtime;


	private List<String> jsonResultListOfMacroIndustry = null;
	private List<String> thematicList = null;
	private List<String> lightParserResultList = null;
	private List<String> syntSemanIdsList = null;
	private List<Integer> scores = null;
	private Set<String> skipSet = null;//跳过指定插件
	private Calendar backtestTime;
	

	public Calendar getBacktestTime() {
		return backtestTime;
	}

	public void setBacktestTime(Calendar backtestTime) {
		this.backtestTime = backtestTime;
	}

	public ParserItem(String q, String qType) {
		this.initQuery = q;
		this.qType = qType;
		this.standardQueryList = new ArrayList<String>();
		this.standardOutputList = new ArrayList<String>();
		this.jsonResultList = new ArrayList<String>();
		this.luaResultList = new ArrayList<String>();
		this.jsonResultListOfMacroIndustry = new ArrayList<String>();
		this.thematicList = new ArrayList<String>();
		this.lightParserResultList = new ArrayList<String>();
		this.multResultLists = new ArrayList<String>();
		this.IndexMultPossibilitys = new ArrayList<String>();
		this.syntSemanIdsList = new ArrayList<String>();
		this.isshowdetail = false;
	}
	
	public ParserItem(String q, String qType, String detail, String skip) {
		this(q, qType);
		if(detail!=null && detail.equals("1")) {
			isshowdetail = true;
		}else{
			isshowdetail = false;
		}
		if(skip!=null && skip.length()>0){
			//skip = skip.toUpperCase();
			String[] skips = skip.split(",");
			if(skips!=null)
			{
				skipSet  = new HashSet<String>();
				for(String s : skips) {
					skipSet.add(s);
				}
				
			}
		}
	}
	
	public boolean isShowPluginDetail() {
		return isshowdetail;
	}
	
	public List<String> getIndexMultPossibilitys() {
    	return IndexMultPossibilitys;
    }

	public void setIndexMultPossibilitys(List<String> indexMultPossibilitys) {
    	IndexMultPossibilitys = indexMultPossibilitys;
    }

	public List<String> getMultResultLists() {
    	return multResultLists;
    }

	public void setMultResultLists(List<String> multResultLists) {
    	this.multResultLists = multResultLists;
    }

	public String getInitQuery() {
		return this.initQuery;
	}

	public void setInitQuery(String q) {
		this.initQuery = q;
	}
	
	public String getQType() {
		return this.qType;
	}

	public void setQType(String qType) {
		this.qType = qType;
	}

	public List<String> getStandardQueryList() {
		return this.standardQueryList;
	}

	public void setStandardQueryList(List<String> arr) {
		this.standardQueryList = arr;
	}

	public void addStandardQueryList(String sq) {
		this.standardQueryList.add(sq);
	}

	public List<String> getStandardOutputList() {
		return standardOutputList;
	}

	public void setStandardOutputList(List<String> standardOutputList) {
		this.standardOutputList = standardOutputList;
	}

	public List<String> getJsonResultList() {
		return jsonResultList;
	}

	public void setJsonResultList(List<String> jsonResultList) {
		this.jsonResultList = jsonResultList;
	}
	
	public List<String> getJsonResultListOfMacroIndustry() {
		return jsonResultListOfMacroIndustry;
	}

	public void setJsonResultListOfMacroIndustry(List<String> jsonResultListOfMacroIndustry) {
		this.jsonResultListOfMacroIndustry = jsonResultListOfMacroIndustry;
	}

	public List<String> getThematicList() {
		return thematicList;
	}

	public void setThematicList(List<String> thematicList) {
		this.thematicList = thematicList;
	}

	public List<String> getLightParserResultList() {
		return lightParserResultList;
	}

	public void setLightParserResultList(List<String> lightParserResult) {
		this.lightParserResultList = lightParserResult;
	}
	
	public List<String> getSyntSmeanIdsList() {
		return syntSemanIdsList;
	}

	public void setSyntSmeanIdsList(List<String> syntSmeanIdsList) {
		this.syntSemanIdsList = syntSmeanIdsList;
	}

	public List<Integer> getScores() {
		return this.scores;
	}

	public void setScores(List<Integer> arr) {
		this.scores = arr;
	}
	
	public List<String> getLuaResultList() {
    	return luaResultList;
    }

	public void setLuaResultList(List<String> luaResultList) {
    	this.luaResultList = luaResultList;
    }

	public String getLogtime() {
		return logtime;
	}

	public void setLogtime(String logtime) {
		this.logtime = logtime;
	}

	public Set<String> getSkipSet() {
		return skipSet;
	}
	
}
