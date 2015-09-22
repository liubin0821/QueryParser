package com.myhexin.DB.mybatis.mode;

import java.io.Serializable;

public class IndexRenameRule  implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int id;
	private String domain;
	private String original_name;
	private String new_rename;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getDomain() {
		return domain;
	}
	public void setDomain(String domain) {
		this.domain = domain;
	}
	public String getOriginal_name() {
		return original_name;
	}
	public void setOriginal_name(String original_name) {
		this.original_name = original_name;
	}
	public String getNew_rename() {
		return new_rename;
	}
	public void setNew_rename(String new_rename) {
		this.new_rename = new_rename;
	}
	
	

}
