package com.myhexin.DB.mybatis.mode;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 对应表configFile.cond_use_newapi
 * 
 * @author 刘小峰 liuxiaofeng@myhexin.com
 * @Version 创建时间 2014-12-22
 *
 */
public class CondUseNewapi implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1666480850607209784L;
	private long id;
	private String include_names;
	private String exclude_names;
	
	private Map<String, Object> includeMap = new HashMap<String, Object>();
	private Map<String, Object> excludeMap = new HashMap<String, Object>();
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getInclude_names() {
		return include_names;
	}
	public void setInclude_names(String include_names) {
		this.include_names = include_names;
	}
	public String getExclude_names() {
		return exclude_names;
	}
	public void setExclude_names(String exclude_names) {
		this.exclude_names = exclude_names;
	}
	
	
	/**
	 * include_names字段里可以有多个index_name,用逗号分隔
	 * 把他们拆开,放到hashMap中,便于查询
	 * 
	 */
	public void loadIntoMap() {
		byte[] ref = new byte[0];
		if(include_names!=null) {
			String[] names = include_names.split(",");
			for(String n : names) {
				includeMap.put(n, ref);
			}
		}
		
		if(exclude_names!=null) {
			String[] names = exclude_names.split(",");
			for(String n : names) {
				excludeMap.put(n, ref);
			}
		}
	}
	
	public boolean isZhishuQuery(List<String> names) {
		if(names==null || names.size()==0) {
			return true;
		}
		
		int include = 0;
		int exclude = 0;
		for(String name : names) {
			if(includeMap.get(name)!=null) include++;
			else if(excludeMap.get(name)!=null) exclude++;
		}
		
		/*System.out.println("names = " + names);
		System.out.println("include_names = " + include_names);
		System.out.println("exclude_names = " + exclude_names);
		System.out.println("includeMap = " + includeMap);
		System.out.println("excludeMap = " + excludeMap);
		System.out.println("include = " + include);
		System.out.println("exclude = " + exclude);*/
		
		if(include>0 && exclude==0) {
			return true;
		}
		
		return false;
	}
	
}
