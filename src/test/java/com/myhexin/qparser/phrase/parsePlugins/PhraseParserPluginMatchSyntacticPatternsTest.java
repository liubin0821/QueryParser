package com.myhexin.qparser.phrase.parsePlugins;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.myhexin.qparser.ApplicationContext.ApplicationContextHelper;
import com.myhexin.qparser.define.EnumDef.Unit;
import com.myhexin.qparser.except.UnexpectedException;
import com.myhexin.qparser.node.Environment;
import com.myhexin.qparser.node.FocusNode;
import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.node.UnknownNode;
import com.myhexin.qparser.phrase.ParserAnnotation;
import com.myhexin.qparser.phrase.PhraseInfo;
import com.myhexin.qparser.phrase.PhraseParser;
import com.myhexin.qparser.phrase.SyntacticSemantic.Semantic.SemanticArgument;
import com.myhexin.qparser.phrase.SyntacticSemantic.Semantic.SemanticArgument.ValueType;
import com.myhexin.qparser.phrase.SyntacticSemantic.Syntactic.SyntacticElement;
import com.myhexin.qparser.phrase.SyntacticSemantic.Syntactic.SyntacticPattern;
import com.myhexin.qparser.tool.encode.xml.GetObjectFromXml;

public class PhraseParserPluginMatchSyntacticPatternsTest {
	public static String testClassName = "com.myhexin.qparser.phrase.parsePlugins.PhraseParserPluginMatchSyntacticPatternsBreadthFirst";
	
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
		String fileName = query.hashCode()+"-PhraseParserPluginMatchSyntacticPatternsBreadthFirst";
		ArrayList<SemanticNode> nodes = (ArrayList<SemanticNode>) new GetObjectFromXml("unit_test/" + fileName + "Before.xml").getObject();
		ArrayList<ArrayList<SemanticNode>> expected = (ArrayList<ArrayList<SemanticNode>>) new GetObjectFromXml("unit_test/" + fileName + "After.xml").getObject();
		
		PhraseParserPluginAbstract matchSyntacticPatternsBreadthFirst = new  PhraseParserPluginMatchSyntacticPatterns();
		
		ParserAnnotation annotation = new ParserAnnotation();
    	annotation.setEnv(new Environment());
    	annotation.setNodes(nodes);
		
