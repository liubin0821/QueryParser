package com.myhexin.qparser.resource.model;

import java.util.Calendar;

import com.myhexin.qparser.ApplicationContext.ApplicationContextHelper;

public class IndexDefDateTestCase {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ApplicationContextHelper.loadApplicationContext();

		String indexName = "市盈率(pe)";
		Calendar backtestTime = Calendar.getInstance();
		//Calendar[] backtestTime = new Calendar[]{backtestTime1};
		Calendar[] c = IndexDefDateInfo.getInstance().getDate(indexName, null, null, backtestTime);
		System.out.println(IndexDefDateInfo.getInstance().getIndexDefDate(indexName) + ": " + backtestTime.getTime() + "=>"+ c[0].getTime());
		
		indexName = "市销率(ps)";
		c = IndexDefDateInfo.getInstance().getDate(indexName, null, null, backtestTime);
		System.out.println(IndexDefDateInfo.getInstance().getIndexDefDate(indexName) + ": " + backtestTime.getTime() + "=>"+ c[0].getTime());
		
		
		indexName = "结算备付金";
		c = IndexDefDateInfo.getInstance().getDate(indexName,null,  null, backtestTime);
		System.out.println(IndexDefDateInfo.getInstance().getIndexDefDate(indexName) + ": " + backtestTime.getTime() + "=>"+ c[0].getTime());
		
		indexName = "红股上市交易日";
		c = IndexDefDateInfo.getInstance().getDate(indexName, null, null, backtestTime);
		System.out.println(IndexDefDateInfo.getInstance().getIndexDefDate(indexName) + ": " + backtestTime.getTime() + "=>"+ c[0].getTime());
		
		indexName = "股权登记日(b股最后交易日)";
		c = IndexDefDateInfo.getInstance().getDate(indexName, null, null, backtestTime);
		System.out.println(IndexDefDateInfo.getInstance().getIndexDefDate(indexName) + ": " + backtestTime.getTime() + "=>"+ c[0].getTime());
		
		indexName = "市盈率(pe,ttm)";
		c = IndexDefDateInfo.getInstance().getDate(indexName, null, null, backtestTime);
		System.out.println(IndexDefDateInfo.getInstance().getIndexDefDate(indexName) + ": " + backtestTime.getTime() + "=>"+ c[0].getTime());
		
		indexName = "业绩快报.摊薄每股收益eps";
		c = IndexDefDateInfo.getInstance().getDate(indexName, null, null, backtestTime);
		System.out.println(IndexDefDateInfo.getInstance().getIndexDefDate(indexName) + ": " + backtestTime.getTime() + "=>"+ c[0].getTime());
		
		indexName = "应收股利";
		c = IndexDefDateInfo.getInstance().getDate(indexName, null, null, backtestTime);
		System.out.println(IndexDefDateInfo.getInstance().getIndexDefDate(indexName) + ": " + backtestTime.getTime() + "=>"+ c[0].getTime());
		indexName = "指数@市盈率";
		c = IndexDefDateInfo.getInstance().getDate(indexName,null,  null, backtestTime);
		System.out.println(IndexDefDateInfo.getInstance().getIndexDefDate(indexName) + ": " + backtestTime.getTime() + "=>"+ c[0].getTime());
		
	}

}
