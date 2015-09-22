package com.myhexin.qparser.phrase.parsePlugins;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.myhexin.qparser.ApplicationContext.ApplicationContextHelper;
import com.myhexin.qparser.node.Environment;
import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.phrase.ParserAnnotation;
import com.myhexin.qparser.phrase.PhraseParser;
import com.myhexin.qparser.tool.encode.xml.GetObjectFromXml;

public class PhraseParserPluginBindProp2IndexTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

		// 加载xml文件，初始spring容器
		ApplicationContextHelper.loadApplicationContext();

		// phraseParser配置于qparser_unit_test.xml文件
		PhraseParser parser = (PhraseParser) ApplicationContextHelper.getBean("phraseParserUnitTest");
		parser.setSplitWords("; ");
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}
	
	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testProcess() {
		String query = "pe>10";
		String fileName = query.hashCode()+"-PhraseParserPluginBindProp2Index";
		ArrayList<SemanticNode> nodes = (ArrayList<SemanticNode>) new GetObjectFromXml("unit_test/"+fileName+"Before.xml").getContainer("list1");
		ArrayList<ArrayList<SemanticNode>> expected = (ArrayList<ArrayList<SemanticNode>>) new GetObjectFromXml("unit_test/"+fileName+"After.xml").getContainer("list1");
		ArrayList<ArrayList<SemanticNode>> resultlist = null;
		ParserAnnotation annotation = new ParserAnnotation();
    	annotation.setEnv(new Environment());
    	annotation.setNodes(nodes);
		
		PhraseParserPluginAbstract bindProp2Index = new  PhraseParserPluginBindStrToIndex();
		resultlist = bindProp2Index.process(annotation);
		assertEquals(true, UnitTestTools.isEqualNodesList(expected, resultlist));
	}

}
