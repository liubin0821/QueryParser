package com.myhexin.server.processor.impl;

import java.util.List;
import com.myhexin.qparser.ParseResult;
import com.myhexin.qparser.except.ExceptionUtil;
import com.myhexin.qparser.node.Environment;
import com.myhexin.server.plugins.ParserPluginsAbstract;
import com.myhexin.server.processor.ParserProcessorAbstract;
import com.myhexin.server.vo.impl.ParserParam;
import com.myhexin.server.vo.impl.ParserResult;

/**
 * @author 徐祥
 * @createDataTime 2014-5-14 上午10:57:19
 * @description 解析服务插件处理实现类
 */
public class ParserProcessor extends ParserProcessorAbstract {

	private List<ParserPluginsAbstract> plugins;
	private String split = "_&_";

	/**
	 * @description 无参构造函数，建议不要删除（Spring中可能会用到）
	 */
	public ParserProcessor() {
	}

	/**
	 * @description 解析处理过程具体实现
	 * @return 返回类型ParserResult，成员result变量中保存ParseResult类型结果
	 */
	@Override
	public ParserResult process(ParserParam param) {
		String queryText = param.getQueryText();
		String queryType = param.getQueryType();
		Environment env = new Environment();
		
		ParserResult result = new ParserResult();
		StringBuilder log = new StringBuilder();
		
		// 设置环境
		param.setEnv(env);

		try {
			ParserResult pluginRet = null;
			for (ParserPluginsAbstract plugin : plugins) {
				pluginRet = plugin.work(param);
				
				param.setPreResult(pluginRet.getResult());
				param.setPreLog(pluginRet.getLog());
				

				result.setResult(pluginRet.getResult());
				result.setLog(pluginRet.getLog());
				result.setErrmsg(pluginRet.getErrmsg());
			}
			
			log.append("## Common Output...\n");

			List<String> standardQueries = ((ParseResult) result.getResult()).standardQueries;
			for (int i = 0; i < standardQueries.size(); i++) {
				log.append(standardQueries.get(i) + "\n");
			}
		} catch (Exception e) {
			log.append("## Stack Trace...\n");
			log.append(ExceptionUtil.getStackTrace(e));
		}

		result.setLog(log);	
		((ParseResult) result.getResult()).processLog = result.getLog().toString();	

		return result;
	}
	
	// set和get方法
	public List<ParserPluginsAbstract> getPlugins() {
		return plugins;
	}

	public void setPlugins(List<ParserPluginsAbstract> plugins) {
		this.plugins = plugins;
	}

	public String getSplit() {
		return split;
	}

	public void setSplit(String split) {
		this.split = split;
	}
}
