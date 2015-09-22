package com.myhexin.qparser.phrase.parsePluginsTest;

import java.util.ArrayList;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.phrase.ParserAnnotation;
import com.myhexin.qparser.phrase.parsePrePlugins.PhraseParserPrePluginAbstract;

/**
 * 检查问句中乱码比率的测试类。
 * 
 * @author huangmin
 *
 */
public class PhraseParserPrePluginCheckIsMessyCodeTextTest extends
		PhraseParserPrePluginBaseTest {

	@BeforeClass
	public static void setUp() throws Exception {
	}

	@AfterClass
	public static void tearDown() throws Exception {
	}

	/**
	 * 测试问句是:pe>10 的乱码情况。
	 * 
	 * @author huangmin
	 *
	 * @throws Exception
	 */
	@Test
	public void checkIsMessyCodeTextForPeGreatThan10Test() throws Exception {
		String queryStr = "pe>10";
		for (int i = 0; i < getQueries().size(); i++) {
			ParserAnnotation pAnnotation = createAnnotation(queryStr, false);
			PhraseParserPrePluginAbstract checkIsMessyCodeTextPlugin = getPrePluginByName(CHECK_IS_MESSYCODE_TEXT);
			execute(checkIsMessyCodeTextPlugin, true, pAnnotation);
			ArrayList<ArrayList<SemanticNode>> qlist = pAnnotation.getQlist();
			assertResultListNotNull(qlist);
			// TODO: any other assert should be add here.
		}
	}
}
