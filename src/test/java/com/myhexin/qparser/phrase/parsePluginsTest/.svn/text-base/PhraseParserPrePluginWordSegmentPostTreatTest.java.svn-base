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
 * 分词后处理的插件测试类。
 * 
 * @author huangmin
 *
 */
public class PhraseParserPrePluginWordSegmentPostTreatTest extends
		PhraseParserPrePluginBaseTest {

	@BeforeClass
	public static void setUp() throws Exception {
	}

	@AfterClass
	public static void tearDown() throws Exception {
	}

	@Test
	public void wordSegmentPostTreatTest() throws Exception {
		for (String queryStr : getQueries()) {
			String fqueryStr = StringUtils.lowerCase(queryStr);
			ParserAnnotation pAnnotation = createAnnotation(fqueryStr, false);
			PhraseParserPrePluginAbstract wordSegmentPlugin = getPrePluginByName(WORD_SEGMENT);
			execute(wordSegmentPlugin, true, pAnnotation); // 执行分词
			PhraseParserPrePluginAbstract worSegmentPostTreatPlugin = getPrePluginByName(WORD_SEGMENT_POSTTREAT);
			execute(worSegmentPostTreatPlugin, true, pAnnotation);// 分词后处理
			ArrayList<ArrayList<SemanticNode>> qlist = pAnnotation.getQlist();
			assertResultListNotNull(qlist);
			// TODO: any other assert should be add here.
		}
	}
}
