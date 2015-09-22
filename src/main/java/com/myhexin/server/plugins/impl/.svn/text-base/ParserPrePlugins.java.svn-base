package com.myhexin.server.plugins.impl;

import java.util.ArrayList;

import com.myhexin.qparser.Query;
import com.myhexin.qparser.except.ExceptionUtil;
import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.phrase.ParserAnnotation;
import com.myhexin.qparser.phrase.parsePrePlugins.PhraseParserPrePluginAbstract;
import com.myhexin.qparser.tokenize.Tokenizer;
import com.myhexin.server.plugins.ParserPluginsAbstract;
import com.myhexin.server.vo.impl.ParserParam;
import com.myhexin.server.vo.impl.ParserResult;

/**
 * @author 徐祥
 * @createDataTime 2014-5-9 下午5:56:51
 * @description 解析预处理组合插件实现类
 */
public class ParserPrePlugins extends ParserPluginsAbstract {
	
	private ArrayList<PhraseParserPrePluginAbstract> plugins = null;
	private String stopAfterPlugin = null;
	private String split = "_&_";

	/**
	 * @description 无参构造函数，建议不要删除（Spring中可能会用到）
	 */
	public ParserPrePlugins() {
	}

	/**
	 * @description 解析预处理组合插件处理过程具体实现
	 * @return 返回类型ParserResult，成员result变量中保存ParseResult类型结果
	 */
	@Override
	public ParserResult work(ParserParam param) {
		String queryText = param.getQueryText();
		String queryType = param.getQueryType();

		if (null == queryText || 0 == queryText.length()
				|| null == this.plugins) {
			return null;
		}

		StringBuilder log = param.getPreLog();

		// 没有上次记录，生成空记录
		log = log == null ? new StringBuilder() : log;

		Query query = new Query(queryText, queryType);
		query.getParseResult().standardQueries = new ArrayList<String>();

		/*ArrayList<String> qstrlist = new ArrayList<String>();
		ArrayList<ArrayList<SemanticNode>> qlist = new ArrayList<ArrayList<SemanticNode>>();
		qstrlist.add(query.text);*/

		ParserAnnotation annotation = new ParserAnnotation();
    	annotation.setQueryText(query.text);
    	annotation.setQuery(query);
    	annotation.setStopProcessFlag(true);
		
		try {
			for (PhraseParserPrePluginAbstract plugin : this.plugins) {
				long start = System.currentTimeMillis();
				plugin.init();
				
				plugin.process(annotation);
				
				long end = System.currentTimeMillis();
				log.append(String.format("## %dms %s\n", end - start, plugin.strTitle + "..."));
				log.append(plugin.getLogResult(annotation) );

				if(annotation.isStopProcessFlag()) {
                	annotation.setSegmentedText(null);
                	break;
                }

				if (null != this.stopAfterPlugin && this.stopAfterPlugin.equals(plugin.strTitle))
					break;
			}

			// tokenize 分词结果转化为节点列表
			String segmentedText = annotation.getSegmentedText();
	            
	        ArrayList<ArrayList<SemanticNode>> qlist = null;
	        // tokenize 分词结果转化为节点列表
	        if (segmentedText != null && segmentedText.length()>0) {
				long start = System.currentTimeMillis();
				Tokenizer.tokenize(annotation); //ENV, query, qstrlist.get(0));
            	qlist = annotation.getQlist();
				long end = System.currentTimeMillis();
				log.append(String.format("## %dms %s\n", end - start,
						"## Tokenize..."));
				if (qlist == null || qlist.size() == 0) {
					log.append("tokenize error\n");
				}
				for (int i = 0; qlist != null && i < qlist.size(); i++) {
					log.append(String.format("[match %d]: %s\n", i,
							qlist.get(i)));
				}
				log.append(String.format("%s\n", getSentence(qlist, true)));
			}

			query.getParseResult().qlist = qlist;
		} catch (Exception e) {
			log.append("## Stack Trace...\n");
			log.append(ExceptionUtil.getStackTrace(e));
		}

		// 返回值
		ParserResult parserResult = new ParserResult();
		parserResult.setResult(query.getParseResult());
		parserResult.setLog(log);
		parserResult.setErrmsg(new StringBuilder(query.hasFatalError() ? query.getErrorMsg() : ""));
		
		return parserResult;
	}

	// set和get方法
	public ArrayList<PhraseParserPrePluginAbstract> getPlugins() {
		return plugins;
	}

	public void setPlugins(ArrayList<PhraseParserPrePluginAbstract> plugins) {
		this.plugins = plugins;
	}

	public String getStopAfterPlugin() {
		return stopAfterPlugin;
	}

	public void setStopAfterPlugin(String stopAfterPlugin) {
		this.stopAfterPlugin = stopAfterPlugin;
	}

	public String getSplit() {
		return split;
	}

	public void setSplit(String split) {
		this.split = split;
	}

}
