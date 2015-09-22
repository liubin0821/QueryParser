package com.myhexin.qparser.iterator;

import org.junit.Test;


import junit.framework.TestCase;

public class SemanticRepresentationIteratorTest extends TestCase {
	/**
     * chineseRepresentation为空时。
		  */
		   @Test
		   public void testNullChineseRepresentation() {
			   SemanticRepresentationIteratorImpl it = new SemanticRepresentationIteratorImpl("");
			   assertEquals(false, it.hasNext());
			   assertEquals(null, it.next());
		   }
		   /**
		    * 测试当chineseRepresentation为“$1”时。
		    * 其输出应该为：$1
		    */
		   @Test
		   public void testOne$ChineseReepresentation(){
			   SemanticRepresentationIteratorImpl it = new SemanticRepresentationIteratorImpl("$1");
			   assertEquals(true, it.hasNext());
			   assertEquals("$1", it.next().getText());
			   assertEquals(false, it.hasNext());
			   assertEquals(null, it.next());
		   }
		   /**
		    * 测试当chineseRepresentation为“$1增长”时。
		    * 其输出为两个：$1、增长
		    */
		   @Test
		   public void test$AndOneKeyWordChineseReepresentation(){
			   SemanticRepresentationIteratorImpl it = new SemanticRepresentationIteratorImpl("$1增长");
			   assertEquals(true, it.hasNext());
			   assertEquals("$1", it.next().getText());
			   assertEquals(true, it.hasNext());
			   assertEquals("增长", it.next().getText());
			   assertEquals(false, it.hasNext());
			   assertEquals(null, it.next());
		   }
		   /**
		    * 测试当chineseRepresentation为“增长$1”时。
		    * 其输出为两个：增长、$1
		    */
		   @Test
		   public void testOneKeyWordAnd$ChineseReepresentation(){
			   SemanticRepresentationIteratorImpl it = new SemanticRepresentationIteratorImpl("增长$1");
			   assertEquals(true, it.hasNext());
			   assertEquals("增长", it.next().getText());
			   assertEquals(true, it.hasNext());
			   assertEquals("$1", it.next().getText());
			   assertEquals(false, it.hasNext());
			   assertEquals(null, it.next());
		   }
		   /**
		    * 测试当chineseRepresentation为“增长”时。
		    * 其输出为：增长。
		    */
		   @Test
		   public void testOneKeyWordChineseReepresentation(){
			   SemanticRepresentationIteratorImpl it = new SemanticRepresentationIteratorImpl("增长");
			   assertEquals(true, it.hasNext());
			   assertEquals("增长", it.next().getText());
			   assertEquals(false, it.hasNext());
			   assertEquals(null, it.next());
		   }
		   /**
		    * 测试当chineseRepresentation为“$1增长$2”时。
		    * 其输出为三个：$1、增长、$2
		    */
		   @Test
		   public void test2$AndOneKeyWordChineseReepresentation(){
			   SemanticRepresentationIteratorImpl it = new SemanticRepresentationIteratorImpl("$1增长$2");
			   assertEquals(true, it.hasNext());
			   assertEquals("$1", it.next().getText());
			   assertEquals(true, it.hasNext());
			   assertEquals("增长", it.next().getText());
			   assertEquals(true, it.hasNext());
			   assertEquals("$2", it.next().getText());
			   assertEquals(false, it.hasNext());
			   assertEquals(null, it.next());
		   }
		   /**
		    * 测试当chineseRepresentation为“$1增长”时。
		    * 其输出为两个：$1、增长
		    */
		   @Test
		   public void test$AndMoreKeyWordChineseReepresentation(){
			   SemanticRepresentationIteratorImpl it = new SemanticRepresentationIteratorImpl("$1增长>=-");
			   assertEquals(true, it.hasNext());
			   assertEquals("$1", it.next().getText());
			   assertEquals(true, it.hasNext());
			   assertEquals("增长", it.next().getText());
			   assertEquals(true, it.hasNext());
			   assertEquals(">=", it.next().getText());
			   assertEquals(true, it.hasNext());
			   assertEquals("-", it.next().getText());
			   assertEquals(false, it.hasNext());
			   assertEquals(null, it.next());
		   }
		   /**
		    * 测试当chineseRepresentation为“增长$1”时。
		    * 其输出为两个：增长、$1
		    */
		   @Test
		   public void testMoreKeyWordAnd$ChineseReepresentation(){
			   SemanticRepresentationIteratorImpl it = new SemanticRepresentationIteratorImpl("增长>=-$1");
			   assertEquals(true, it.hasNext());
			   assertEquals("增长", it.next().getText());
			   assertEquals(true, it.hasNext());
			   assertEquals(">=", it.next().getText());
			   assertEquals(true, it.hasNext());
			   assertEquals("-", it.next().getText());
			   assertEquals(true, it.hasNext());
			   assertEquals("$1", it.next().getText());
			   assertEquals(false, it.hasNext());
			   assertEquals(null, it.next());
		   }
		   /**
		    * 测试当chineseRepresentation为“增长”时。
		    * 其输出为：增长。
		    */
		   @Test
		   public void testMoreKeyWordChineseReepresentation(){
			   SemanticRepresentationIteratorImpl it = new SemanticRepresentationIteratorImpl("增长>=-");
			   assertEquals(true, it.hasNext());
			   assertEquals("增长", it.next().getText());
			   assertEquals(true, it.hasNext());
			   assertEquals(">=", it.next().getText());
			   assertEquals(true, it.hasNext());
			   assertEquals("-", it.next().getText());
			   assertEquals(false, it.hasNext());
			   assertEquals(null, it.next());
		   }
		   /**
		    * 测试当chineseRepresentation为“$1增长>=-$2”时。
		    * 其输出为五个：$1、增长、>=、-、$2
		    */
		   @Test
		   public void test2$AndMoreKeyWordChineseReepresentation(){
			   SemanticRepresentationIteratorImpl it = new SemanticRepresentationIteratorImpl("$1增长>=-$2");
			   assertEquals(true, it.hasNext());
			   assertEquals("$1", it.next().getText());
			   assertEquals(true, it.hasNext());
			   assertEquals("增长", it.next().getText());
			   assertEquals(true, it.hasNext());
			   assertEquals(">=", it.next().getText());
			   assertEquals(true, it.hasNext());
			   assertEquals("-", it.next().getText());
			   assertEquals(true, it.hasNext());
			   assertEquals("$2", it.next().getText());
			   assertEquals(false, it.hasNext());
			   assertEquals(null, it.next());
		   }
	}

