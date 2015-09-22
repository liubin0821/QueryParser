/**
 * 
 */
package com.myhexin.qparser.util.condition;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.myhexin.qparser.ApplicationContext.ApplicationContextHelper;
import com.myhexin.qparser.util.condition.model.BackTestCondAnnotation;

/**
 * @author chenhao
 *
 */
public class ConditionTechBuilderTest {
	@Before
	public void before() {
		ApplicationContextHelper.loadApplicationContext();
	}

	@Test
	public void test() {
		String query = "10：30-14：28 60分钟macd金叉";
		String postDataStr = null;
		List<BackTestCondAnnotation> list = ConditionParser.compileToCond(query, "", "", postDataStr);
		String jsonResult = list.get(0).getResultCondJson();
		Assert.assertTrue(jsonResult.contains("起始交易时间 10:30"));
		Assert.assertTrue(jsonResult.contains("截止交易时间 14:28"));
		Assert.assertTrue(jsonResult.contains("区间偏移 [-238,0]"));

		query = "7月20号14点 30分钟macd金叉";
		postDataStr = null;
		list = ConditionParser.compileToCond(query, "", "", postDataStr);
		jsonResult = list.get(0).getResultCondJson();
		Assert.assertTrue(jsonResult.contains("周期长度 30"));
		Assert.assertTrue(jsonResult.contains("交易日期 20150720"));
		Assert.assertTrue(jsonResult.contains("交易时间 14:00"));

		query = "2015年5月25日 60分钟kdj金叉";
		postDataStr = null;
		list = ConditionParser.compileToCond(query, "", "", postDataStr);
		jsonResult = list.get(0).getResultCondJson();
		Assert.assertTrue(jsonResult.contains("周期长度 60"));
		Assert.assertTrue(jsonResult.contains("交易日期 20150525"));
		Assert.assertTrue(jsonResult.contains("起始交易时间 9:30"));
		Assert.assertTrue(jsonResult.contains("截止交易时间 15:00"));

		query = "15分钟kdj上移";
		postDataStr = "{\"start\":\"2015-08-01 14:58:00\"}";
		list = ConditionParser.compileToCond(query, "", "", postDataStr);
		jsonResult = list.get(0).getResultCondJson();
		Assert.assertTrue(jsonResult.contains("周期长度 15"));
		Assert.assertTrue(jsonResult.contains("交易日期 20150731"));
		Assert.assertTrue(jsonResult.contains("交易时间 14:58"));

		query = "连续48个周期 5分钟ma20下移";
		postDataStr = "{\"start\":\"2015-08-01 14:58:00\"}";
		list = ConditionParser.compileToCond(query, "", "", postDataStr);
		jsonResult = list.get(0).getResultCondJson();
		Assert.assertTrue(jsonResult.contains("周期长度 5"));
		Assert.assertTrue(jsonResult.contains("交易日期 20150731"));
		Assert.assertTrue(jsonResult.contains("交易时间 14:58"));
		Assert.assertTrue(jsonResult.contains("持续周期 48"));

		query = "60分钟macd金叉";
		postDataStr = "{\"start\":\"2015-08-01 14:58:00\"}";
		list = ConditionParser.compileToCond(query, "", "", postDataStr);
		jsonResult = list.get(0).getResultCondJson();
		Assert.assertTrue(jsonResult.contains("周期长度 60"));
		Assert.assertTrue(jsonResult.contains("交易日期 20150731"));
		Assert.assertTrue(jsonResult.contains("交易时间 14:58"));


	}

	@Test
	public void test1() {
		String query = "2015年8月19号13点00分到2015年8月20号14点50分 15分钟kdj金叉";
		String postDataStr = null;
		List<BackTestCondAnnotation> list = ConditionParser.compileToCond(query, "", "", postDataStr);
		String jsonResult = list.get(0).getResultCondJson();
		Assert.assertTrue(jsonResult.contains("起始交易日期 20150819"));
		Assert.assertTrue(jsonResult.contains("截止交易日期 20150820"));
		Assert.assertTrue(jsonResult.contains("起始交易时间 13:00"));
		Assert.assertTrue(jsonResult.contains("截止交易时间 14:50"));
		Assert.assertTrue(jsonResult.contains("区间偏移 [-110,0]"));

	}

