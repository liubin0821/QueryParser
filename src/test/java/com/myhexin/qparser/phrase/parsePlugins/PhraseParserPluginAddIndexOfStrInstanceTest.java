package com.myhexin.qparser.phrase.parsePlugins;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.myhexin.qparser.node.Environment;
import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.phrase.ParserAnnotation;
import com.myhexin.qparser.tool.encode.xml.GetObjectFromXml;

/**
 * 
 * @author huangmin
 *
 */
public class PhraseParserPluginAddIndexOfStrInstanceTest extends
		PhraseParserPluginBaseTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * 
	 * @author huangmin
	 *
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void addIndexOfStrInstanceTest() throws Exception {
		String query = "pe>10";
		String fileName = query.hashCode()
				+ "-PhraseParserPluginAddIndexOfStrInstance";
		ArrayList<SemanticNode> nodes = (ArrayList<SemanticNode>) new GetObjectFromXml(
				"unit_test/" + fileName + "Before.xml").getContainer("list1");
		ArrayList<ArrayList<SemanticNode>> expected = (ArrayList<ArrayList<SemanticNode>>) new GetObjectFromXml(
				"unit_test/" + fileName + "After.xml").getContainer("list1");
		ParserAnnotation annotation = new ParserAnnotation();
		annotation.setEnv(new Environment());
		annotation.setNodes(nodes);

		PhraseParserPluginAbstract addIndexOfStrInstancePlugin = getPluginByName(ADD_INDEX_OF_STRINSTANCE);
		ArrayList<ArrayList<SemanticNode>> actual = addIndexOfStrInstancePlugin
				.process(annotation);
		assertTrue(UnitTestTools.isEqualNodesList(expected, actual));
	}
}
