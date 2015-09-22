package com.myhexin.qparser.phrase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import com.myhexin.qparser.ParseResult;
import com.myhexin.qparser.Query;
import com.myhexin.qparser.QueryIndex;
import com.myhexin.qparser.QueryIndexWithProp;
import com.myhexin.qparser.except.ExceptionUtil;
import com.myhexin.qparser.node.Environment;
import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.phrase.parsePlugins.PhraseParserPluginAbstract;
import com.myhexin.qparser.phrase.parsePrePlugins.PhraseParserPrePluginAbstract;
import com.myhexin.qparser.tokenize.Tokenizer;
//import com.myhexin.qparser.util.MemoryUtil;

public class PhraseParser {
    public static final org.slf4j.Logger logger_ = org.slf4j.LoggerFactory.getLogger(PhraseParser.class.getName());
    private String split_ = "_&_";
    private ArrayList<PhraseParserPrePluginAbstract> pre_plugins_;// = new ArrayList<PhraseParserPrePluginAbstract>();
    private ArrayList<PhraseParserPluginAbstract> plugins_;// = new ArrayList<PhraseParserPluginAbstract>();
    private ArrayList<PhraseParserPluginAbstract> post_plugins_;// = new ArrayList<PhraseParserPluginAbstract>();
    
	/**
	 * @author 徐祥
	 * @createTime 2014-04-30 16:04
	 * @description 设置预处理插件和处理插件
	 */
	public PhraseParser(ArrayList<PhraseParserPrePluginAbstract> pre_plugins,
			ArrayList<PhraseParserPluginAbstract> plugins,
			ArrayList<PhraseParserPluginAbstract> post_plugins) {
		
		pre_plugins_ = pre_plugins;
		plugins_ = plugins;
		post_plugins_ = post_plugins;
	}
	
	public void setSplitWords(String split) {
        split_ = split;
    }
    
    public String getSplitWords() {
        return split_;
    }

    /**
     * 以后停用,都用ParserAnnotation annotation作为参数
     * 
     * @param query
     * @return
     * @deprecated
     */
	public ParseResult parse(Query query) {
		ParserAnnotation annotation = new ParserAnnotation();
    	annotation.setQueryText(query.text);
    	annotation.setQueryType(query.getType());
    	annotation.setQuery(query);
    	return parse(annotation, pre_plugins_, plugins_, post_plugins_, null,null);
    }
    
	/**
     * 以后停用,都用ParserAnnotation annotation作为参数
     * 
     * @param query
     * @return
     * @deprecated
     */
    public ParseResult parse(Query query, String stop_after_plugin) {
    	ParserAnnotation annotation = new ParserAnnotation();
    	annotation.setQueryText(query.text);
    	annotation.setQueryType(query.getType());
    	annotation.setQuery(query);
    	return parse(annotation, pre_plugins_, plugins_, post_plugins_, stop_after_plugin,null);
    }
    
    /**
     * 以后停用,都用ParserAnnotation annotation作为参数
     * 
     * @param query
     * @return
     * @deprecated
     */
    public ParseResult parse(Query query, ArrayList<PhraseParserPrePluginAbstract> pre_plugins, 
    		ArrayList<PhraseParserPluginAbstract> plugins,
    		ArrayList<PhraseParserPluginAbstract> post_plugins) {
    	ParserAnnotation annotation = new ParserAnnotation();
    	annotation.setQueryText(query.text);
    	annotation.setQueryType(query.getType());
    	annotation.setQuery(query);
    	return parse(annotation, pre_plugins, plugins, post_plugins, null,null);
    }
    
    
    public ParseResult parse(ParserAnnotation annotation) {
    	return parse(annotation, pre_plugins_, plugins_, post_plugins_, null,null);
    }
    
    public ParseResult parse(ParserAnnotation annotation, String stop_plugin_name) {
    	return parse(annotation, pre_plugins_, plugins_, post_plugins_, stop_plugin_name,null);
    }
    
