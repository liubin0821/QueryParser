package com.myhexin.qparser.util.cfgdate;

import com.myhexin.qparser.ApplicationContext.ApplicationContextHelper;

public class CfgDateLogicInstanceTestCase {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ApplicationContextHelper.loadApplicationContext();
		
		/*String s = CfgDateLogicInstance.getInstance().getDatePeriod("龙虎榜", null, null);
		System.out.println("龙虎榜: " + s);*/
		
		
		String s = CfgDateLogicInstance.getInstance().getDatePeriod("预告净利润", "报告期", "");
		System.out.println("报告期: " + s);
		
		s = CfgDateLogicInstance.getInstance().getDatePeriod(null, "报告期_optimize", "");
		System.out.println("报告期_optimize: " + s);
		
		String[] indexNames = new String[]{"营业部买卖方向", "营业部-交易日期", "营业部买入金额", "买入占总成交额比例", "营业部净额", "营业部名称", "营业部-上榜原因"};
		for(int i=0;i<indexNames.length;i++) {
			s = CfgDateLogicInstance.getInstance().getDatePeriod(indexNames[i], null, "");
			System.out.println(indexNames[i] + ": " + s);
		}
		s = CfgDateLogicInstance.getInstance().getDatePeriod("营业部买卖方向", null, "");
		System.out.println("营业部买卖方向: " + s);
	}

}