	@Test
	//测试分时行情指标问句
	public void test2() {
		String query = "2015年5月25日 10:31 涨跌幅";
		String postDataStr = null;
		List<BackTestCondAnnotation> list = ConditionParser.compileToCond(query, "", "", postDataStr);
		String jsonResult = list.get(0).getResultCondJson();
		Assert.assertTrue(jsonResult.contains("分时涨跌幅:前复权"));
		Assert.assertTrue(!jsonResult.contains("周期长度 60"));
		Assert.assertTrue(jsonResult.contains("交易日期 20150525"));
		Assert.assertTrue(jsonResult.contains("交易时间 10:31"));

		query = "2015年5月25日 涨跌幅";
		postDataStr = null;
		list = ConditionParser.compileToCond(query, "", "", postDataStr);
		jsonResult = list.get(0).getResultCondJson();
		Assert.assertTrue(!jsonResult.contains("分时涨跌幅:前复权"));
		Assert.assertTrue(jsonResult.contains("涨跌幅:前复权"));
		Assert.assertTrue(!jsonResult.contains("周期长度 60"));
		Assert.assertTrue(jsonResult.contains("交易日期 20150525"));
		Assert.assertTrue(!jsonResult.contains("交易时间 "));

		query = "2015年5月25日 10:31-11:12涨跌幅";
		postDataStr = null;
		list = ConditionParser.compileToCond(query, "", "", postDataStr);
		jsonResult = list.get(0).getResultCondJson();
		Assert.assertTrue(jsonResult.contains("分时区间涨跌幅:前复权"));
		Assert.assertTrue(!jsonResult.contains("周期长度 60"));
		Assert.assertTrue(jsonResult.contains("交易日期 20150525"));
		Assert.assertTrue(jsonResult.contains("起始交易时间 10:31"));
		Assert.assertTrue(jsonResult.contains("截止交易时间 11:12"));

		query = "2015年5月25日 10:31-11:12收盘价";
		postDataStr = null;
		list = ConditionParser.compileToCond(query, "", "", postDataStr);
		jsonResult = list.get(0).getResultCondJson();
		Assert.assertTrue(jsonResult.contains("分时区间收盘价"));
		Assert.assertTrue(!jsonResult.contains("周期长度 60"));
		Assert.assertTrue(jsonResult.contains("交易日期 20150525"));

		query = "2015年5月25日 10:31成交量";
		postDataStr = null;
		list = ConditionParser.compileToCond(query, "", "", postDataStr);
		jsonResult = list.get(0).getResultCondJson();
		Assert.assertTrue(jsonResult.contains("分时成交量"));
		Assert.assertTrue(!jsonResult.contains("周期长度 60"));
		Assert.assertTrue(jsonResult.contains("交易日期 20150525"));
		Assert.assertTrue(jsonResult.contains("交易时间 10:31"));

		query = "2015年5月25日  收盘价";
		postDataStr = null;
		list = ConditionParser.compileToCond(query, "", "", postDataStr);
		jsonResult = list.get(0).getResultCondJson();
		Assert.assertTrue(jsonResult.contains("最新价"));
		Assert.assertTrue(!jsonResult.contains("周期长度 60"));
		Assert.assertTrue(!jsonResult.contains("交易日期 20150525"));
		Assert.assertTrue(!jsonResult.contains("交易时间 "));

		query = "周线周期macd死叉";
		postDataStr = null;
		list = ConditionParser.compileToCond(query, "", "", postDataStr);
		jsonResult = list.get(0).getResultCondJson();
		Assert.assertTrue(jsonResult.contains("macd死叉"));
		Assert.assertTrue(!jsonResult.contains("分时macd死叉"));
		Assert.assertTrue(jsonResult.contains("分析周期 WEEK"));
		Assert.assertTrue(!jsonResult.contains("交易时间 "));
	}

	@Test
	public void test5() {
		String query = "近20日有两次月macd金叉";
		String postDataStr = null;
		List<BackTestCondAnnotation> list = ConditionParser.compileToCond(query, "", "", postDataStr);
		String jsonResult = list.get(0).getResultCondJson();
		Assert.assertTrue(jsonResult.contains("出现次数 2"));
		Assert.assertTrue(jsonResult.contains("分析周期 MONTH"));
		Assert.assertTrue(jsonResult.contains("\"indexName\":\"macd金叉\""));

	}

	@Test
	public void test6() {
		String query = "近20日有3次年macd金叉";
		String postDataStr = null;
		List<BackTestCondAnnotation> list = ConditionParser.compileToCond(query, "", "", postDataStr);
		String jsonResult = list.get(0).getResultCondJson();
		Assert.assertTrue(jsonResult.contains("出现次数 3"));
		Assert.assertTrue(jsonResult.contains("分析周期 YEAR"));
		Assert.assertTrue(jsonResult.contains("\"indexName\":\"macd金叉\""));

	}

	@Test
	public void test7() {
		String query = "近20日有两次周macd金叉";
		String postDataStr = null;
		List<BackTestCondAnnotation> list = ConditionParser.compileToCond(query, "", "", postDataStr);
		String jsonResult = list.get(0).getResultCondJson();
		Assert.assertTrue(jsonResult.contains("出现次数 2"));
		Assert.assertTrue(jsonResult.contains("分析周期 WEEK"));
		Assert.assertTrue(jsonResult.contains("\"indexName\":\"macd金叉\""));

	}
}
