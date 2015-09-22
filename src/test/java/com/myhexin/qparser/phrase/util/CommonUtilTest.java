package com.myhexin.qparser.phrase.util;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class CommonUtilTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testToLowerAndHalf() {
		//所有的汉字都是全角，所有全角字符共95个，如下
        String str = "！＂＃＄％＆＇（）＊＋，－．／０１２３４５６７８９：；＜＝＞？＠ＡＢＣＤＥＦＧＨＩＪＫＬＭＮＯＰＱＲＳＴＵＶＷＸＹＺ［＼］＾＿｀ａｂｃｄｅｆｇｈｉｊｋｌｍｎｏｐｑｒｓｔｕｖｗｘｙｚ｛｜｝～　";
        String expected = "!\"#$%&'()*+,-./0123456789:;<=>?@abcdefghijklmnopqrstuvwxyz[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~ ";
        String actual = CommonUtil.toLowerAndHalf(str);
        assertEquals(expected, actual);
        assertEquals("", CommonUtil.toLowerAndHalf(""));
        assertEquals(expected, CommonUtil.toLowerAndHalf(expected));
	}
}
