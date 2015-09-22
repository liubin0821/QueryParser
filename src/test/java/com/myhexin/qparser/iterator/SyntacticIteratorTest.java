package com.myhexin.qparser.iterator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.Test;

import junit.framework.TestCase;

import com.myhexin.qparser.Query.Type;
import com.myhexin.qparser.except.QPException;
import com.myhexin.qparser.node.SemanticNode;


public class SyntacticIteratorTest extends TestCase{
	private IteratorTestUtil itUtil = new IteratorTestUtil();
	   /**
	    * List<SemanticNode为空,则不会有下一个，next()返回为空。
	    */
	   @Test
	   public void testNullList() {
		   List<SemanticNode> nodes = new ArrayList<SemanticNode>();
		   SyntacticIteratorImpl it = new SyntacticIteratorImpl(nodes);
		   assertEquals(false, it.hasNext());
		   assertEquals(null, it.next());
	   }
	   /**
	    * 只有一个boundaryNode节点没有组合成一个完整的句式，则不会有下一个，next()返回为空。
	    */
     @Test
      public void testOneBoundaryNode() {
    	 List<SemanticNode> nodes = new ArrayList<SemanticNode>();
		 nodes.add(itUtil.createStartBoundaryNode());
    	 SyntacticIteratorImpl it = new SyntacticIteratorImpl(nodes);
		 assertEquals(false, it.hasNext());
		 assertEquals(null, it.next());
      }
      /**
       * 测试只有一个句式的情况其后面没有附带SemanticNode,那么start = bStart;end = bEnd。
       * 例如：只有StartBoundaryNode,EndBoundaryNode则start=bStart=0;bEnd=end=1.
       */
      @Test     
      public void testOneSyntactic() {
    	 List<SemanticNode> nodes = new ArrayList<SemanticNode>();
    	 nodes.add(itUtil.createStartBoundaryNode());
    	 nodes.add(itUtil.createEndBoundaryNode()); 
     	 SyntacticIteratorImpl it = new SyntacticIteratorImpl(nodes);
 		 assertEquals(true, it.hasNext());
		 BoundaryInfos pm = it.next();
	     assertEquals(0, pm.start);
	     assertEquals(0, pm.bStart);
	     assertEquals(1, pm.bEnd);
	     assertEquals(1, pm.end);
	     assertEquals(false, it.hasNext());
	   }
      /**
       * 测试只有一个句式的情况其后面有附带SemanticNode,那么start = bStart;bEnd为EndBoundaryNode的位置，end为整个list的最后。
       * 例如：只有StartBoundaryNode,EndBoundaryNode,SemanticNode则start=bStart=0;bEnd=1;end=2.
       */
      @Test     
      public void testOneSyntacticIncidentalSemanticNode() {
    	 List<SemanticNode> nodes = new ArrayList<SemanticNode>();
    	 nodes.add(itUtil.createStartBoundaryNode());
    	 nodes.add(itUtil.createEndBoundaryNode());
    	 nodes.add(new SemanticNode() {
			@Override
			public void parseNode(HashMap<String, String> k2v, Type qtype)
					throws QPException {
			}

			@Override
			public SemanticNode copy() {
				// TODO Auto-generated method stub
				return null;
			}
		});
     	 SyntacticIteratorImpl it = new SyntacticIteratorImpl(nodes);
 		 assertEquals(true, it.hasNext());
		 BoundaryInfos pm = it.next();
	     assertEquals(0, pm.start);
	     assertEquals(0, pm.bStart);
	     assertEquals(1, pm.bEnd);
	     assertEquals(2, pm.end);
	     assertEquals(false, it.hasNext());
	   }
      /**
       * 测试多个句式，那么start为前面一个的bEnd,bStart为前一个的end。
       */
      @Test
      public void testMoreSyntactic() {
	      List<SemanticNode> nodes = new ArrayList<SemanticNode>();
	      nodes.add(itUtil.createStartBoundaryNode());
	      nodes.add(itUtil.createEndBoundaryNode());
	      nodes.add(itUtil.createStartBoundaryNode());
	      nodes.add(itUtil.createEndBoundaryNode()); 
	      nodes.add(itUtil.createStartBoundaryNode());
	      nodes.add(itUtil.createEndBoundaryNode()); 
	       //得到句式迭代器
		  SyntacticIteratorImpl it = new SyntacticIteratorImpl(nodes);
		  BoundaryInfos pm = it.next();
		  assertEquals(0, pm.start);
		  assertEquals(0, pm.bStart);
		  assertEquals(1, pm.bEnd);
		  assertEquals(2, pm.end);
		  BoundaryInfos pm1 = it.next();
		  assertEquals(1, pm1.start);
		  assertEquals(2, pm1.bStart);
		  assertEquals(3, pm1.bEnd);
		  assertEquals(4, pm1.end);	
		  BoundaryInfos pm2 = it.next();
		  assertEquals(3, pm2.start);
		  assertEquals(4, pm2.bStart);
		  assertEquals(5, pm2.bEnd);
		  assertEquals(5, pm2.end);	
      }  
}
	 
	
