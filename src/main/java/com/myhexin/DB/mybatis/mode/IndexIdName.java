package com.myhexin.DB.mybatis.mode;

import java.io.Serializable;


/**
 * 指数Id-Name 对应关系
 * 具体情况table: configFile.index_id_name
 * 
 * 
 * @author 刘小峰 liuxiaofeng@myhexin.com
 * @Version 创建时间 2014-12-22
 *
 */
public class IndexIdName implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8839204167748804597L;
	private String id;
	private String short_name;
	private String categoryLevel1;
	private String categoryLevel2;
	public String getId() {
		return id!=null?id.trim():id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getShort_name() {
		return short_name!=null?short_name.trim():short_name;
	}
	public void setShort_name(String short_name) {
		this.short_name = short_name;
	}
	public String getCategoryLevel1() {
		return categoryLevel1!=null?categoryLevel1.trim():categoryLevel1;
	}
	public void setCategoryLevel1(String category) {
		this.categoryLevel1 = category;
	}
	public String getCategoryLevel2() {
		return categoryLevel2!=null?categoryLevel2.trim():categoryLevel2;
	}
	public void setCategoryLevel2(String category) {
		this.categoryLevel2 = category;
	}
	

}
