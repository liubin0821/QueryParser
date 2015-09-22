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
import com.myhexin.qparser.phrase.parsePlugins.bind.stateMachine.PhraseParserPluginOneTripBindPropToIndex;
import com.myhexin.qparser.resource.Resource;
import com.myhexin.qparser.tool.encode.xml.GetObjectFromXml;

import junit.framework.TestCase;

public class PhraseParserPluginOneTripBindPropToIndexTest extends TestCase {

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
		String query = "董事长姓名马云年龄25岁";
		String fileNameBefore = query.hashCode()+"-PhraseParserPluginDateNumModify";	
		ArrayList<SemanticNode> nodes = (ArrayList<SemanticNode>) new GetObjectFromXml("unit_test/"+fileNameBefore+"After.xml").getContainer("list1");

		String fileNameAfter = query.hashCode()+"-PhraseParserPluginOneTripBindPropToIndex";	
		ArrayList<SemanticNode> expected = (ArrayList<SemanticNode>) new GetObjectFromXml("unit_test/"+fileNameAfter+"After.xml").getContainer("list1");
		PhraseParserPluginOneTripBindPropToIndex oneTripBindPropToIndex = new  PhraseParserPluginOneTripBindPropToIndex();
		
		ParserAnnotation annotation = new ParserAnnotation();
    	annotation.setEnv(new Environment());
    	annotation.setNodes(nodes);
		
    	ArrayList<ArrayList<SemanticNode>> actual = oneTripBindPropToIndex.process(annotation);
		//ArrayList<ArrayList<SemanticNode>> actual = (ArrayList<ArrayList<SemanticNode>>) newENV.get("resultList");
		assertEquals(true, UnitTestTools.isEqualNodesListNode(expected, actual.get(0)));
	}
}
