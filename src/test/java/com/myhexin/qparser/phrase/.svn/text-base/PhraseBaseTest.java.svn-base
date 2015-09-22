/**
 * 
 */
package com.myhexin.qparser.phrase;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.myhexin.BaseTest;
import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.phrase.parsePlugins.PhraseParserPluginAbstract;
import com.myhexin.qparser.phrase.parsePrePlugins.PhraseParserPrePluginAbstract;

/**
 * 这是新解析测试类的父类，用于获取解析的插件。
 * 
 * @author huangmin
 *
 */
public class PhraseBaseTest extends BaseTest {

	public static final Logger logger = LoggerFactory
			.getLogger(PhraseBaseTest.class.getName());

	/** 错误的类型 */
	private static final String ERROR_TYPE_INPUT = "输入参数错误";
	private static final String ERROR_TYPE_NULL = "容器初始化错误";
	/** 错误的描述 */
	private static final String ERROR_MESSAGE_NULL = "当前实例不存在！";
	private static final String ERROR_MESSAGE_PARAM_EMPTY = "输入参数不能为空！";

	/**
	 * 
	 * 加载插件对象，该对象有包含有
	 * <ul>
	 * <li><code>pre_plugins_</code>，为预解析插件列表</li>
	 * <li><code>plugins_</code>，为解析插件列表</li>
	 * <li><code>post_plugins_</code>，为解析后处理插件列表</li>
	 * <ul>
	 * 该插件对象的定义在unitTest-plguin.xml中
	 * 
	 * @author huangmin
	 */
	@Resource
	private PhraseParser phraseParserUnitTest;

	/**
	 * @return the phraseParserUnitTest
	 */
	public PhraseParser getPhraseParser() {
		return phraseParserUnitTest;
	}

	/**
	 * 根据解析插件的名称获取解析插件实例。
	 * 
	 * @param pluginName
	 *            该插件名称是解析插件的bean的id，注意，后解析插件也可通过此方法获取。
	 * @return 解析插件实例或者后解析插件实例，预解析插件不在此处返回。
	 * @throws Exception
	 * 
	 * @author huangmin
	 */
	public PhraseParserPluginAbstract getPluginByName(String pluginName)
			throws Exception {
		if (!isLegal("getPluginByName", pluginName)) {
			return null;
		}

		return (PhraseParserPluginAbstract) (getBean(pluginName)); // 该名称必须是解析(后解析)插件名称，如果不是，则抛出类型转换错误。
	}

	/**
	 * 根据解析插件的类型获取解析插件实例。
	 * 
	 * @param plguinType
	 *            该插件类型是解析插件或者后解析插件的类型。
	 * @return 解析插件实例或者后解析插件实例，预解析插件不在此处返回。
	 * @throws Exception
	 */
	public <T extends PhraseParserPluginAbstract> T getPluginByType(
			Class<T> plguinType) throws Exception {
		if (!isLegal("getPluginByType", plguinType)) {
			return null;
		}

		return getBean(plguinType); // 该类型必须是解析(后解析)插件类型，如果不是，则抛出类型转换错误。
	}

	/**
	 * 根据预解析插件的名称获取预解析插件实例。
	 * 
	 * @param prePluginName
	 *            预解析bean在spring中的id
	 * @return 预解析插件实例
	 * @throws Exception
	 * 
	 * @author huangmin
	 */
	public PhraseParserPrePluginAbstract getPrePluginByName(String prePluginName)
			throws Exception {
		if (!isLegal("getPrePluginByName", prePluginName)) {
			return null;
		}

		return (PhraseParserPrePluginAbstract) (getBean(prePluginName)); // 该名称必须是预解析插件名称，如果不是，则抛出类型转换错误。
	}

	/**
	 * 根据预解析插件的类型获取预解析插件实例。
	 * 
	 * @param prePlguinType
	 *            预解析bean的类型
	 * @return 预解析插件实例
	 * @throws Exception
	 */
	public <T extends PhraseParserPrePluginAbstract> T getPrePluginByType(
			Class<T> prePlguinType) throws Exception {
		if (!isLegal("getPrePluginByType", prePlguinType)) {
			return null;
		}

		return getBean(prePlguinType);// 该类型必须是预解析插件类型，如果不是，则抛出类型转换错误。
	}

	/**
	 * 在获取插件实例前，检查各个参数是否合法。
	 * 
	 * @param methodName
	 * @param needCheckObject
	 *            需要被检查的参数
	 * @return
	 * @throws Exception
	 */
	public boolean isLegal(String methodName, Object... needCheckObject)
			throws Exception {
		// check null
		for (Object obj : needCheckObject) {
			if (obj == null) {
				logger.error(methodName + "====" + ERROR_TYPE_INPUT + "："
						+ ERROR_MESSAGE_PARAM_EMPTY);
				return false;
			}
		}
		// any other validation you can add here...............
		if (phraseParserUnitTest == null) {
			throw new NullPointerException(methodName + "===="
					+ ERROR_TYPE_NULL + "：" + ERROR_MESSAGE_NULL);
		}

		return true;
	}

	/**
	 * 断言返回的qlist是否为空，该断言是所有插件处理后的优先断言。
	 * 
	 * @author huangmin
	 *
	 * @param qlist
	 */
	protected void assertResultListNotNull(
			ArrayList<ArrayList<SemanticNode>> qlist) {
		assertTrue("qlist不为null", qlist != null);
		assertTrue("qlist不为空", qlist.size() > 0);

		if (qlist != null && qlist.size() > 0) {
			for (ArrayList<SemanticNode> list : qlist) {
				assertTrue("节点列表不为空", list.size() > 0);
				for (SemanticNode sn : list) {
					assertTrue("节点不为空", StringUtils.isNotBlank(sn.getText()));
				}
			}
		}
	}
}
