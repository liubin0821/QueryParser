package com.myhexin.qparser.util.condition;

import java.util.List;

import com.myhexin.qparser.ApplicationContext.ApplicationContextHelper;
import com.myhexin.qparser.phrase.parsePlugins.PhraseParserPluginBusinessAdjustDateNumByReportType;
import com.myhexin.qparser.util.condition.model.BackTestCondAnnotation;

public class ConditionBuilderStrTestCase {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		// 加载xml文件，初始spring容器
		ApplicationContextHelper.loadApplicationContext("applicationContext.xml");
		PhraseParserPluginBusinessAdjustDateNumByReportType n = ApplicationContextHelper.getBean("adjustDateNumByReportType");
		System.out.println(n.strTitle);
		//String query="指数昨日区间涨跌幅";
		//String query="macd金叉";
		//String query="连续3天涨跌幅";
		//String query="kdj的k值";
		//String query="macd金叉的指数";
		//String query="5日均线";
		//String query="5日均线金叉10日均线";
		//String query="2013.12.25融券偿还量";
		//String query="macd低位金叉顶背离";
		//String query="概念指数涨跌幅";
		//String query="市净率-市盈率>0.0001";
		//String query="市盈率>10";
		//String query="上证指数连续2天涨跌幅";
		//String query="上证指数当天成交量大于昨天成交量";
		//String query="分时kdj金叉 行情领涨股 每股收益0.35-1.50元的股票 涨幅 换手率 行业";
		//String query="涨跌幅排名前10;证券行业";
		//String query="行业涨跌幅排名前10";
		//String query="2015年3月4日涨跌幅";
		//String query="近利润行业排名第5";
		//String query="今日逐笔主动买单量大于5000手大于0次;流通股";//"未来复牌";//"未来停牌";
		//String query="股票代码:600652,000835,002071;股票代码，股票简称，员工总数";//"未来复牌";//"未来停牌";
		String query = "成长快";
		
		String domain=null;
		String qType="ALL";
		String postData = null;// "{\"length\":\"0\",\"dunit\":\"0\",\"start\":\"2015-06-25\"}";
		List<BackTestCondAnnotation> list = ConditionParser.compileToCond(query, qType, domain, postData);
		for (BackTestCondAnnotation s : list) {
			System.out.println(query + "\n" + s.getResultCondJson());
		}
				

		
		// 解析, 转condition

	}

	

}