    	ArrayList<ArrayList<SemanticNode>> actual = matchSyntacticPatternsBreadthFirst.process(annotation);
		//ArrayList<ArrayList<SemanticNode>> actual = (ArrayList<ArrayList<SemanticNode>>) newENV.get("resultList");
		assertEquals(true, UnitTestTools.isEqualNodesList(expected, actual));
	}

	@Test
	public void testMatchSyntacticPatterns() {
		String query = "pe>10";
		String fileName = query.hashCode()+"-PhraseParserPluginMatchSyntacticPatternsBreadthFirst";
		ArrayList<SemanticNode> nodes = (ArrayList<SemanticNode>) new GetObjectFromXml("unit_test/" + fileName + "Before.xml").getObject();
		ArrayList<ArrayList<SemanticNode>> expected = (ArrayList<ArrayList<SemanticNode>>) new GetObjectFromXml("unit_test/" + fileName + "After.xml").getObject();
		
		String className = testClassName;
		String methodName = "matchSyntacticPatterns";
		Object[] prams = { nodes };
		ArrayList<ArrayList<SemanticNode>> actual = (ArrayList<ArrayList<SemanticNode>>) UnitTestTools.invokePrivateMethod(className, methodName, prams);
		assertEquals(true, UnitTestTools.isEqualNodesList(expected, actual));
		
		Object[] pramsNull = { null };
		actual = (ArrayList<ArrayList<SemanticNode>>) UnitTestTools.invokePrivateMethod(className, methodName, pramsNull);
		assertEquals(null, actual);
	}
	
	@Test
	public void testIsDone() {
    	
	}
    
	@Test
	public void testgetMinListAndNonMinList() {
    	
    }
    
	@Test
	public void testGetNextKeywordNodePos() {
    	
    }
    
	@Test
	public void testmergeMatcheds() {
    	
    }
    
	@Test
	public void testMergeImplicitMatcheds() {
    	
    }
    
	@Test
	public void testMatchSyntacticPatternsFromBreadthFirst() {
		
    }
    
	@Test
	public void testCheckIndexNode() {
		//SemanticNode node, SemanticArgument arg
		String className = testClassName;
		String methodName = "checkIndexNode";
		
		Object[] pramsNull = { null, null };
		assertEquals(false, UnitTestTools.invokePrivateMethod(className, methodName, pramsNull));
		
		String[] indexStrs = {"涨跌幅", "收盘价", "macd", "收盘价", "涨跌幅", "收盘价"};
		String[] patternIds = {"1165", "1165", "76", "76", "668", "668"};
		String[] poss = {"1", "1", "1", "1", "1", "1"};
		boolean[] expected = {true, false, true, false, true, true};
		for (int i=0; i < indexStrs.length; i++) {
			SemanticNode node = ParsePluginsUtil.getIndexNodeFromStr(indexStrs[i]);
			String patternId = patternIds[i];
			String pos = poss[i];
			SemanticArgument argument = getArgumentByPatternIdAndPos(patternId, pos);
			Object[] prams = { node, argument };
			boolean actual = (Boolean) UnitTestTools.invokePrivateMethod(className, methodName, prams);
			assertEquals(expected[i], actual);
		}
    }
	
	public SyntacticElement getElementByPatternIdAndPos(String patternId, String pos) {
		SyntacticPattern syntPtn = PhraseInfo.getSyntacticPattern(patternId);
		SyntacticElement element = syntPtn.getSyntacticElement(pos);
		return element;
	}
	
	public SemanticArgument getArgumentByPatternIdAndPos(String patternId, String pos) {
		SyntacticPattern syntPtn = PhraseInfo.getSyntacticPattern(patternId);
		SyntacticElement element = syntPtn.getSyntacticElement(pos);
		SemanticArgument argument = element.getArgument();
		return argument;
	}
    
	@Test
	public void testCheckIndexNodeValType() {
		// SemanticNode node, SemanticArgument.ValueType valueType
		String className = testClassName;
		String methodName = "checkIndexNodeValType";
		Object[] pramsNull = { null, ValueType.UNDEFINED };
		assertEquals(false, UnitTestTools.invokePrivateMethod(className, methodName, pramsNull));
		
		String[] indexStrs = {"业绩预告类型", "收盘价", "市盈率", "首发上市日期", "收盘价", "市盈率"};
		ValueType[] valueTypes = {ValueType.STRING, ValueType.NUMBER, ValueType.PERCENTAGE, ValueType.DATE, ValueType.UNDEFINED, ValueType.UNDEFINED};
		boolean[] expected = {true, true, true, true, true, true};
		for (int i=0; i < indexStrs.length; i++) {
			SemanticNode node = ParsePluginsUtil.getIndexNodeFromStr(indexStrs[i]);
			ValueType valueType = valueTypes[i];
			Object[] prams = { node, valueType };
			boolean actual = (Boolean) UnitTestTools.invokePrivateMethod(className, methodName, prams);
			assertEquals(expected[i], actual);
		}
    }

	@Test
	public void testCheckConstantNode() {
		String className = testClassName;
		String methodName = "checkConstantNode";
		
		Object[] pramsNull = { null, null };
		assertEquals(false, UnitTestTools.invokePrivateMethod(className, methodName, pramsNull));
		
		String[] constantStrs = {"压缩机/onto_value:prop=_主营产品名称", "3d打印/onto_value:prop=_所属概念", "压缩机/onto_value:prop=_主营产品名称", "10/onto_num:", "10%/onto_num:", "3季报/onto_date:", "10/onto_num:", "0.1/onto_num:"};
		String[] patternIds = {"1121", "1129", "1129", "466", "479", "1180", "772", "772"};
		String[] poss = {"1", "1", "1", "4", "3", "1", "1", "1"};
		boolean[] expected = {true, true, false, true, true, true, true, true};
		for (int i=0; i < constantStrs.length; i++) {
			SemanticNode node = UnitTestTools.parseNodeFromToken(constantStrs[i]);
			String patternId = patternIds[i];
			String pos = poss[i];
			SemanticArgument argument = getArgumentByPatternIdAndPos(patternId, pos);
			Object[] prams = { node, argument };
			boolean actual = (Boolean) UnitTestTools.invokePrivateMethod(className, methodName, prams);
			assertEquals(expected[i], actual);
		}
    }
    
	@Test
	public void testCheckConstantNodeValType() {
		String className = testClassName;
		String methodName = "checkConstantNodeValType";
		
		Object[] pramsNull = { null, null };
		assertEquals(false, UnitTestTools.invokePrivateMethod(className, methodName, pramsNull));
		
		String[] constantStrs = {"压缩机/onto_value:prop=_主营产品名称", "3d打印/onto_value:prop=_所属概念", "压缩机/onto_value:prop=_主营产品名称", "10/onto_num:", "10%/onto_num:", "3季报/onto_date:", "10/onto_num:", "0.1/onto_num:"};
		ValueType[] valueTypes = {ValueType.STRING, ValueType.STRING, ValueType.STRING, ValueType.NUMBER, ValueType.PERCENTAGE, ValueType.DATE, ValueType.UNDEFINED, ValueType.UNDEFINED};
		boolean[] expected = {true, true, true, true, true, true, true, true};
		
		for (int i=0; i < constantStrs.length; i++) {
			SemanticNode node = UnitTestTools.parseNodeFromToken(constantStrs[i]);
			Object[] prams = { node, valueTypes[i] };
			boolean actual = (Boolean) UnitTestTools.invokePrivateMethod(className, methodName, prams);
			assertEquals(expected[i], actual);
		}
    }

	@Test
	public void testCheckNodeSemanticConstrains() {
		String className = testClassName;
		String methodName = "checkNodeSemanticConstrains";
		
		Object[] pramsNull = { null, null };
		assertEquals(false, UnitTestTools.invokePrivateMethod(className, methodName, pramsNull));
		
		String[] indexStrs = {"涨跌幅", "收盘价", "macd", "收盘价", "涨跌幅", "收盘价"};
		String[] patternIds = {"1165", "1165", "76", "76", "668", "668"};
		String[] poss = {"1", "1", "1", "1", "1", "1"};
		boolean[] expected = {true, false, true, false, true, true};
		for (int i=0; i < indexStrs.length; i++) {
			SemanticNode node = ParsePluginsUtil.getIndexNodeFromStr(indexStrs[i]);
			String patternId = patternIds[i];
			String pos = poss[i];
			SemanticArgument argument = getArgumentByPatternIdAndPos(patternId, pos);
			Object[] prams = { node, argument };
			boolean actual = (Boolean) UnitTestTools.invokePrivateMethod(className, methodName, prams);
			assertEquals(expected[i], actual);
		}
		
		String[] constantStrs = {"压缩机/onto_value:prop=_主营产品名称", "3d打印/onto_value:prop=_所属概念", "压缩机/onto_value:prop=_主营产品名称", "10/onto_num:", "10%/onto_num:", "3季报/onto_date:"};
		patternIds = new String[]{"1121", "1129", "1129", "466", "479", "1180"};
		poss = new String[]{"1", "1", "1", "4", "3", "1"};
		expected = new boolean[]{true, true, false, true, true, true, true, true};
		for (int i=0; i < constantStrs.length; i++) {
			SemanticNode node = UnitTestTools.parseNodeFromToken(constantStrs[i]);
			String patternId = patternIds[i];
			String pos = poss[i];
			SemanticArgument argument = getArgumentByPatternIdAndPos(patternId, pos);
			Object[] prams = { node, argument };
			boolean actual = (Boolean) UnitTestTools.invokePrivateMethod(className, methodName, prams);
			assertEquals(expected[i], actual);
		}
    }

	@Test
	public void testMatchNodeWithSyntacticPatternElement() {
        
    }

	@Test
	public void testAddIndexsOfAlias() {
    	
    }
    
	@Test
	public void testGetDefalutIndexNodeForAbsentElem() {
        
    }
    
	@Test
	public void testMatchSyntacticPatternElementsAbsent() {
		
    }

	@Test
	public void testMatchSyntacticPatternElementsIndexList() {
		
	}
	
	@Test
	public void testMatchSyntacticPatternElementsConstantList() {
		
	}
	
	@Test
	public void testMatchSyntacticPatternElements() {
        
    }
    
	@Test
	public void testMatchOneSyntacticPattern() {
        
    }

	@Test
	public void testInsertImplicitBinaryRelation() {
        
    }
    
	@Test
	public void testMatchValueNodeToIndexNode() {
        
    }

	@Test
	public void testIsStrValInstance() throws UnexpectedException {
		String className = testClassName;
		String methodName = "isStrValInstance";
		// null
		Object[] pramsNull = { null };
		assertEquals(false, (Boolean) UnitTestTools.invokePrivateMethod(className, methodName, pramsNull));
		// isCombined
		SemanticNode snCombined = new UnknownNode("");
		snCombined.isCombined = true;
		Object[] temp = { snCombined };
		assertEquals(false, (Boolean) UnitTestTools.invokePrivateMethod(className, methodName, temp));
		// 压缩机->str	预增->str+keyword	政府津贴->index+str	收盘价->index
		String[] testCases = {"压缩机/onto_value:prop=_主营产品名称", "预增/onto_value:prop=_业绩预告类型", "政府津贴/onto_value:prop=_政府奖励", "收盘价/onto_class:"};
		boolean[] expected = {true, true, true, false};
		for (int i = 0; i < testCases.length; i++) {
			String testCase = testCases[i];
			SemanticNode sn = UnitTestTools.parseNodeFromToken(testCase);
			Object[] params = { sn };
			FocusNode fn = ParsePluginsUtil.getFocusNode(sn.getText(), sn);
	        if (fn != null)
	        	params[0] = fn;
			boolean actual = (Boolean) UnitTestTools.invokePrivateMethod(className, methodName, params);
			assertEquals(expected[i], actual);
		}
	}
	
	@Test
	public void testGetStrValInstance() throws UnexpectedException {
		String className = testClassName;
		String methodName = "getStrValInstance";
		// null
		Object[] pramsNull = { null };
		assertEquals(null, UnitTestTools.invokePrivateMethod(className, methodName, pramsNull));
		// isCombined
		SemanticNode snCombined = new UnknownNode("");
		snCombined.isCombined = true;
		Object[] temp = { snCombined };
		assertEquals(null, UnitTestTools.invokePrivateMethod(className, methodName, temp));
		// 压缩机->str	预增->str+keyword	政府津贴->index+str	收盘价->index
		String[] testCases = {"压缩机/onto_value:prop=_主营产品名称", "预增/onto_value:prop=_业绩预告类型", "政府津贴/onto_value:prop=_政府奖励", "收盘价/onto_class:"};
		boolean[] expected = {true, true, true, false};
		for (int i = 0; i < testCases.length; i++) {
			String testCase = testCases[i];
			SemanticNode sn = UnitTestTools.parseNodeFromToken(testCase);
			Object[] params = { sn };
			FocusNode fn = ParsePluginsUtil.getFocusNode(sn.getText(), sn);
	        if (fn != null)
	        	params[0] = fn;
			SemanticNode actual = (SemanticNode) UnitTestTools.invokePrivateMethod(className, methodName, params);
			assertEquals(expected[i], sn.equals(actual));
		}
	}
    
	@Test
	public void testMatchImplicitBinaryRelation() {
		
	}

	@Test
	public void testCanBeMatchedStrInstance() throws UnexpectedException {
		String className = testClassName;
		String methodName = "canBeMatchedStrInstance";
		// null
		Object[] pramsNull = { null };
		assertEquals(false, UnitTestTools.invokePrivateMethod(className, methodName, pramsNull));
		
		// 10->num	去年->date	前复权->str复权方式	硕士->str_学历	预增->str+keyword	政府津贴->index+str	收盘价->index
		String[] testCases = {"10/onto_num:", "去年/onto_date:", "前复权/onto_value:prop=复权方式",  "硕士/onto_value:prop=_学历", "预增/onto_value:prop=_业绩预告类型", "政府津贴/onto_value:prop=_政府奖励", "收盘价/onto_class:"};
		boolean[] expected = {false, false, false, true, true, true, false};
		for (int i = 0; i < testCases.length; i++) {
			String testCase = testCases[i];
			SemanticNode sn = UnitTestTools.parseNodeFromToken(testCase);
			Object[] params = { sn };
			FocusNode fn = ParsePluginsUtil.getFocusNode(sn.getText(), sn);
	        if (fn != null)
	        	params[0] = fn;
			boolean actual = (Boolean) UnitTestTools.invokePrivateMethod(className, methodName, params);
			assertEquals(expected[i], actual);
		}
	}

	@Test
	public void testCheckMatchedByIndexlistCount() {
        
    }
    
	@Test
	public void testCheckMatchedByConstantlistCount() {
        
    }

	@Test
	public void testEvaluateSyntacticPatterns() {
	    
    }
    
	@Test
	public void testEvaluateSyntacticPatternsSamePos() {
	    
    }
    
	@Test
	public void testIsUnitEquals() {
		String className = testClassName;
		String methodName = "isUnitEquals";
		//Unit unit1, ArrayList<Unit> unit2
		Unit[] unit1 = {Unit.UNKNOWN, Unit.UNKNOWN, Unit.UNKNOWN, Unit.BEI, Unit.BEI, Unit.PERCENT, Unit.YUAN};
		Object[] unit2 = {null, new ArrayList<Unit>(), new ArrayList(Arrays.asList(Unit.BEI)), new ArrayList(Arrays.asList(Unit.UNKNOWN)), new ArrayList(Arrays.asList(Unit.PERCENT)), new ArrayList(Arrays.asList(Unit.BEI)), new ArrayList(Arrays.asList(Unit.YUAN))};
		boolean[] expected = {false, true, true, true, true, true, true};
		for (int i = 0; i < expected.length; i++) {
			Object[] prams = { unit1[i], unit2[i] };
			boolean actual = (Boolean) UnitTestTools.invokePrivateMethod(className, methodName, prams);
			assertEquals(expected[i], actual);
		}
	}
}
