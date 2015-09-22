package com.myhexin.qparser.util.lightparser;

import java.io.StringWriter;

import com.myhexin.qparser.ApplicationContext.ApplicationContextHelper;

public class LightParserTestCase {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ApplicationContextHelper.loadApplicationContext();
		
		String query = "主力资金流向排名2012-08-15";
		//String query = "3d概念龙头股";
		LightParserServletParam requestParam = LightParserServletParam.getParam(query, null);
		
		int x =0;
		while(true) {
		StringWriter out = new StringWriter();
		LightParser.createLightParserJson(requestParam, out);
		//String s = out.toString();
		//System.out.println(s);
		if(x++>100) break;
		}
	}

}
