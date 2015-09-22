package com.myhexin.DB.mybatis.mode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DataLine implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int id;
	private int type_id;
	private String text;
	private String code;
	private List<String> indexNames;
	
	public List<String> getIndexName() {
		return indexNames;
	}
	
	private void trimAddStr(String name) {
		if(indexNames==null) {
			indexNames = new ArrayList<String>();
		}
		
		if(name==null || name.length()==0) {
			return;
		}
		
		String[] names = name.split(",");
		if(names!=null && names.length>0) {
			for(String s : names) {
				if(s.charAt(0)=='_') {
					s = s.substring(1);
				}
				
				if(s.endsWith("简称") || s.endsWith("全称")) {
					s = s.substring(0, s.length()-2);
				}
				if(indexNames.contains(s)==false) {
					indexNames.add(s);
				}
			}
		}
	}
	
	public void setCode_short_desc(String code_short_desc) {
		trimAddStr(code_short_desc);
	}
	public void setLabel(String label) {
		trimAddStr(label);
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getType_id() {
		return type_id;
	}
	public void setType_id(int type_id) {
		this.type_id = type_id;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getCode() {
		return code;
	}
	public String getCodeWithoutColon() {
		if(code!=null) {
			int idx = code.indexOf(":");
			if(idx>0) {
				String id = code.substring(idx+1);
				id = id.replaceAll("\n", "");
				id = id.replaceAll("\r", "");
				id = id.replaceAll("\r\n", "");
				return id;
			}
		}
		return "";
	}
	
	public void setCode(String code) {
		this.code = code;
	}
	
	

}
