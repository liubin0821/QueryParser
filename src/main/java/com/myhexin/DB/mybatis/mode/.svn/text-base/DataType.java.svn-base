package com.myhexin.DB.mybatis.mode;

import java.io.Serializable;

public class DataType implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final static String SHORT_NAME = "简称";
	private int id;
	private String label;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getLabel() {
		return label;
	}
	
	public String getLabelWithoutSubline() {
		if(label!=null && label.length()>0 && label.charAt(0)=='_') {
			label = label.substring(1);
			
			int idx = label.indexOf(SHORT_NAME);
			if(idx >0) {
				StringBuilder buf = new StringBuilder();
				String s1 = label.substring(0, idx);
				buf.append(s1);
				if(idx+SHORT_NAME.length() < label.length()) {
					String s2 = label.substring(idx+SHORT_NAME.length());
					buf.append(s2);
				}
				label = buf.toString();
			}
			return label;
		}else{
			return label;
		}
	}
	
	public void setLabel(String label) {
		this.label = label;
	}
	

}
