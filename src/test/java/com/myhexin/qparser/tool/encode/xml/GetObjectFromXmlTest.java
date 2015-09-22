/**
 * @author: 	    吴永行 
 * @dateTime:	  2013-12-24 下午4:31:24
 * @description:   	
 * 
 */
package com.myhexin.qparser.tool.encode.xml;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class GetObjectFromXmlTest {

	private GetObjectFromXml gofx = null;
	/**
	 * @author: 	    吴永行 
	 * @dateTime:	  2013-12-24 下午4:31:24
	 * @description:   	
	 * @throws java.lang.Exception
	 * 
	 */
	@Before
	public void setUp() throws Exception {
		gofx = new GetObjectFromXml("src/test/com/myhexin/qparser/tool/encode/xml/testGetXmlFromObject.xml");
	}

	/**
	 * Test method for {@link com.myhexin.qparser.tool.encode.xml.GetObjectFromXml#GetObjectFromXml(java.lang.String)}.
	 */
	@Test
	public void testGetObjectFromXml() {
		
	}

	/**
	 * Test method for {@link com.myhexin.qparser.tool.encode.xml.GetObjectFromXml#getList(java.lang.String)}.
	 */
	@Test
	public void testGetListString() {
		gofx.getList("list1");
	}

	/**
	 * Test method for {@link com.myhexin.qparser.tool.encode.xml.GetObjectFromXml#getList(java.lang.String, org.dom4j.Element)}.
	 */
	@Test
	public void testGetListStringElement() {
		gofx = new GetObjectFromXml("src/test/com/myhexin/qparser/tool/encode/xml/getListTest.xml");
		gofx.getList("list1");
	}

	/**
	 * Test method for {@link com.myhexin.qparser.tool.encode.xml.GetObjectFromXml#getBean(java.lang.String)}.
	 */
	@Test
	public void testGetBeanString() {
		gofx.getObject("bean1");
	}

	/**
	 * Test method for {@link com.myhexin.qparser.tool.encode.xml.GetObjectFromXml#getBean(java.lang.String, java.lang.Object)}.
	 */
	@Test
	public void testGetBeanStringObject() {
		gofx.getObject("set1");
	}

	/**
	 * Test method for {@link com.myhexin.qparser.tool.encode.xml.GetObjectFromXml#getContainer(java.lang.String)}.
	 */
	@Test
	public void testGetContainerString() {
		gofx.getObject("map1");

	}

	/**
	 * Test method for {@link com.myhexin.qparser.tool.encode.xml.GetObjectFromXml#getContainer(java.lang.String, java.lang.Object)}.
	 */
	@Test
	public void testGetContainerStringObject() {
		
	}

	/**
	 * Test method for {@link com.myhexin.qparser.tool.encode.xml.GetObjectFromXml#getClassType(java.lang.String)}.
	 * @throws ClassNotFoundException 
	 */
	@Test
	public void testGetClassType() throws ClassNotFoundException {
		gofx.getClassType("int");
		gofx.getClassType("double");
		gofx.getClassType("boolean");
		gofx.getClassType("float");
		gofx.getClassType("long");
		gofx.getClassType("shot");
		gofx.getClassType("char");
		gofx.getClassType("String");
		gofx.getClassType("String");
		gofx.getClassType("byte");
		
	}

}
