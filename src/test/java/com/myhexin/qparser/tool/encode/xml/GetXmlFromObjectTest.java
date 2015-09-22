/**
 * @author: 	    吴永行 
 * @dateTime:	  2013-12-24 下午4:32:05
 * @description:   	
 * 
 */
package com.myhexin.qparser.tool.encode.xml;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.myhexin.qparser.Param;
import com.myhexin.qparser.node.DateNode;

public class GetXmlFromObjectTest {

	 GetXmlFromObject gxfo = new GetXmlFromObject("src/test/com/myhexin/qparser/tool/encode/xml/testGetXmlFromObject.xml");
	static Object object = new Object();
	static List list1 = new ArrayList<Object>();
	static List list2 = new ArrayList<Object>();
	static List list3 = new ArrayList<Object>();
	static List list4 = new ArrayList<Object>();
	static List list5 = new ArrayList<Object>();
	static Map map1 = new HashMap<Object, Object>();
	static Map map2 = new HashMap<Object, Object>();
	static Map map3 = new HashMap<Object, Object>();
	static Map map4 = new HashMap<Object, Object>();
	static Map map5 = new HashMap<Object, Object>();
	static Set set1 = new HashSet<Object>();
	static Set set2 = new HashSet<Object>();
	static Set set3 = new HashSet<Object>();
	static Set set4 = new HashSet<Object>();
	static Set set5 = new HashSet<Object>();
	static DateNode dn1 = new DateNode("2010年");
		
	static{
		 list1.add(object);
		 list2.add(list1);
		 list3.add(map1);
		 list4.add(set1);
		 list5.add(dn1);	
		 
		 set1.add(object);
		 set2.add(list1);
		 set3.add(map1);
		 set4.add(set1);
		 set5.add(dn1);	
		 
		 
		 map1.put(object,object);
		 map2.put(list1,list1);
		 map3.put(map1,map1);
		 map4.put(set1,set1);
		 map5.put(dn1,dn1);			 			 
	 }

	/**
	 * @author: 	    吴永行 
	 * @dateTime:	  2013-12-24 下午4:32:05
	 * @description:   	
	 * @throws java.lang.Exception
	 * 
	 */
	@Before
	public void setUp() throws Exception {
		gxfo = new GetXmlFromObject("src/test/com/myhexin/qparser/tool/encode/xml/testGetXmlFromObject.xml");
	}


	/**
	 * Test method for {@link com.myhexin.qparser.tool.encode.xml.GetXmlFromObject#createXML(java.lang.Object)}.
	 */
	@Test
	public void testCreateXML2() {
		gxfo.createXML(new GetXmlFromObjectTest().list1);
	}
	@Test
	public void testCreateXML3() {
		gxfo.createXML(new GetXmlFromObjectTest().map1);
	}
	@Test
	public void testCreateXML4() {
		gxfo.createXML(new GetXmlFromObjectTest().set1);
	}
	
	@Test
	public void testCreateXML() {
		gxfo.createXML(new GetXmlFromObjectTest());
	}
	
	class TestOBJ{
		 Object object_ = object;
		 List list_ = list1;
		 Map map_ = map1;
		 Set set_ = set1;
		 DateNode dn_ = dn1;
		 
	}

}
