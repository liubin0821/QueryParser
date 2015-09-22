package com.myhexin.qparser.unit;

import com.myhexin.qparser.servlet.ScoreParserServletTestCase;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * 新写的需求请往这里加test case
 * 
 * @author 刘小峰 liuxiaofeng@myhexin.com
 * @Version 创建时间 2015-3-14
 *
 */
public class PhraseParserTestSuite extends TestCase{

	/**
	 * @param args
	 */
	public static Test suite() {
		TestSuite suite = new TestSuite();
		suite.addTest(new TestUtilTestCase("testStart"));
		suite.addTestSuite(TestCaseTestCase.class);
		suite.addTestSuite(ScoreParserServletTestCase.class);
		
		
		suite.addTest(new TestUtilTestCase("testEnd"));
		return suite;
	}

}
