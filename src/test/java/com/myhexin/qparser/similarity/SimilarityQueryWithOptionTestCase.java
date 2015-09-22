package com.myhexin.qparser.similarity;

public class SimilarityQueryWithOptionTestCase {

	public static void main(String[] args) {
	long start = System.currentTimeMillis();
	SimilarityQueryWithOptions option = new SimilarityQueryWithOptions();
	String queryText = "300033";
	//String queryText = "创业板指数最近1个月涨跌幅";
	//String queryText = "低股价，低市盈率，高成长的股票";
	//String queryText = "收盘价排名后30_&_静态市盈率(中证发布)排名后30_&_高成长_&_收盘价的股票";
	//String queryText = "股票代码是300033";
	System.out.println("SimilarityQueryWithOptions Input : " + queryText);
	String newqText = option.process(queryText);
	
	System.out.println("SimilarityQueryWithOptions Final Output : " + newqText);
	long end = System.currentTimeMillis();
	System.out.println( "Spend time = " + (end-start));
	}

}



/*
 柴华 09:44:40 
##MODULE=SIM##SIM_RLT收盘价排名后30_&amp;_静态市盈率(中证发布)排名后30_&amp;_高成长_&amp;_收盘价的股票=&gt;收盘价排名后30_&amp;_静态市盈率(中证发布)排名后30_&amp;_成长快_&amp;_股价
柴华 10:32:50 
低股价，低市盈率，高成长的股票
刘小峰 10:32:22 
好
柴华 10:33:01 
http://www.iwencai.com/stockpick/search?w=%E4%BD%8E%E8%82%A1%E4%BB%B7%EF%BC%8C%E4%BD%8E%E5%B8%82%E7%9B%88%E7%8E%87%EF%BC%8C%E9%AB%98%E6%88%90%E9%95%BF%E7%9A%84%E8%82%A1%E7%A5%A8&tid=stockpick&qs=stockpick_h
*/
