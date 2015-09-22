package com.myhexin.DB.mybatis.mode;

import java.io.Serializable;

public class RefCode implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int attr_id; 
	private String code_value; 
	private String code_short_desc; 
	private String code_long_desc;
	public int getAttr_id() {
		return attr_id;
	}
	public void setAttr_id(int attr_id) {
		this.attr_id = attr_id;
	}
	public String getCode_value() {
		return code_value;
	}
	public void setCode_value(String code_value) {
		this.code_value = code_value;
	}
	public String getCode_short_desc() {
		return code_short_desc;
	}
	public void setCode_short_desc(String code_short_desc) {
		this.code_short_desc = code_short_desc;
	}
	public String getCode_long_desc() {
		return code_long_desc;
	}
	public void setCode_long_desc(String code_long_desc) {
		this.code_long_desc = code_long_desc;
	}
	
	

}