    public ParseResult parse(ParserAnnotation annotation, HashMap<String,Object> parameterMap) {
    	return parse(annotation, pre_plugins_, plugins_, post_plugins_, null, parameterMap);
    }
    
    
    public ParseResult parse(ParserAnnotation annotation, 
			ArrayList<PhraseParserPrePluginAbstract> pre_plugins_, 
			ArrayList<PhraseParserPluginAbstract> plugins_, 
			ArrayList<PhraseParserPluginAbstract> post_plugins_,
			String stop_after_plguin,HashMap<String,Object> parameterMap) {
		//以后都用annotation做接口的参数
    	annotation.setStopProcessFlag(false);
		Query query = annotation.getQuery();
		
		//这一步必不可少,每一个query产生一个行的环境
		Environment ENV = (parameterMap==null) ? new Environment() : new Environment(parameterMap);
    	ENV.put("query", query.text,false);
		ENV.put("qType", query.getType(),true);
    	ENV.put("domain", query.getDomain(),false);
    	StringBuilder log_sb = new StringBuilder();;
    	ENV.put("logsb_", log_sb, true);
    	//TODO 把ENV中的变量都移到annotation中去
    	annotation.setEnv(ENV);
    	
    	logger_.info("\tquery:\t" + query.text);

        //TODO 好诡异的写法,以后重写
        query.getParseResult().standardQueries = new ArrayList<String>();
        try {
        	// run pretreat plugins 预处理
            for (PhraseParserPrePluginAbstract plugin : pre_plugins_) {
            	plugin.init();
                plugin.process(annotation);
                if(annotation.isStopProcessFlag()) {
                	annotation.setSegmentedText(null);
                	break;
                }
            }//预处理结束
            
            String segmentedText = annotation.getSegmentedText();
            ArrayList<ArrayList<SemanticNode>> qlist = null;
            // tokenize 分词结果转化为节点列表
            if (segmentedText != null && segmentedText.length()>0) {
            	Tokenizer.tokenize(annotation); //ENV, query, qstrlist.get(0));
            	qlist = annotation.getQlist();
            }//tokenize 分词结果转化为节点列表 结束
            
            // run plugins 解析处理
            for(PhraseParserPluginAbstract plugin : plugins_) {
            	if (qlist==null || qlist.size() == 0)
                	break;

                ArrayList<ArrayList<SemanticNode>> rlist = new ArrayList<ArrayList<SemanticNode>>();
                ArrayList<ArrayList<SemanticNode>> tlist = null;
                plugin.init();
                for(ArrayList<SemanticNode> nodes: qlist) {
                	annotation.setNodes(nodes);
                	tlist = plugin.process(annotation);
                	
                	//tlist = (ArrayList<ArrayList<SemanticNode>>) NewENV.get("resultList");
                	
                	//NewENV.remove("resultList");//不想保留这样的结果列表
                	//ENV.putAll(NewENV,true);
                    
                    if(tlist != null && tlist.size() > 0) {
                        for(ArrayList<SemanticNode> tnodes: tlist) {
                            rlist.add(tnodes);
                        }
                    }
                }
                qlist = rlist;
               
                //如果设置了退出插件,就在这个插件退出
                if ((stop_after_plguin != null && plugin.strTitle.equals(stop_after_plguin)))
                	break;
            }// run plugins 解析处理 结束
            
            // 将评分较高的放在前面
            PhraseUtil.sortByScore(qlist);

            // run post_plugins 解析处理
            for(PhraseParserPluginAbstract plugin : post_plugins_) {
            	if (qlist==null || qlist.size() == 0)
                	break;
                ArrayList<ArrayList<SemanticNode>> rlist = new ArrayList<ArrayList<SemanticNode>>();
                ArrayList<ArrayList<SemanticNode>> tlist = null;
                plugin.init();
                for(ArrayList<SemanticNode> nodes: qlist) {
                	annotation.setNodes(nodes);
                	tlist = plugin.process(annotation);
                	
                	//tlist = (ArrayList<ArrayList<SemanticNode>>) NewENV.get("resultList");
                	
                	//NewENV.remove("resultList");//不想保留这样的结果列表
                	//ENV.putAll(NewENV,true);
                    
                    //tlist = plugin.processProxy(query.text, nodes);
                    if(tlist != null && tlist.size() > 0) {
                        for(ArrayList<SemanticNode> tnodes: tlist) {
                            rlist.add(tnodes);
                        }
                    }
                }
                qlist = rlist;
                if ((stop_after_plguin != null && plugin.strTitle.equals(stop_after_plguin)))
                	break;
            }

			query.getParseResult().qlist = qlist;
			query.getParseResult().wordSegment = new ArrayList<String>();
			query.getParseResult().wordSegment.add(annotation.getSegmentedText());
			query.getParseResult().standardQueries = ParseResult.toStandardQueries(query, qlist, split_);;
			query.getParseResult().standardQueriesScore = ParseResult.getQueryScores(qlist);
			query.getParseResult().standardQueriesIndex = QueryIndex.instance.getQueriesIndexs(qlist);
			query.getParseResult().luaExpression = ParseResult.getLuaExpression(qlist);
			query.getParseResult().standardQueriesIndexWithProp = QueryIndexWithProp.instance.getQueriesIndexs(qlist);
			query.getParseResult().standardQueriesSyntacticSemanticIds = ParseResult.getQueriesSyntacticSemanticIds(qlist);
			// System.out.println(query.getParseResult().standardQueries);
			// System.out.println(query.getParseResult().standardQueriesScore);
			// System.out.println(query.getParseResult().standardQueriesIndex);
			// System.out.println(query.getParseResult().standardQueriesSyntacticSemanticIds);
			// output results
            
			List<String> standardQueries = query.getParseResult().standardQueries;
			for (int i = 0; i < standardQueries.size(); i++) {
                logger_.info("result:\t" + standardQueries.get(i));
            }
		} catch (Exception e) {
			String msg = ExceptionUtil.getStackTrace(e);
			logger_.error(msg);
			
		}
		return query.getParseResult();
    }
    
    
    public ParseResult parseForYunyin(ParserAnnotation annotation) {
    	return parseForYunyin(annotation, pre_plugins_, plugins_, post_plugins_, null,null);
    }

