/**
 * 
 */
package com.myhexin.qparser.phrase.parsePlugins;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.myhexin.qparser.ParseResult;
import com.myhexin.qparser.Query;
import com.myhexin.qparser.ApplicationContext.ApplicationContextHelper;
import com.myhexin.qparser.phrase.ParserAnnotation;
import com.myhexin.qparser.phrase.PhraseParser;

/**
 * @author chenhao
 *
 */
public class PhraseParserPluginBindNumOrDateToIndexTest {
	@Before
	public void load() {
		ApplicationContextHelper.loadApplicationContext();
	}

	@Test
	public void test() {
		PhraseParser phraseParser = (PhraseParser) ApplicationContextHelper.getBean("phraseParser");
		String query = "6月18日跌停，6月19日跌停，6月23日涨幅大于-9小于9.98，6月24日涨幅大于3";
		Query q = new Query(query.toLowerCase(), "All");
		ParserAnnotation annotation = new ParserAnnotation();
		annotation.setQueryText(q.text);
		annotation.setQuery(q);
		annotation.setQueryType(q.getType());
		annotation.setWriteLog(true);
		//annotation.setBacktestTime(item.getBacktestTime());
		ParseResult parseResult = phraseParser.parse(annotation);
		String result = parseResult.getStandardQueries().get(0);
		Assert.assertEquals(
				"涨跌停{交易日期:6月18日}为否_&_涨跌停{交易日期:6月19日}为否_&_涨跌幅{交易日期:6月23日}>-9且涨跌幅{交易日期:6月23日}<9.98_&_涨跌幅{交易日期:6月24日}>3",
				result);
	}

	@Test
	public void test1() {
		PhraseParser phraseParser = (PhraseParser) ApplicationContextHelper.getBean("phraseParser");
		String query = "2015年8月19号到2015年8月20号 15分钟kdj金叉";
		Query q = new Query(query.toLowerCase(), "All");
		ParserAnnotation annotation = new ParserAnnotation();
		annotation.setQueryText(q.text);
		annotation.setQuery(q);
		annotation.setQueryType(q.getType());
		annotation.setWriteLog(true);
		//annotation.setBacktestTime(item.getBacktestTime());
		ParseResult parseResult = phraseParser.parse(annotation);
		String result = parseResult.getStandardQueries().get(0);
		Assert.assertEquals("15分钟2015年8月19号到2015年8月20号的kdj金叉", result);
	}

	@Test
	public void test2() {
		PhraseParser phraseParser = (PhraseParser) ApplicationContextHelper.getBean("phraseParser");
		String query = "2015年8月19号13点00分到2015年8月20号14点50分 15分钟kdj金叉";
		Query q = new Query(query.toLowerCase(), "All");
		ParserAnnotation annotation = new ParserAnnotation();
		annotation.setQueryText(q.text);
		annotation.setQuery(q);
		annotation.setQueryType(q.getType());
		annotation.setWriteLog(true);
		//annotation.setBacktestTime(item.getBacktestTime());
		ParseResult parseResult = phraseParser.parse(annotation);
		String result = parseResult.getStandardQueries().get(0);
		Assert.assertEquals("15分钟2015年8月19号13点00分到2015年8月20号14点50分的kdj金叉", result);
	}

	@Test
	public void test3() {
		PhraseParser phraseParser = (PhraseParser) ApplicationContextHelper.getBean("phraseParser");
		String query = "12月1号深证成指的最高价";
		Query q = new Query(query.toLowerCase(), "All");
		ParserAnnotation annotation = new ParserAnnotation();
		annotation.setQueryText(q.text);
		annotation.setQuery(q);
		annotation.setQueryType(q.getType());
		annotation.setWriteLog(true);
		//annotation.setBacktestTime(item.getBacktestTime());
		ParseResult parseResult = phraseParser.parse(annotation);
		String result = parseResult.getStandardQueries().get(0);
		Assert.assertEquals("15分钟2015年8月19号13点00分到2015年8月20号14点50分的kdj金叉", result);
	}

	@Test
	public void test4() {
		PhraseParser phraseParser = (PhraseParser) ApplicationContextHelper.getBean("phraseParser");
		String query = "5日300033融资融券余额";
		Query q = new Query(query.toLowerCase(), "All");
		ParserAnnotation annotation = new ParserAnnotation();
		annotation.setQueryText(q.text);
		annotation.setQuery(q);
		annotation.setQueryType(q.getType());
		annotation.setWriteLog(true);
		//annotation.setBacktestTime(item.getBacktestTime());
		ParseResult parseResult = phraseParser.parse(annotation);
		String result = parseResult.getStandardQueries().get(0);
		Assert.assertEquals("15分钟2015年8月19号13点00分到2015年8月20号14点50分的kdj金叉", result);
	}

	@Test
	public void test5() {
		PhraseParser phraseParser = (PhraseParser) ApplicationContextHelper.getBean("phraseParser");
		String query = "创业板指数本月涨跌幅";
		Query q = new Query(query.toLowerCase(), "All");
		ParserAnnotation annotation = new ParserAnnotation();
		annotation.setQueryText(q.text);
		annotation.setQuery(q);
		annotation.setQueryType(q.getType());
		annotation.setWriteLog(true);
		//annotation.setBacktestTime(item.getBacktestTime());
		ParseResult parseResult = phraseParser.parse(annotation);
		String result = parseResult.getStandardQueries().get(0);
		Assert.assertEquals("区间涨跌幅的指数是创业板指数(价格)_&_本月的区间涨跌幅", result);
	}
}
