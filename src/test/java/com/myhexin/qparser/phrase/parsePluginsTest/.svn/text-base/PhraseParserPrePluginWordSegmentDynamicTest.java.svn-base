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
 * 动态分词插件的测试类。
 * 
 * @author huangmin
 *
 */
public class PhraseParserPrePluginWordSegmentDynamicTest extends
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
	 */
	@Test
	public void WordSegmentDynamicTest() throws Exception {
		for (String queryStr : getQueries()) {
			String fqueryStr = StringUtils.lowerCase(queryStr);
			ParserAnnotation pAnnotation = createAnnotation(fqueryStr, false);
			PhraseParserPrePluginAbstract wordSegmentDynamicPlugin = getPrePluginByName(WORD_SEGMENT_DYNAMIC);
			execute(wordSegmentDynamicPlugin, true, pAnnotation);
			ArrayList<ArrayList<SemanticNode>> qlist = pAnnotation.getQlist();
			assertResultListNotNull(qlist);
			// TODO: any other assert should be add here.
		}
	}
}
