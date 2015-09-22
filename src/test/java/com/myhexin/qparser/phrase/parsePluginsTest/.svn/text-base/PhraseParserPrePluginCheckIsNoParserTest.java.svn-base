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
 * 检查问句都是解析完善的词。
 * 
 * @author huangmin
 *
 */
public class PhraseParserPrePluginCheckIsNoParserTest extends
		PhraseParserPrePluginBaseTest {

	@BeforeClass
	public static void setUp() throws Exception {
	}

	@AfterClass
	public static void tearDown() throws Exception {
	}

	@Test
	public void checkIsNoParser() throws Exception {
		for (String queryStr : getQueries()) {
			String fqueryStr = StringUtils.lowerCase(queryStr);
			ParserAnnotation pAnnotation = createAnnotation(fqueryStr, false);
			PhraseParserPrePluginAbstract checkIsNoParserPlugin = getPrePluginByName(CHECK_IS_NOPARSER);
			execute(checkIsNoParserPlugin, true, pAnnotation);
			ArrayList<ArrayList<SemanticNode>> qlist = pAnnotation.getQlist();
			assertResultListNotNull(qlist);
			// TODO: any other assert should be add here.
		}
	}
}
