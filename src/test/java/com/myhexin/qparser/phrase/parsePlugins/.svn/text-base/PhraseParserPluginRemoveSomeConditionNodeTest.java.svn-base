/**
 * @author: 	    吴永行 
 * @dateTime:	  2013-12-30 下午3:35:18
 * @description:   	
 * 
 */
package com.myhexin.qparser.phrase.parsePlugins;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;

import org.junit.Test;

import com.myhexin.qparser.Param;

import com.myhexin.qparser.except.DataConfException;
import com.myhexin.qparser.node.Environment;
import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.onto.MemOnto;
import com.myhexin.qparser.phrase.ParserAnnotation;
import com.myhexin.qparser.util.Util;

public class PhraseParserPluginRemoveSomeConditionNodeTest {

	private PhraseParserPluginRemoveSomeConditionNode ppp = null;
	/**
	 * Test method for {@link com.myhexin.qparser.phrase.parsePlugins.PhraseParserPluginRemoveSomeConditionNode#process(java.util.ArrayList)}.
	 */
	@Test
	public void testProcess() {
		/*try {
			MemOnto.loadStockOnto(
					Util.readXMLFile(Param.DATA_ROOT+"/"+Param.STOCK_ONTO_FILE, true),
					Util.readXMLFile(Param.DATA_ROOT+"/"+Param.STOCK_ONTO_CONDITION_FILE, true));
		} catch (DataConfException e) {
			
			e.printStackTrace();
		}*/
		ppp = new PhraseParserPluginRemoveSomeConditionNode();
		
		String[] testCase = { "不包含交通运输行业",
					 		  "不包含行业交通运输",
							  "交通运输" };
			String packetge = "com/myhexin/qparser/phrase/parsePlugins/RemoveSomeConditionNode_";
			for (int i = 0; i < testCase.length; i++) {
			ArrayList<SemanticNode> nodes = (ArrayList<SemanticNode>) UnitTestTools.getXmlObj(packetge+i+"_before.xml");
			ArrayList<SemanticNode> result = (ArrayList<SemanticNode>) UnitTestTools.getXmlObj(packetge+i+"_after.xml");
			
			ParserAnnotation annotation = new ParserAnnotation();
	    	annotation.setEnv(new Environment());
	    	annotation.setNodes(nodes);
			
			ppp.process(annotation);
			assertEquals(true,UnitTestTools.isEqualNodesListNode(nodes, result));
			}
		}

}
