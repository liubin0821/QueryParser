package com.myhexin.qparser.iterator;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.myhexin.qparser.node.BoundaryNode;
import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.tool.encode.xml.GetObjectFromXml;


import junit.framework.TestCase;

public class IndexIteratorTest extends TestCase{
	private ArrayList<SemanticNode> nodes= new ArrayList<SemanticNode>();
	@Before
	public void setUp(){
		String query = "董事长马云本科男";
		String fileName = query.hashCode()+"-PhraseParserPluginAddIndexOfStrInstance";
		ArrayList<ArrayList<SemanticNode>> expected = (ArrayList<ArrayList<SemanticNode>>) new GetObjectFromXml("unit_test/" + fileName + "After.xml").getObject();	
		nodes = expected.get(0);
	}
	/**
	  * List<SemanticNode为空
	  */
    @Test
	public void testNullList() {
		   List<SemanticNode> nodes = new ArrayList<SemanticNode>();
		   IndexIteratorImpl it = new IndexIteratorImpl(nodes);
		   it.first();
		   assertEquals(false, it.hasNext());
	   }
    /**
     * 对问句："董事长马云本科男"，前后遍历
     */
   @Test
   public void testNext(){
	   IndexIteratorImpl it = new IndexIteratorImpl(nodes);
	   assertEquals(true, it.hasNext());
	   assertEquals("董事长", it.next().getText());
	   assertEquals(true, it.hasNext());
	   assertEquals("姓名", it.next().getText());
	   assertEquals(true, it.hasNext());
	   assertEquals("学历", it.next().getText());
	   assertEquals(true, it.hasNext());
	   assertEquals("性别", it.next().getText());
	   assertEquals(false, it.hasNext());
   }
    
    /**
     * 对问句："董事长马云本科男"，前后遍历
     */
   @Test
   public void testNextAndPrev(){
	   IndexIteratorImpl it = new IndexIteratorImpl(nodes);
	   assertEquals(true, it.hasNext());
	   assertEquals("董事长", it.next().getText());
	   assertEquals(false, it.hasPrev());
	   assertEquals(true, it.hasNext());
	   assertEquals("姓名", it.next().getText());
	   assertEquals(true, it.hasPrev());
	   assertEquals("董事长", it.prev().getText());
   }
   /**
    * 测试得到当前指标的Semantic节点信息
    */
   @Test
   public void testGetCurrentSemantic(){
	   IndexIteratorImpl it = new IndexIteratorImpl(nodes);
	   assertEquals(true, it.hasNext());
	   assertEquals("董事长", it.next().getText());
	   //董事长为正常指标
	   assertEquals("董事长", it.getCurrentSemanticNode().getText());
	   assertEquals(false, it.hasPrev());
	   assertEquals(true, it.hasNext());
	   assertEquals("姓名", it.next().getText());
	  // System.out.println(it.getCurrentSemanticNode().text);
	   //姓名为默认指标。其是由StartBoundaryNode所生产的。
	   assertEquals(true, ((BoundaryNode)it.getCurrentSemanticNode()).isStart());
	   assertEquals(true, it.hasPrev());
	   assertEquals("董事长", it.prev().getText());
   }
   /**
    * 对单个句式中指标的遍历，如"董事长马云本科男"。
    */
   @Test
   public void testOneSyntacticNext(){
	   IndexIteratorImpl it = new IndexIteratorImpl(nodes,3,5);
	   assertEquals(true, it.hasNext());
	   assertEquals("姓名", it.next().getText());
	   assertEquals(false, it.hasNext());
   }

}
