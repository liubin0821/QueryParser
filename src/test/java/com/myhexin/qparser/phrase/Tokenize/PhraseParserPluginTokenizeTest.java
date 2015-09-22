package com.myhexin.qparser.phrase.Tokenize;

import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.phrase.ParserAnnotation;
import com.myhexin.qparser.phrase.parsePluginsTest.PhraseParserPrePluginBaseTest;
import com.myhexin.qparser.phrase.parsePrePlugins.PhraseParserPrePluginAbstract;
import com.myhexin.qparser.tokenize.Tokenizer;

/**
 * 分词字符串包装成节点的测试类。
 * 
 * @author huangmin
 *
 */
public class PhraseParserPluginTokenizeTest extends
		PhraseParserPrePluginBaseTest {

	@BeforeClass
	public static void init() throws Exception {
	}

	@AfterClass
	public static void tearDown() throws Exception {
	}

	/**
	 * 测试分词字符串包装成节点的插件
	 * 
	 * @throws Exception
	 */
	@Test
	public void TokenizeTest() throws Exception {
		for (String queryStr : getQueries()) {
			String fqueryStr = StringUtils.lowerCase(queryStr);
			ParserAnnotation pAnnotation = createAnnotation(fqueryStr, false);
			PhraseParserPrePluginAbstract wordSegmentPlugin = getPrePluginByName(WORD_SEGMENT);
			execute(wordSegmentPlugin, true, pAnnotation); // 执行分词
			PhraseParserPrePluginAbstract worSegmentPostTreatPlugin = getPrePluginByName(WORD_SEGMENT_POSTTREAT);
			execute(worSegmentPostTreatPlugin, true, pAnnotation);// 分词后处理

			String segmentedText = pAnnotation.getSegmentedText(); // 获取分词字符串
			if (segmentedText != null && segmentedText.length() > 0) {
				Tokenizer.tokenize(pAnnotation); // 包装
				ArrayList<ArrayList<SemanticNode>> qlist = pAnnotation
						.getQlist();
				assertResultListNotNull(qlist);
				// TODO: any other assert should be add here.
			}
		}
	}
}
