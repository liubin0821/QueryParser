/**
 * @author: 	    吴永行 
 * @dateTime:	  2013-12-30 上午10:05:28
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

public class PhraseParserPluginCombineOnePhraseDateTest {

	private PhraseParserPluginCombineOnePhraseDate ppp = null;
	/**
	 * Test method for {@link com.myhexin.qparser.phrase.parsePlugins.PhraseParserPluginCombineOnePhraseDate#process(java.util.ArrayList)}.
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
		ppp = new PhraseParserPluginCombineOnePhraseDate();
		
		String[] testCase = { "2010年的3月涨停",
							"3年的3月涨停",
							"连续3年的3月涨停",
							"5日",
							"2010年一季度的季报业绩大增",
							"12月31日的至今的涨停",
							"2013年的连续3月涨停",
							"2012年的连续3月涨停",
							"2013年的一季报涨停",
							"2013年一季度前哈连续三个季度业绩大增" };
			String packetge = "com/myhexin/qparser/phrase/parsePlugins/CombineOnePhraseDate_";
			
			ParserAnnotation annotation = new ParserAnnotation();
	    	annotation.setEnv(new Environment());
	    	
			
			for (int i = 0; i < testCase.length; i++) {
			ArrayList<SemanticNode> nodes = (ArrayList<SemanticNode>) UnitTestTools.getXmlObj(packetge+i+"_before.xml");
			ArrayList<SemanticNode> result = (ArrayList<SemanticNode>) UnitTestTools.getXmlObj(packetge+i+"_after.xml");
			
			annotation.setNodes(nodes);
			ppp.process(annotation);
			assertEquals(true,UnitTestTools.isEqualNodesListNode(nodes, result));
			}
		}

}
