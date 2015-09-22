package com.myhexin.qparser.phrase.parsePlugins;

import com.myhexin.qparser.phrase.ParserAnnotation;
import com.myhexin.qparser.phrase.PhraseBaseTest;

/**
 * 解析插件测试类的父类。
 * 
 * @author huangmin
 *
 */
public class PhraseParserPluginBaseTest extends PhraseBaseTest {

	/** ----------以下为测试中用到的解析插件的名称---------- */
	/** 添加字符串的指标插件名称 */
	protected static final String ADD_INDEX_OF_STRINSTANCE = "addIndexOfStrInstance";

	/**
	 * 解析插件执行任务。
	 * 
	 * @author huangmin
	 *
	 * @param plugin
	 *            需要执行的任务的插件
	 * @param needInit
	 *            是否需要该插件执行初始化操作
	 * @param pAnnotation
	 *            任务
	 */
	protected void execute(PhraseParserPluginAbstract plugin, boolean needInit,
			ParserAnnotation pAnnotation) {
		if (needInit) {
			plugin.init();
		}

		plugin.process(pAnnotation); // 执行插件
	}
}