	public ParseResult parseForYunyin(ParserAnnotation annotation, 
			ArrayList<PhraseParserPrePluginAbstract> pre_plugins_, 
			ArrayList<PhraseParserPluginAbstract> plugins_, 
			ArrayList<PhraseParserPluginAbstract> post_plugins_,
			String stop_after_plguin,HashMap<String,Object> parameterMap) {
    	
		//以后都用annotation做接口的参数
		annotation.setStopProcessFlag(false);
		Query query = annotation.getQuery();
		Boolean writeLog = annotation.isWriteLog();
		Set<String> skipList = annotation.getPluginSkipSet();
		if(writeLog==null) writeLog = false;
		
    	
		//这一步必不可少,每一个query产生一个行的环境
		Environment ENV = (parameterMap==null) ? new Environment() : new Environment(parameterMap);
    	ENV.put("qType", query.getType(),true);
    	ENV.put("domain", query.getDomain(),false);
    	
    	//TODO 把ENV中的变量都移到annotation中去
    	annotation.setEnv(ENV);
    	
    	logger_.info("\tquery:\t" + query.text);
    	
    	//TODO 这个也应该改掉
    	StringBuilder log_sb = new StringBuilder();;
    	ENV.put("logsb_", log_sb, true);
        
    	Timing timer = new Timing();
    	
        //TODO 好诡异的写法,以后重写
        query.getParseResult().standardQueries = new ArrayList<String>();
        try {
        	// run pretreat plugins 预处理
            for (PhraseParserPrePluginAbstract plugin : pre_plugins_) {
            	if(skipList!=null && skipList.contains(plugin.strTitle)){
            		continue;
            	}
            	timer.start();
                plugin.init();
                plugin.process(annotation);
                timer.end();
                
                if(writeLog){
                	log_sb.append(String.format("## %dms ## %s\n",timer.mills(), plugin.strTitle+"..."));
                	log_sb.append(String.format("%s\n", plugin.getPluginDesc()));
                	log_sb.append(plugin.getLogResult(annotation));
                }
                
                if(annotation.isStopProcessFlag()) {
                	annotation.setSegmentedText(null);
                	break;
                }
                
                
                if(annotation.isLogTime() &&  timer.isBigtime()) {
                	logger_.info("\t" + timer.mills() + "ms ," + plugin.strTitle);
                }
                //PhraseUtil.debug(plugin.strTitle, annotation);
            }//预处理结束
            
            
            
            String segmentedText = annotation.getSegmentedText();
            ArrayList<ArrayList<SemanticNode>> qlist = null;
            // tokenize 分词结果转化为节点列表
            if (segmentedText != null && segmentedText.length()>0) {
            	timer.start();
            	Tokenizer.tokenize(annotation); //ENV, query, qstrlist.get(0));
            	qlist = annotation.getQlist(); 
            	timer.end();
            	
            	
	            if(writeLog){
		            log_sb.append(String.format("## %dms ## %s\n", timer.mills(), "Tokenize..."));
		            //MemoryUtil.getMemoryInfo( "## Tokenize...");
		            if(qlist == null || qlist.size() == 0) {
		                log_sb.append("tokenize error\n");
		            }
		            for(int i=0 ; qlist != null && i < qlist.size(); i++) {
		                log_sb.append(String.format("[match %d]:<BR>%s\n", i, PhraseUtil.nodesHtml(qlist.get(i))));
		            }
		            log_sb.append(String.format("%s\n", PhraseUtil.getSentence(qlist, true)));
	            }
	            
	            if(annotation.isLogTime() &&  timer.isBigtime()) {
                	logger_.info("\t" + timer.mills() + "ms ,## Tokenize");
                }
	            //PhraseUtil.debug("## Tokenize", annotation);
            }//tokenize 分词结果转化为节点列表 结束
            
            
            
            // run plugins 解析处理
            for(PhraseParserPluginAbstract plugin : plugins_) {
            	if (qlist==null || qlist.size() == 0)
                	break;
            	if(skipList!=null && skipList.contains(plugin.strTitle)){
            		continue;
            	}
            	timer.start();
                ArrayList<ArrayList<SemanticNode>> rlist = new ArrayList<ArrayList<SemanticNode>>();
                ArrayList<ArrayList<SemanticNode>> tlist = null;
                plugin.init();
                for(ArrayList<SemanticNode> nodes: qlist) {
                	annotation.setNodes(nodes);
                	tlist = plugin.process(annotation);
                	
                	//tlist = (ArrayList<ArrayList<SemanticNode>>) NewENV.get("resultList");
                	
                	//NewENV.remove("resultList");//不想保留这样的结果列表
                	//ENV.putAll(NewENV,true);
                    
                    if(tlist != null && tlist.size() > 0) {
                        for(ArrayList<SemanticNode> tnodes: tlist) {
                            rlist.add(tnodes);
                        }
                    }
                }
                qlist = rlist;
                timer.end();
                if(writeLog){
                	
                	if(plugin.strTitle.equals("Calculate_Score")){
                		PhraseUtil.addCalcScoreFormula(log_sb);
                    }
                    
                	
	                log_sb.append(String.format("## %dms ## %s\n", timer.mills(), plugin.strTitle+"..."));
	                log_sb.append(String.format("%s\n", plugin.getPluginDesc()));
	                plugin.logResult(log_sb, qlist);
	                log_sb.append(String.format("%s\n", PhraseUtil.getSentence(qlist, true)));
                }
                if(annotation.isLogTime() &&  timer.isBigtime()) {
                	logger_.info("\t" + timer.mills() + "ms ," + plugin.strTitle);
                }
                
                
                
                //PhraseUtil.printBindingInfo(plugin.strTitle, qlist);
                
                //如果设置了退出插件,就在这个插件退出
                if ((stop_after_plguin != null && plugin.strTitle.equals(stop_after_plguin)))
                	break;
            }// run plugins 解析处理 结束
            
            
            // 将评分较高的放在前面
            PhraseUtil.sortByScore(qlist);
            
            
            if(writeLog){
                log_sb.append(String.format("## %dms %s\n", 0, "开始解析后处理..."));
            }
            
            // run post_plugins 解析处理
            for(PhraseParserPluginAbstract plugin : post_plugins_) {
            	if (qlist==null || qlist.size() == 0)
                	break;
            	if(skipList!=null && skipList.contains(plugin.strTitle)){
            		continue;
            	}
            	timer.start();
                ArrayList<ArrayList<SemanticNode>> rlist = new ArrayList<ArrayList<SemanticNode>>();
                ArrayList<ArrayList<SemanticNode>> tlist = null;
                plugin.init();
                for(ArrayList<SemanticNode> nodes: qlist) {
                	annotation.setNodes(nodes);
                	tlist = plugin.process(annotation);
                	
                	//tlist = (ArrayList<ArrayList<SemanticNode>>) NewENV.get("resultList");
                	
                	//NewENV.remove("resultList");//不想保留这样的结果列表
                	//ENV.putAll(NewENV,true);
                    
                    //tlist = plugin.processProxy(query.text, nodes);
                    if(tlist != null && tlist.size() > 0) {
                        for(ArrayList<SemanticNode> tnodes: tlist) {
                            rlist.add(tnodes);
                        }
                    }
                }
                qlist = rlist;
                timer.end();
                if(writeLog){
	                log_sb.append(String.format("## %dms ## %s\n", timer.mills(), plugin.strTitle+"..."));
	                log_sb.append(String.format("%s\n", plugin.getPluginDesc()+"..."));
	                plugin.logResult(log_sb, qlist);
	                log_sb.append(String.format("%s\n", PhraseUtil.getSentence(qlist, true)));
                }
                if(annotation.isLogTime() && timer.isBigtime()) {
                	logger_.info("\t" + timer.mills() + "ms ," + plugin.strTitle);
                }
                //plugin.print(qlist);
                //MemoryUtil.getMemoryInfo(plugin.strTitle);
                if ((stop_after_plguin != null && plugin.strTitle.equals(stop_after_plguin)))
                	break;
            }
            
			query.getParseResult().qlist = qlist;
			query.getParseResult().wordSegment = new ArrayList<String>();
			query.getParseResult().wordSegment.add(annotation.getSegmentedText());
			query.getParseResult().standardQueries = ParseResult.toStandardQueries(query, qlist, split_);;
			query.getParseResult().standardQueriesScore = ParseResult.getQueryScores(qlist);
			query.getParseResult().standardQueriesIndex = QueryIndex.instance.getQueriesIndexs(qlist);
			query.getParseResult().luaExpression = ParseResult.getLuaExpression(qlist);
			query.getParseResult().standardQueriesIndexWithProp = QueryIndexWithProp.instance.getQueriesIndexs(qlist);
			query.getParseResult().standardQueriesSyntacticSemanticIds = ParseResult.getQueriesSyntacticSemanticIds(qlist);
			// System.out.println(query.getParseResult().standardQueries);
			// System.out.println(query.getParseResult().standardQueriesScore);
			// System.out.println(query.getParseResult().standardQueriesIndex);
			// System.out.println(query.getParseResult().standardQueriesSyntacticSemanticIds);
			// output results
            
			if(writeLog){
				log_sb.append("## Common_Output...\n");
	            List<String> standardQueries = query.getParseResult().standardQueries;
	            for (int i = 0; i < standardQueries.size(); i++) {
	                log_sb.append(standardQueries.get(i)+"\n");
	                logger_.info("result:\t" + standardQueries.get(i));
	            }
			}else{
				List<String> standardQueries = query.getParseResult().standardQueries;
				for (int i = 0; i < standardQueries.size(); i++) {
	                logger_.info("result:\t" + standardQueries.get(i));
	            }
			}
		} catch (Exception e) {
			String msg = ExceptionUtil.getStackTrace(e);
			logger_.error(msg);
			if(writeLog){
				log_sb.append("## Stack Trace...\n");
				log_sb.append(msg);
			}
			
		}
		// logger_.info("\n"+log_sb.toString());
        if(writeLog){
        	query.getParseResult().processLog = log_sb.toString();
        }
		return query.getParseResult();
    }

}
