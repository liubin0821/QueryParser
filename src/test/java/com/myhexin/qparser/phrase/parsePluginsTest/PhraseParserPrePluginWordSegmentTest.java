package com.myhexin.qparser.phrase.parsePluginsTest;

import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.phrase.ParserAnnotation;
import com.myhexin.qparser.phrase.parsePrePlugins.PhraseParserPrePluginAbstract;

/**
 * 分词插件的测试类。
 * 
 * @author huangmin
 *
 */
public class PhraseParserPrePluginWordSegmentTest extends
		PhraseParserPrePluginBaseTest {

	@BeforeClass
	public static void setUp() throws Exception {
	}

	@AfterClass
	public static void tearDown() throws Exception {
	}

	/**
	 * 
	 * @author huangmin
	 *
	 * @throws Exception
	 */
	@Test
	public void wordSegmentTest() throws Exception {
		for (String queryStr : getQueries()) {
			String fqueryStr = StringUtils.lowerCase(queryStr);
			ParserAnnotation pAnnotation = createAnnotation(fqueryStr, false);
			PhraseParserPrePluginAbstract wordSegmentPlugin = getPrePluginByName(WORD_SEGMENT);
			execute(wordSegmentPlugin, true, pAnnotation); // 执行分词
			ArrayList<ArrayList<SemanticNode>> qlist = pAnnotation.getQlist();
			assertResultListNotNull(qlist);
			// TODO: any other assert should be add here.
		}
	}
}
