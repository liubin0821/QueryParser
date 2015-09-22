/**
 * @author: 	    吴永行 
 * @dateTime:	  2013-12-26 上午10:51:51
 * @description:   	
 * 
 */
package com.myhexin.qparser.tool.encode.xml;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.myhexin.qparser.node.DateNode;

public class ReflectionUtilsTest {

	private ReflectionUtils ru = new ReflectionUtils();
	DateNode dn = new DateNode("2010年");
	
	/**
	 * @author: 	    吴永行 
	 * @dateTime:	  2013-12-26 上午10:51:51
	 * @description:   	
	 * @throws java.lang.Exception
	 * 
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * Test method for {@link com.myhexin.qparser.tool.encode.xml.ReflectionUtils#getDeclaredMethod(java.lang.Object, java.lang.String, java.lang.Class<?>[])}.
	 */
	@Test
	public void testGetDeclaredMethod() {
		assertEquals(null, ReflectionUtils.getDeclaredMethod(ru, "toString", null));
	}

	/**
	 * Test method for {@link com.myhexin.qparser.tool.encode.xml.ReflectionUtils#invokeMethod(java.lang.Object, java.lang.String, java.lang.Class<?>[], java.lang.Object[])}.
	 */
	@Test
	public void testInvokeMethod() {
		
		assertEquals("NodeType:DATE  NodeText:2010年  DateUnit:UNKNOWN",ReflectionUtils.invokeMethod(dn, "toString", null, null));
		
	}

	/**
	 * Test method for {@link com.myhexin.qparser.tool.encode.xml.ReflectionUtils#getDeclaredField(java.lang.Object, java.lang.String)}.
	 */
	@Test
	public void testGetDeclaredField() {
		assertEquals("public java.lang.String com.myhexin.qparser.node.SemanticNode.text",
				ReflectionUtils.getDeclaredField(dn, "text").toString());
	}

	/**
	 * Test method for {@link com.myhexin.qparser.tool.encode.xml.ReflectionUtils#getDeclaredFields(java.lang.Object)}.
	 */
	@Test
	public void testGetDeclaredFields() {
		assertEquals("[public static final int java.lang.Integer.MIN_VALUE, public static final int java.lang.Integer.MAX_VALUE, public static final java.lang.Class java.lang.Integer.TYPE, static final char[] java.lang.Integer.digits, static final char[] java.lang.Integer.DigitTens, static final char[] java.lang.Integer.DigitOnes, static final int[] java.lang.Integer.sizeTable, private final int java.lang.Integer.value, public static final int java.lang.Integer.SIZE, private static final long java.lang.Integer.serialVersionUID, private static final long java.lang.Number.serialVersionUID]",
				ReflectionUtils.getDeclaredFields(dn.getSyntacticNum()).toString());
	}

	/**
	 * Test method for {@link com.myhexin.qparser.tool.encode.xml.ReflectionUtils#getFieldValue(java.lang.Object, java.lang.String)}.
	 */
	@Test
	public void testGetFieldValue() {
		assertEquals("2010年",ReflectionUtils.getFieldValue(dn, "text"));
	}
	
	/**
	 * Test method for {@link com.myhexin.qparser.tool.encode.xml.ReflectionUtils#setFieldValue(java.lang.Object, java.lang.String, java.lang.Object)}.
	 */
	@Test
	public void testSetFieldValue() {
		ReflectionUtils.setFieldValue(dn, "text", "2014年");
		assertEquals("2014年",dn.getText());
	}



}
