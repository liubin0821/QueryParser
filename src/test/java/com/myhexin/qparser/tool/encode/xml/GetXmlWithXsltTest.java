/**
 * @author: 	    吴永行 
 * @dateTime:	  2013-12-26 下午3:39:41
 * @description:   	
 * 
 */
package com.myhexin.qparser.tool.encode.xml;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class GetXmlWithXsltTest {

	/**
	 * @author: 	    吴永行 
	 * @dateTime:	  2013-12-26 下午3:39:41
	 * @description:   	
	 * @throws java.lang.Exception
	 * 
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * Test method for {@link com.myhexin.qparser.tool.encode.xml.GetXmlWithXslt#translate(java.lang.String, java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testTranslate() {
		GetXmlWithXslt.translate("src/test/com/myhexin/qparser/tool/encode/xml/result.xml", 
				"src/test/com/myhexin/qparser/tool/encode/xml/semanticPatternMap_.xsl",
				"src/test/com/myhexin/qparser/tool/encode/xml/semanticPatternMap_.xml");
	}

}
