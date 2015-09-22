package com.myhexin.qparser.util.condition.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.annotations.Expose;
import com.myhexin.qparser.define.EnumDef.ReportType;
import com.myhexin.qparser.phrase.util.Consts;
import com.myhexin.qparser.resource.model.IndexIdNameMapInfo;
import com.myhexin.qparser.resource.model.RenameMapInfo;
import com.myhexin.qparser.util.condition.ConditionBuilderAbstract;
import com.myhexin.qparser.util.condition.ConditionBuilderUtil;

public class ConditionIndexModel extends ConditionModel {

	
	public ConditionIndexModel copy() {
		ConditionIndexModel model = new ConditionIndexModel(this.indexId, this.classNodeText, this.domain);
		model.indexName = indexName;
		model.indexProperties = new ArrayList<String>(indexProperties);
		model.sonSize = sonSize;
		model.type = type;
		model.reportType = reportType;
		model.valueType = valueType;
		model.chunkInfo = chunkInfo;
		model.domain = domain;
		return model;
	}
	
	public ConditionIndexModel(int indexId, String classNodeText, String domain) {
		this.indexId = indexId;
		this.classNodeText = classNodeText;
		this.domain = domain;
		this.source= "new_parser";
	}
	
	private final static List<String> EMPTY_PROPS = new ArrayList<String>(0);
	
	@Expose
	private int indexId;
	@Expose
	private String indexName;
	@Expose
	private List<String> indexProperties;
	@Expose
	private String type = "index";
	@Expose
	private int sonSize;
	@Expose
	private String reportType;
	@Expose
	private String valueType;
	@Expose
	private String chunkInfo;
	@Expose
	private String domain;
	@Expose
	private String source;
	
	
	private String classNodeText;
	
	
	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public int getIndexId() {
		return indexId;
	}

	public void setIndexId(int indexId) {
		this.indexId = indexId;
	}

	public String getClassNodeText() {
		return classNodeText;
	}

	public void setClassNodeText(String classNodeText) {
		this.classNodeText = classNodeText;
	}

	public String getIndexName() {
		return indexName;
	}
	
	public String getIndexNameWithoutAtSign() {
		if(indexName!=null && indexName.indexOf('@')>0) {
			return indexName.replace("@", "");
		}else
			return indexName;
	}
	
	/*public String getIndexNameAtSignPrefix() {
		if(indexName!=null && indexName.indexOf('@')>0) {
			int index =indexName.indexOf('@');
			if(index>0) {
				return indexName.substring(0, index);
			}
		}
		
		return null;
	}*/
	
	public void setIndexName(String indexName) {
		this.indexName = trimZhishuat(indexName);
		if(ConditionBuilderUtil.isTechIndex(indexName) ) {
			this.type = "tech";
		}
	}
	
	public String getReportType() {
		return reportType;
	}

	public String getValueType() {
		return valueType;
	}

	public void setValueType(String valueType) {
		this.valueType = valueType;
	}

	public void setReportType(ReportType reportType) {
		this.reportType = String.valueOf(reportType);
	}

	private String trimZhishuat(String indexName) {
		if(indexName.indexOf("区间指数@")>=0) {
			String prefix = "区间指数@";
			String leftName = indexName.substring(5);
			if(leftName.indexOf("指数@")>=0) {
				leftName = leftName.replaceAll("指数@", "");
				indexName = prefix + leftName;
			}
		}else if(indexName.indexOf("指数@")>=0) {
			indexName = indexName.replaceAll("指数@", "");
			indexName = "指数@" + indexName;
		}
		return indexName;
	}
	
	
	/*
	 * abs_指数领域->指数
	 * abs_股票领域->“”
	 * abs_基金领域->基金
	 * abs_港股领域->港股
	 * ...
	 * 
	 */
	private static Map<String,String> domainPrefixMap = new HashMap<String, String>(); //领域=>指标名称的对应关系
	static{
		domainPrefixMap.put(Consts.CONST_absSmallMoneyGod, Consts.CONST_SmallMoneyGod);
		domainPrefixMap.put(Consts.CONST_absMarketEnv, Consts.CONST_MarketEnv);
		domainPrefixMap.put(Consts.CONST_absHkDomain, Consts.CONST_HkPrefix);
		domainPrefixMap.put(Consts.CONST_absUsStockDomain, Consts.CONST_UsPrefix);
		domainPrefixMap.put(Consts.CONST_absFundDomain, Consts.CONST_FundPrefix);
		domainPrefixMap.put(Consts.CONST_absZhishuDomain, Consts.CONST_IndexPrefix);
	}
	
