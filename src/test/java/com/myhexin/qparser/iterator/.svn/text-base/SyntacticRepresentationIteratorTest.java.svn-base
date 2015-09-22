package com.myhexin.qparser.iterator;

import org.junit.Test;


import junit.framework.TestCase;

public class SyntacticRepresentationIteratorTest extends TestCase {
	/**
	    * chineseRepresentation为空时。
	    */
	   @Test
	   public void testNullChineseRepresentation() {
		   SyntacticRepresentationIteratorImpl it = new SyntacticRepresentationIteratorImpl("");
		   assertEquals(false, it.hasNext());
		   assertEquals(null, it.next());
	   }
	   /**
	    * 测试当chineseRepresentation为“#1”时。
	    * 这样的情况为大多数情况，则其输出应该为#1
	    */
	   @Test
	   public void testOneChineseReepresentation(){
		   SyntacticRepresentationIteratorImpl it = new SyntacticRepresentationIteratorImpl("#1");
		   assertEquals(true, it.hasNext());
		   assertEquals("#1", it.next().getText());
		   assertEquals(false, it.hasNext());
		   assertEquals(null, it.next());
	   }
	   /**
	    * 测试当chineseRepresentation为“#1且”时。
	    * 这样的情况为错误情况，则其输出应该为#1，由于最后没有#2因此且是没有意义的。
	    */
	   @Test
	   public void testErrorEndChineseReepresentation(){
		   SyntacticRepresentationIteratorImpl it = new SyntacticRepresentationIteratorImpl("#1且");
		   assertEquals(true, it.hasNext());
		   assertEquals("#1", it.next().getText());
		   assertEquals(false, it.hasNext());
		   assertEquals(null, it.next());
	   }
	   /**
	    * 测试当chineseRepresentation为“且#1”时。
	    * 这样的情况为错误情况，则其输出应该为null并loger中记录ERROR信息。
	    */
	   @Test
	   public void testErrorStartChineseReepresentation(){
		   SyntacticRepresentationIteratorImpl it = new SyntacticRepresentationIteratorImpl("且#1");
		   assertEquals(false, it.hasNext());
		   assertEquals(null, it.next());
	   }
	   /**
	    * 测试当chineseRepresentation为“且”时。
	    * 这样的情况为错误情况，则其输出应该为null并loger中记录ERROR信息。
	    */
	   @Test
	   public void testErrorChineseReepresentation(){
		   SyntacticRepresentationIteratorImpl it = new SyntacticRepresentationIteratorImpl("且");
		   assertEquals(false, it.hasNext());
		   assertEquals(null, it.next());
	   }
	   /**
	    * 测试当chineseRepresentation为“#1且#2”时。
	    * 这样的情况为大多数情况，则其输出应该为#1
	    */
	   @Test
	   public void testLogicChineseReepresentation(){
		   SyntacticRepresentationIteratorImpl it = new SyntacticRepresentationIteratorImpl("#1且#2");
		   assertEquals(true, it.hasNext());
		   assertEquals("#1", it.next().getText());
		   assertEquals(true, it.hasNext());
		   assertEquals("且", it.next().getText());
		   assertEquals(true, it.hasNext());
		   assertEquals("#2", it.next().getText());
		   assertEquals(false, it.hasNext());
		   assertEquals(null, it.next());
	   }
}
