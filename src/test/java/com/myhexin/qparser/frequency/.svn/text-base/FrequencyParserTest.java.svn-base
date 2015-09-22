/**
 * @author: 	    吴永行 
 * @dateTime:	  2013-12-26 下午4:24:13
 * @description:   	
 * 
 */
package com.myhexin.qparser.frequency;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import com.myhexin.qparser.Query;
import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.phrase.parsePlugins.UnitTestTools;

public class FrequencyParserTest {

	private FrequencyParser fp = null;
	/**
	 * @author: 	    吴永行 
	 * @dateTime:	  2013-12-26 下午4:24:13
	 * @description:   	
	 * @throws java.lang.Exception
	 * 
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * Test method for {@link com.myhexin.qparser.frequency.FrequencyParser#FrequencyParser(com.myhexin.qparser.Query)}.
	 */

	/**
	 * Test method for {@link com.myhexin.qparser.frequency.FrequencyParser#parse()}.
	 */
	@Test
	public void testParse() {
		String[] testCase = {"5日出现3次以上涨停",
							 "5日涨停出现3次以上",
							 "5日涨停出现3次以下",
							 "5日出现超过3次涨停",
							 "5日出现低于3次涨停",
							 "近5日有3天",
							 "5日3天",
							 "10月08日至10月09日 涨停 1次",
							 "5日出现 涨停 3次",
							 "5日出现3次"};
		String packetge = "com/myhexin/qparser/frequency/函数名称_";
		for (int i = 0; i < testCase.length; i++) {
			Query query = (Query) UnitTestTools.getXmlObj(packetge+testCase[i]+"_before.xml");
			Query result = (Query) UnitTestTools.getXmlObj(packetge+testCase[i]+"_after.xml");
			fp = new FrequencyParser();
			fp.parse(query.getNodes());
			assertEquals(true,UnitTestTools.isEqualNodesListNode(query.getNodes(), result.getNodes()));
		}
		
	}

}
