package com.myhexin.qparser.util.cfgdate;

import java.io.Serializable;


public class CfgRptPeriodDefault implements Serializable {

	private static final long serialVersionUID = 1L;
	private int id;
	private String key_name;
	private String dt_format;
	private String dt_from;
	private String dt_to;
	private String dt_val;
	private int offset_val;
	private String sign_val;
	
	
	private CfgSimpleDate from;
	private CfgSimpleDate to; 
	private CfgSimpleDate defaultDate; 
	
	
	/*public boolean isMatchIndexPropBoth(String indexName, String propName) {
		if(key_name!=null && ( (indexName!=null && key_name.contains(indexName)) &&  (propName!=null && key_name.contains(propName) )) ) {
			return true;
		}else{
			return false;
		}
	}*/
	

	/*public boolean isMatchIndexPropEither(String indexName, String propName) {
		if(key_name!=null && ( (indexName!=null && key_name.contains(indexName)) ||  (propName!=null && key_name.contains(propName) )) ) {
			return true;
		}else{
			return false;
		}
	}*/
	
	public CfgSimpleDate getFrom() {
		return from;
	}

	public void setFrom(CfgSimpleDate from) {
		this.from = from;
	}

	public CfgSimpleDate getTo() {
		return to;
	}

	public void setTo(CfgSimpleDate to) {
		this.to = to;
	}

	public CfgSimpleDate getDefaultDate() {
		return defaultDate;
	}

	public void setDefaultDate(CfgSimpleDate defaultDate) {
		this.defaultDate = defaultDate;
	}

	
	public void reloadDtFieldsByFromat() {
		from = new CfgSimpleDate(dt_format, this.dt_from);
		to = new CfgSimpleDate(dt_format, this.dt_to);
		defaultDate = new CfgSimpleDate(dt_format, this.dt_val);
	}

	
	public String toString() {
		return key_name + " ["+from + ","  +to + "] = " + defaultDate;
	}
	/*public static DefaultRptPeriod create(String[] cells) {
		DefaultRptPeriod config = new DefaultRptPeriod();
		config.keyName = cells[0];
		config.from = new CfgSimpleDate(cells[1]);
		config.to = new CfgSimpleDate(cells[2]);
		config.defaultDate = new CfgSimpleDate(cells[3]);
		config.yearOffset = Integer.parseInt(cells[4]);
		config.sign =  cells[5];
		return config;
	}*/
	

	public String getKey_name() {
		return key_name;
	}

	public void setKey_name(String key_name) {
		this.key_name = key_name;
	}

	public String getDt_format() {
		return dt_format;
	}

	public void setDt_format(String dt_format) {
		this.dt_format = dt_format;
	}

	public String getDt_from() {
		return dt_from;
	}

	public void setDt_from(String dt_from) {
		this.dt_from = dt_from;
	}

	public String getDt_to() {
		return dt_to;
	}

	public void setDt_to(String dt_to) {
		this.dt_to = dt_to;
	}

	public String getDt_val() {
		return dt_val;
	}

	public void setDt_val(String dt_val) {
		this.dt_val = dt_val;
	}

	public int getOffset_val() {
		return offset_val;
	}

	public void setOffset_val(int offset_val) {
		this.offset_val = offset_val;
	}

	public String getSign_val() {
		return sign_val;
	}

	public void setSign_val(String sign_val) {
		this.sign_val = sign_val;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	
	
	
}
