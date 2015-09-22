/**
 * @author: 	    吴永行 
 * @dateTime:	  2013-12-30 下午2:15:46
 * @description:   	
 * 
 */
package com.myhexin.qparser.phrase.parsePlugins;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.junit.Test;

import com.myhexin.qparser.node.Environment;
import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.phrase.ParserAnnotation;

public class PhraseParserPluginDateNumModifyTest {
	private PhraseParserPluginDateNumModify ppp = null;

	/**
	 * Test method for {@link com.myhexin.qparser.phrase.parsePlugins.PhraseParserPluginDateNumModify#process(java.util.ArrayList)}.
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
		ppp = new PhraseParserPluginDateNumModify();
		
		ParserAnnotation annotation = new ParserAnnotation();
    	annotation.setEnv(new Environment());
    	
		
		String[] testCase = { "每日收阳" };
		String packetge = "com/myhexin/qparser/phrase/parsePlugins/DateNumModify_";
		for (int i = 0; i < testCase.length; i++) {
			ArrayList<SemanticNode> nodes = (ArrayList<SemanticNode>) UnitTestTools.getXmlObj(packetge+i+"_before.xml");
			ArrayList<SemanticNode> result = (ArrayList<SemanticNode>) UnitTestTools.getXmlObj(packetge+i+"_after.xml");
			annotation.setNodes(nodes);
			ppp.process(annotation);
			assertEquals(true,UnitTestTools.isEqualNodesListNode(nodes, result));
		}
	}
	

}
