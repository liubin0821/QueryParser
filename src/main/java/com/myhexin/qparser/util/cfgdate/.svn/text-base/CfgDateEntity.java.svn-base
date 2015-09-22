package com.myhexin.qparser.util.cfgdate;

import java.io.Serializable;

public class CfgDateEntity implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int id;
	private String key_name;
	private String dt_format;
	private String dt_from;
	private String dt_to;
	private String dt_next;
	private String dt_current;
	private String dt_previous;

	private String previous_year_num;
	private String previous_season_num;
	
	private CfgSimpleDate from;
	private CfgSimpleDate to; 
	
	public String toString() {
		return key_name + "," + from + "," + to + "," + dt_next + "," + dt_current + "," + dt_previous + "," + previous_year_num + "," + previous_season_num;
	}
	
	
	public boolean isMatchIndexProp(String indexName, String propName) {
		if(key_name!=null && ( (indexName!=null && key_name.contains(indexName)) ||  (propName!=null && key_name.contains(propName) ) ) )  {
			return true;
		}else{
			return false;
		}
	}
	
	//解析配置文件的一行
	//报告期,0130,0331,N,Y,Y,X,X
	public static CfgDateEntity createInstance(String[] cells) {
		if(cells!=null && cells.length==8) {
			CfgDateEntity config = new CfgDateEntity();
			config.key_name = cells[0];
			config.from = new CfgSimpleDate(cells[1]);
			config.to = new CfgSimpleDate(cells[2]);
			config.dt_next = cells[3];
			config.dt_current = cells[4];
			config.dt_previous = cells[5];
			config.previous_year_num = cells[6];
			config.previous_season_num = cells[7];
			return config;
		}
				
		return null;
	}
	
	public void reloadDtFieldsByFromat() {
		from = new CfgSimpleDate(dt_format, this.dt_from);
		to = new CfgSimpleDate(dt_format, this.dt_to);
		
	}
	
	public boolean isThisPeriod(int month, int day) {
		boolean from_flag = (from!=null && (from.getMonth()<month || (from.getMonth()==month && from.getDay()<=day) ) );
		boolean to_flag = (to!=null && (to.getMonth()>month || (to.getMonth()==month && to.getDay()>=day) ) );
		
		if(from_flag && to_flag) {
			return true;
		}else{
			return false;
		}
	}


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


	public String getPrevious_year_num() {
		return previous_year_num;
	}

	public void setPrevious_year_num(String previous_year_num) {
		this.previous_year_num = previous_year_num;
	}

	public String getPrevious_season_num() {
		return previous_season_num;
	}

	public void setPrevious_season_num(String previous_season_num) {
		this.previous_season_num = previous_season_num;
	}

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

	public String getDt_next() {
		return dt_next;
	}

	public void setDt_next(String dt_next) {
		this.dt_next = dt_next;
	}

	public String getDt_current() {
		return dt_current;
	}

	public void setDt_current(String dt_current) {
		this.dt_current = dt_current;
	}

	public String getDt_previous() {
		return dt_previous;
	}

	public void setDt_previous(String dt_previous) {
		this.dt_previous = dt_previous;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	
}
