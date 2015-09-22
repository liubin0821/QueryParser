package com.myhexin.server.plugins.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import com.myhexin.qparser.ParseResult;
import com.myhexin.qparser.Query;
import com.myhexin.qparser.QueryIndex;
import com.myhexin.qparser.except.ExceptionUtil;
import com.myhexin.qparser.node.Environment;
import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.phrase.ParserAnnotation;
import com.myhexin.qparser.phrase.parsePlugins.PhraseParserPluginAbstract;
import com.myhexin.server.plugins.ParserPluginsAbstract;
import com.myhexin.server.vo.impl.ParserParam;
import com.myhexin.server.vo.impl.ParserResult;

/**
 * @author 徐祥
 * @createDataTime 2014-5-9 下午5:56:51
 * @description 解析处理组合插件实现类
 */
public class ParserHandlePlugins extends ParserPluginsAbstract{
	
	private ArrayList<PhraseParserPluginAbstract> plugins = null;
	private String stopAfterPlugin = null;
	private String split = "_&_";
	
	/**
	 * @description 无参构造函数，建议不要删除（Spring中可能会用到）
	 */
	public ParserHandlePlugins() {
	}

	/**
	 * @description 解析处理组合插件处理过程具体实现
	 * @return 预处理结果返回，类型ArrayList<ArrayList<SemanticNode>>
	 */
	public ParserResult work(ParserParam param) {
		String queryText = param.getQueryText();
		String queryType = param.getQueryType();
		Environment env = param.getPreEnv();


		if (null == queryText || 0 == queryText.length() || null == this.plugins) {
			return null;
		}
		
		Query query = new Query(queryText, queryType);
		
		StringBuilder log = param.getPreLog();
		// 没有上次记录，生成空记录
		log = log == null ? new StringBuilder() : log;
		ParserAnnotation annotation = new ParserAnnotation();
    	annotation.setQueryText(query.text);
    	annotation.setQuery(query);
    	annotation.setEnv(env);
		try {
			ArrayList<ArrayList<SemanticNode>> qlist = ((ParseResult) param.getPreResult()).qlist;
			
			for (PhraseParserPluginAbstract plugin : this.plugins) {
				if (qlist == null || qlist.size() == 0)
					break;

				long start = System.currentTimeMillis();
				ArrayList<ArrayList<SemanticNode>> rlist = new ArrayList<ArrayList<SemanticNode>>();
				ArrayList<ArrayList<SemanticNode>> tlist = null;
				plugin.init();
				for (ArrayList<SemanticNode> nodes : qlist) {
					annotation.setNodes(nodes);
					tlist = plugin.process(annotation);
					if (tlist != null && tlist.size() > 0) {
						for (ArrayList<SemanticNode> tnodes : tlist) {
							rlist.add(tnodes);
						}
					}
				}
				qlist = rlist;
				long end = System.currentTimeMillis();
				log.append(String.format("## %dms %s\n", end - start,
						plugin.strTitle + "..."));
				plugin.logResult(log, qlist);
				log.append(String.format("%s\n", getSentence(qlist, true)));

				if (null != this.stopAfterPlugin
						&& this.stopAfterPlugin.equals(plugin.strTitle))
					break;
			}

			// 将评分较高的放在前面
			if (qlist != null) {
				Collections.sort(qlist, new Comparator<ArrayList<SemanticNode>>() {
					@Override
					public int compare(ArrayList<SemanticNode> o1, ArrayList<SemanticNode> o2) {
						//ArrayList<SemanticNode> rlist1 = (ArrayList<SemanticNode>) o1;
						//ArrayList<SemanticNode> rlist2 = (ArrayList<SemanticNode>) o2;
						return o1.get(0).score - o2.get(0).score;
					}
				});
			}

			query.getParseResult().qlist = qlist;
			query.getParseResult().standardQueries = ParseResult
					.toStandardQueries(query, qlist, this.split);
			query.getParseResult().standardQueriesScore = ParseResult
					.getQueryScores(qlist);
			query.getParseResult().standardQueriesIndex = QueryIndex.instance
					.getQueriesIndexs(qlist);
			query.getParseResult().standardQueriesSyntacticSemanticIds = ParseResult
					.getQueriesSyntacticSemanticIds(qlist);
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
	public ArrayList<PhraseParserPluginAbstract> getPlugins() {
		return plugins;
	}

	public void setPlugins(ArrayList<PhraseParserPluginAbstract> plugins) {
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
