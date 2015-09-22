package com.myhexin.qparser.phrase.parsePluginsTest;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.myhexin.qparser.Query;
import com.myhexin.qparser.phrase.ParserAnnotation;
import com.myhexin.qparser.phrase.PhraseBaseTest;
import com.myhexin.qparser.phrase.parsePrePlugins.PhraseParserPrePluginAbstract;

/**
 * 预解析插件测试类的父类。
 * 
 * @author huangmin
 *
 */
public class PhraseParserPrePluginBaseTest extends PhraseBaseTest {

	/** ----------以下为测试中用到的预解析插件的名称---------- */
	/** 分词解析插件名称，该插件会调用分词服务进行分词 */
	protected static final String WORD_SEGMENT = "WordSegment";
	/** 分词后解析插件名称 */
	protected static final String WORD_SEGMENT_POSTTREAT = "WordSegmentPostTreat";
	/** 判断问句是否乱码的预解析插件名称 */
	protected static final String CHECK_IS_MESSYCODE_TEXT = "CheckIsMessyCodeText";
	/** 判断问句是否都是可解析的词的插件名称 */
	protected static final String CHECK_IS_NOPARSER = "CheckIsNoParser";
	/** 繁体转简体，全角转半角的插件名称 */
	protected static final String TEXT_NORMALIZE = "TextNormalize";
	/** 动态分词的插件名称 */
	protected static final String WORD_SEGMENT_DYNAMIC = "WordSegmentDynamic";

	/** 问句字符串 */
	private static List<String> queryStrList;

	/**
	 * 为预解析准备数据。
	 * 
	 * @author huangmin
	 *
	 * @return
	 * @throws Exception
	 */
	protected static List<String> getQueries() throws Exception {
		if (queryStrList == null) {
			synchronized (PhraseParserPrePluginBaseTest.class) {
				if (queryStrList == null) {
					queryStrList = getQueryStrList();
				}
			}
		}

		return queryStrList;
	}

	/**
	 * 创建查询对象query。
	 * 
	 * @author huangmin
	 *
	 * @param queryStr
	 * @param stopProcess
	 *            如果为true，则停止处理问句，不再进行后继的插件处理操作，直接返回。
	 * @return
	 */
	protected ParserAnnotation createAnnotation(String queryStr,
			boolean stopProcess) {
		Query query = new Query(queryStr);
		ParserAnnotation pAnnotation = new ParserAnnotation();
		pAnnotation.setQuery(query);
		pAnnotation.setStopProcessFlag(stopProcess);
		pAnnotation.setQueryText(query.text);

		return pAnnotation;
	}

	/**
	 * 预解析插件执行任务。
	 * 
	 * @author huangmin
	 *
	 * @param prePlugin
	 *            需要执行的任务的插件
	 * @param needInit
	 *            是否需要该插件执行初始化操作
	 * @param pAnnotation
	 *            任务
	 */
	protected void execute(PhraseParserPrePluginAbstract prePlugin,
			boolean needInit, ParserAnnotation pAnnotation) {
		if (needInit) {
			prePlugin.init();
		}

		prePlugin.process(pAnnotation); // 执行插件
	}

	/**
	 * 获取问句字符串列表。
	 * 
	 * @author huangmin
	 *
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("resource")
	private static List<String> getQueryStrList() throws Exception {
		ArrayList<String> lists = new ArrayList<String>();
		BufferedReader br = new BufferedReader(new InputStreamReader(
				new FileInputStream("query.txt"), "utf-8"));
		boolean flag = true;
		String s = null;
		while ((s = br.readLine()) != null) {
			s = StringUtils.trim(s);
			if (s.startsWith("#") || s.length() == 0) {
				continue;
			} else if (s.startsWith("break")) {
				break;
			} else if (s.startsWith("/*")) {
				flag = false;
			} else if (s.endsWith("*/")) {
				flag = true;
				continue;
			}
			if (flag) {
				lists.add(s);
			}
		}

		return lists;
	}
}
