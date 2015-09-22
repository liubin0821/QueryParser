package com.myhexin.qparser.phrase;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

import com.myhexin.qparser.Query;
import com.myhexin.qparser.ApplicationContext.ApplicationContextHelper;

import com.myhexin.qparser.csv.ReadCsv;
import com.myhexin.qparser.define.EnumDef.NodeType;
import com.myhexin.qparser.node.BoundaryNode;
import com.myhexin.qparser.node.Environment;
import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.phrase.parsePlugins.PhraseParserPluginBindStrToIndex;
import com.myhexin.qparser.phrase.parsePlugins.PhraseParserPluginDealWithAmbiguity;
import com.myhexin.qparser.phrase.parsePlugins.PhraseParserPluginMatchIndexAndKeywords;
import com.myhexin.qparser.phrase.parsePlugins.PhraseParserPluginMatchSyntacticPatterns;
import com.myhexin.qparser.resource.Resource;

import junit.framework.TestCase;

public class TestPhraseParser extends TestCase {

	public void test1() {
		phraseParser(1);
	}
	
	public void test2() {
		phraseParser(2);
	}
	
	private void phraseParser(int num) {
		String caseId = null;
		try {
			// PhraseParser parser = new PhraseParser("./conf/qparser.conf", "./data");

			// 加载xml文件，初始spring容器
			ApplicationContextHelper.loadApplicationContext();

			// phraseParser配置于qphrase.xml文件
			PhraseParser parser = (PhraseParser) ApplicationContextHelper.getBean("phraseParser");
			
			ReadCsv csv = new ReadCsv("data/matcher/testPhraseParser.csv");
			caseId = csv.getString(num, 0);
			String description = csv.getString(num, 1);
			System.out.println("**********" + caseId + "**********");
			System.out.println("*****description:" + description + "*****");
			String query = csv.getCsvString(num, csv, "query");
			Query q = new Query(query.toLowerCase());
			Method m = parser.getClass().getDeclaredMethod("tokenize",
					Query.class);
			m.setAccessible(true);
			ArrayList<ArrayList<SemanticNode>> qlist = (ArrayList<ArrayList<SemanticNode>>) m
					.invoke(parser, q);
			ArrayList<ArrayList<SemanticNode>> rlist = new ArrayList<ArrayList<SemanticNode>>();
			PhraseParserPluginMatchIndexAndKeywords pmiak = new PhraseParserPluginMatchIndexAndKeywords();
			PhraseParserPluginMatchSyntacticPatterns pmsp = new PhraseParserPluginMatchSyntacticPatterns();
			PhraseParserPluginDealWithAmbiguity pdwa = new PhraseParserPluginDealWithAmbiguity();
			PhraseParserPluginBindStrToIndex pbp2i = new PhraseParserPluginBindStrToIndex();
			HashMap<String, Object> ENV = new HashMap<String, Object>();
			ArrayList<ArrayList<SemanticNode>> resultlist = null;
			if (qlist.size() > 0) {
				ParserAnnotation annotation = new ParserAnnotation();
		    	annotation.setEnv(new Environment());
		    	annotation.setNodes( qlist.get(0));
		    	resultlist = pmiak.process(annotation);
				annotation.setNodes(resultlist.get(0));
				resultlist = pmsp.process(annotation);
				annotation.setNodes( resultlist.get(0));
				resultlist = pdwa.process(annotation);
				annotation.setNodes( resultlist.get(0));
				resultlist = pbp2i.process(annotation);
				ArrayList<SemanticNode> slist = resultlist.get(0);
				for (int i = 0; i < slist.size(); i++) {
					if (slist.get(i).type == NodeType.BOUNDARY) {
						BoundaryNode bn = (BoundaryNode) slist.get(i);
						assertEquals(bn.syntacticPatternId,
								csv.getCsvString(1, csv, "checks"));
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	/**
	 * 根据文件名，生成绝对路径
	 * 
	 * @param fileName
	 * @return
	 */
	private static String getFileName(String fileName) {
		String useridr = System.getProperty("user.dir");
		String name = useridr + "/src/test/com/myhexin/qparser/phrase/"
				+ fileName;
		return name;
	}

}
