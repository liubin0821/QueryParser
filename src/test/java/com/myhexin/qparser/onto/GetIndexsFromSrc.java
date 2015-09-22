package com.myhexin.qparser.onto;

import junit.framework.TestCase;

// 利用Junit方式执行代码
public class GetIndexsFromSrc extends TestCase {
	
	public void test() {
		XMLWriterOnto.saveIndexsList();
		XMLWriterOnto.clearWhitespace();
	}
}
