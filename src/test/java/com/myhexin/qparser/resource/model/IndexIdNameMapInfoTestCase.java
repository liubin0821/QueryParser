package com.myhexin.qparser.resource.model;

import com.myhexin.qparser.ApplicationContext.ApplicationContextHelper;

public class IndexIdNameMapInfoTestCase {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ApplicationContextHelper.loadApplicationContext();

		get("指数");
		get("概念指数");
		get("行业指数");
		get("上证指数");
		get("房地产行业");
		get("深证a股");
	
	}
	private static void get(String name) {
		get(null, name);
	}
	
	private static void get(String name, String name1) {
		String s = IndexIdNameMapInfo.getInstance().getIdListStr(name, name1); 
		int size = s.split(">-<").length;
		System.out.println(name + ": [" + size + "]" + s);
	}
}