	public void setIndexName(String indexName, String domain) {
		String prefix = domainPrefixMap.get(domain);
		if(prefix==null || prefix.equals(Consts.CONST_IndexPrefix)) {
			String newName = RenameMapInfo.getInstance().getNewName(indexName, domain);
			this.indexName = trimZhishuat(newName);
		}else{
			this.indexName = prefix + "@" + indexName;
		}
		/*if(Consts.CONST_absSmallMoneyGod.equals(domain)) {
			this.indexName = Consts.CONST_SmallMoneyGod + "@" + indexName;
		}else if(Consts.CONST_absMarketEnv.equals(domain) ) {
			this.indexName = Consts.CONST_MarketEnv + "@" + indexName;
		}else if(Consts.CONST_absHkDomain.equals(domain) ) {
			this.indexName = Consts.CONST_HkPrefix + "@" + indexName;
		}else if(Consts.CONST_absUsStockDomain.equals(domain) ) {
			this.indexName = Consts.CONST_UsPrefix + "@" + indexName;
		}else if(Consts.CONST_absFundDomain.equals(domain) ) {
			this.indexName = Consts.CONST_FundPrefix + "@" + indexName;
		}else{
			String newName = RenameMapInfo.getInstance().getNewName(indexName, domain);
			this.indexName = trimZhishuat(newName);
		}*/
	}
	
	public int getConditionType() {
		return ConditionModel.TYPE_INDEX;
	}
	
	//重设交易日期属性
	public void replaceTradeDayProp(String prop) {
		if(indexProperties!=null) {
			for(int i=0;i<indexProperties.size();i++) {
				String s = indexProperties.get(i);
				if(s.startsWith(ConditionBuilderAbstract.DAY_TRADE_DAY)) {
					indexProperties.set(i, ConditionBuilderAbstract.DAY_TRADE_DAY + prop);
				}else if(s.startsWith(ConditionBuilderAbstract.DAY_RANGE_START)) {
					indexProperties.set(i, ConditionBuilderAbstract.DAY_RANGE_START + prop);
				}else if(s.startsWith(ConditionBuilderAbstract.DAY_RANGE_END)) {
					indexProperties.set(i, ConditionBuilderAbstract.DAY_RANGE_END + prop);
				}
			}
			
		}
	}

	public void checkAndSetProperties() {
		if(indexProperties==null) indexProperties = EMPTY_PROPS;
	}
	
	public List<String> getIndexProperties() {
		if(indexProperties==null) {
			return EMPTY_PROPS;
		}else
			return indexProperties;
	}

	public void setIndexProperties(List<String> indexProperties) {
		this.indexProperties = indexProperties;
	}

	public void addIndexProperty(String indexProperty) {
		if (this.indexProperties == null) {
			this.indexProperties = new ArrayList<String>();
		}
		//不塞入重复的属性
		for (String property : this.indexProperties) {
			if (property.equals(indexProperty)) {
				return;
			}
		}
		this.indexProperties.add(indexProperty);
	}

	public void addProp(String name, String value) {
		addProp(name, value, false);
	}
	
	public void addProp(String name, String value, boolean convertNameToId) {
		if (value == null) {
			return;
		}
		
		//添加交易日期之前,如果indexProperties里已经有交易日期,或起始/截止交易日期, 那么不再加入
		if(name!=null && name.equals(ConditionBuilderAbstract.DAY_TRADE_DAY)) {
			if(indexProperties!=null) {
				for(String s : indexProperties) {
					if(s!=null && s.contains(ConditionBuilderAbstract.DAY_TRADE_DAY)) {
						return;
					}
				}
			}
		}//结束判断是否已经有交易日期类属性

		if (convertNameToId) {
			value = IndexIdNameMapInfo.getInstance().getIdListStr(indexName, value);
		}

		if (indexProperties == null) {
			indexProperties = new ArrayList<String>();
		}
		if (name != null && name.equals(ConditionBuilderAbstract.CONTAIN)) {
			indexProperties.add(name + value);
		} else {
			indexProperties.add(name + " " + value);
		}
	}

	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public int getSonSize() {
		return sonSize;
	}
	public void setSonSize(int sonSize) {
		this.sonSize = sonSize;
	}
}
