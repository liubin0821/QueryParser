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
 * 问句繁体转简体，全角转半角的插件测试类。该测试类应该测试4种场景：
 * <p>
 * 1.繁体转简体<br>
 * 2.全角转半角,<br>
 * 3.繁体转简体，同时全角转半角<br>
 * 4. 都不需要转换
 * 
 * @author huangmin
 *
 */
public class PhraseParserPrePluginTextNormalizeTest extends
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
	public void textNormalizeTest() throws Exception {
		for (String queryStr : getQueries()) {
			String fqueryStr = StringUtils.lowerCase(queryStr);
			ParserAnnotation pAnnotation = createAnnotation(fqueryStr, false);
			PhraseParserPrePluginAbstract textNormalizePlugin = getPrePluginByName(TEXT_NORMALIZE);
			execute(textNormalizePlugin, true, pAnnotation);
			ArrayList<ArrayList<SemanticNode>> qlist = pAnnotation.getQlist();
			assertResultListNotNull(qlist);
			// TODO: any other assert should be add here.
		}
	}
}
