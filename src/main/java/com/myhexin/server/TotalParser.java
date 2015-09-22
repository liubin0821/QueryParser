package com.myhexin.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gson.Gson;
import com.myhexin.qparser.Param;
import com.myhexin.qparser.Query;
import com.myhexin.qparser.ApplicationContext.ApplicationContextHelper;
import com.myhexin.qparser.chunk.ChunkQueryResult;
import com.myhexin.qparser.chunk.ClassifyChunkQuery;
import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.phrase.IndexMultPossibility;
import com.myhexin.qparser.phrase.OutputResult;
import com.myhexin.qparser.phrase.parsePlugins.PhraseParserPluginAbstract;
import com.myhexin.qparser.tokenize.NoParserMatch;

public class TotalParser {
	public static org.slf4j.Logger logger_ = org.slf4j.LoggerFactory.getLogger(TotalParser.class.getName());
	private static ParserPlugins parserPlugins;
	
	static{
		// parserPluginsChunk配置于qparser_plugins_chunk.xml文件
		ApplicationContextHelper.loadApplicationContext();
		parserPlugins = (ParserPlugins) ApplicationContextHelper.getBean("parserPluginsChunk");
	}
	
	public static ChunkQueryResult parserQuery(String queryStr, String splitRltStr, String qType, boolean debug) {
		//long start = System.currentTimeMillis();
		// 首先使用原句构建Query
		Query queryInst = new Query(queryStr);				
		
		ChunkQueryResult chunkQueryResult = getChunkRet(queryStr, splitRltStr);
		double prob = 1.0;
    	for (Map<?,?> orig : chunkQueryResult.getChunk()) {
    		if ((Double) orig.get("prob") >= Param.CRF_MIN_PROB_PARSER) {
    			prob = (Double) orig.get("prob");
    			// 如果prob大于一定分数，目前为0.8，则采用chunk划分结果构建Query
    			queryInst = new Query(((String)orig.get("value")));
    			break;
    		}
    	}
    	
    	if(debug) {
    		logger_.info("query : " + queryStr);
    		logger_.info("ChunkQueryResult : " + queryInst.text + "," + prob);
    	}
    	
    	queryInst.setType(qType);
    	
        Parser.parser.parse(queryInst,parserPlugins.pre_plugins_, parserPlugins.plugins_, parserPlugins.post_plugins_);
        List<ArrayList<SemanticNode>> qlist = queryInst.getParseResult().qlist;
        boolean isWellParsed = false;
        ArrayList<Map<?,?>> o = new ArrayList<Map<?,?>>();
        int score = 0;
        if (qlist != null) {
        	
        	if(debug) {
        		logger_.info("Param.NEW_PARSER_USED_MIN_SCORE : " + Param.NEW_PARSER_USED_MIN_SCORE);
        	}
        	
        	int idx=1;
	        for (ArrayList<SemanticNode> nodes : qlist) {
	        	//System.out.println(nodes.get(0).score);
	        	
	        	if(debug) {
	        		logger_.info("["+idx+"]qparser result score : " + nodes.get(0).score);
	        	}
	        	
	        	if (nodes.get(0).score >= Param.NEW_PARSER_USED_MIN_SCORE) {
	        		// 屏蔽当前解析不完善的词
	        		String standardStatement = PhraseParserPluginAbstract.getFromListEnv(nodes, "standardStatement",String.class, false);
	        		if(debug) {
		        		logger_.info("["+idx+"]standardStatement : " + standardStatement);
		        	}
	        		
	        		if (NoParserMatch.noParserWellTrieTree.mpMatchingOne(standardStatement))
	        		{
	        			if(debug) {
			        		logger_.info("["+idx+"]In  noParserWellTrieTree");
			        	}
	        			break;
	        		}
	        		OutputResult multResult = nodes.get(0).getMultResult();	        		
	        		if(multResult ==null){
	        			isWellParsed = false;
	        			break;
	        		} 
	        		
	        		isWellParsed = true;
	        		score = nodes.get(0).score;
	        		Map<String, Object> chunk = new HashMap<String, Object>();	        		  			
	        			
	        		String ret = multResult.getFirstOutput();
	        		if(debug) {
		        		logger_.info("["+idx+"]multResult.getFirstOutput = " + ret);
		        	}
	        		
	        		String origChunk = nodes.get(0).getOrigChunk();
	        		if(multResult.syntacticOutputs.size()==1){
	        			origChunk = queryInst.text;	
	        		}
    	    		if (ret.contains("涨跌停为是") || ret.contains("涨跌停为否"))
    	    			ret = ret.replace("涨跌停为是", "涨停").replace("涨跌停为否", "跌停");
	        	    chunk.put("value", queryInst.text); // 用于新系统解析的问句：原句或chunk划分结果
	        	    chunk.put("ret", ret); // 新系统解析结果
	        	    chunk.put("prob", prob); // 1.0表示采用原句
	        	    chunk.put("score", score); // 新系统解析的分数
	        	    chunk.put("orig_chunk", origChunk);//给老系统提供原句的chunk划分结果
	        	    o.add(chunk);
	        	    ArrayList<HashMap<String, Object>> interaction = new ArrayList<HashMap<String,Object>>();
	        	    for (String result : multResult.getAllResult()) {
	        	    	HashMap<String, Object> tempInteraction= new HashMap<String, Object>();
	        	    	tempInteraction.put("value", queryInst.text);
	        	    	tempInteraction.put("ret", result);
	        	    	tempInteraction.put("prob", prob);
	        	    	tempInteraction.put("score", score);
	        	    	interaction.add(tempInteraction);
					}	
	        	    if(score >= Param.INDEX_INTERACT_MIN_SCORE &&  score <= Param.INDEX_INTERACT_MAX_SCORE){
	        	    	chunk.put("interaction", interaction);
		        	    chunk.put("IndexMultPossibilitys",  getIndexPos(nodes.get(0).getIndexMultPossibility() ,ret));
	        	    }
	        	    idx++;
	        	}
	        }
        }
        // 日志3：根据isWellParsed判断是走我们的解析，还是走线上的解析
        //liuxiaofeng 2015/6/9 无关日志不写
        /*if (isWellParsed == true) {
        	logger_.info("Well parsed: score=" + score + "\tprob=" + prob + "\tquery=" + queryStr);
        } else {
        	logger_.info("None parsed: score=" + score + "\tprob=" + prob + "\tquery=" + queryStr);
        }*/
        
        if (isWellParsed == false) {
        	chunkQueryResult = getChunkRet(queryStr, splitRltStr);
        } else {
        	chunkQueryResult = new ChunkQueryResult(queryStr, o);
        }
        //long end = System.currentTimeMillis();
        //System.out.println("parser: " + (end-start));
		return chunkQueryResult;
	}

	/**
     * @author: 	    吴永行 
	 * @return 
     * @dateTime:	  2014-4-24 下午2:44:06
     * 
     */
    private static List<IndexMultPossibility> getIndexPos(
    		List<IndexMultPossibility> indexMultPossibility,
	        String ret) {
		if (indexMultPossibility == null)
			return null;
		
		ArrayList<IndexMultPossibility> imps = new ArrayList<IndexMultPossibility>();
		for (IndexMultPossibility imp : indexMultPossibility) {
			String originalText = imp.getDefaultResult();
			int startPos = -1;
			if (originalText != null)
				startPos = ret.indexOf(originalText);			
			if (startPos != -1) {
				imp.startPos=startPos;
				imp.endPos = startPos+originalText.length();
			}
			
			if (startPos!=-1) {
				imps.add(imp);
			}
			
		}			
		return imps;

	}

	public static ChunkQueryResult getChunkRet(String queryStr, String splitRltStr) {
		ChunkQueryResult chunkQueryResult = null;
		if (splitRltStr == null || splitRltStr.length() == 0) {
			long start = System.currentTimeMillis();
			ClassifyChunkQuery chunkFacade_=new ClassifyChunkQuery();
	        List<Entry<String, Double>> rlt = chunkFacade_.createChunkKVRlt(queryStr, Query.Type.ALL, false);
	        long end = System.currentTimeMillis();
            logger_.info("query: " + queryStr + "\tchunk: " + (end-start) + "ms");
	        ArrayList<Map<?,?>> o = new ArrayList<Map<?,?>>();
	        for (Entry<String, Double> rltEntry : rlt) {
	        	Map<String, Object> orig = new HashMap<String, Object>();
	        	orig.put("value", rltEntry.getKey());
        		orig.put("prob", rltEntry.getValue());
        		o.add(orig);
	        }
        	chunkQueryResult = new ChunkQueryResult(queryStr);
        	chunkQueryResult.setChunk(o);
		} else {
			Map<?,?> jmo = new Gson().fromJson(splitRltStr, Map.class);
			chunkQueryResult = new ChunkQueryResult(queryStr);
			chunkQueryResult.setChunk((ArrayList<Map<?, ?>>)jmo.get("chunk"));
		}
        
        return chunkQueryResult;
	}
    
    public static void main(String[] args) {
    	/*System.out.println(parserQuery("总市值>10亿元，总市值<15亿元，总市值>10亿元，总市值<15亿元，总市值>10亿元，总市值<15亿元，总市值>10亿元，总市值<15亿元",null,"stock"));
    	System.out.println(parserQuery("董事长属马", "{\"chunk\":[{\"value\":\"董事长属马\", \"prob\":0.979357},{\"value\":\"董事长_&_属马\", \"prob\":0.019286}]}",null));
    	System.out.println(parserQuery("董事长性别男", null,null));
    	System.out.println(parserQuery("董事长未婚", null,null));
    	System.out.println(parserQuery("董事长清华大学", null,null));
    	System.out.println(parserQuery("董事长2013年底任职", null,null));
    	System.out.println(parserQuery("董事长姓王", null,null));
    	System.out.println(parserQuery("董事长本科毕业", null,null));
    	System.out.println(parserQuery("三条均线重合", null,null));
    	System.out.println(parserQuery("营业收入同比增长>0,_&_营业收入环比增长>0,_&_净利润同比增长>0,_&_营业收入同比增长>0,_&_营业收入环比增长>0,_&_净利润同比增长>0", null,null));
    	System.out.println(parserQuery("营业收入同比增长>0,营业收入环比增长>0,净利润同比增长>0,营业收入同比增长>0,营业收入环比增长>0,净利润同比增长>0", null,null));
    	System.out.println(parserQuery("涨停", null,null));
    	System.out.println(parserQuery("昨日涨停", null,null));*/
    	//System.out.println(parserQuery("电子零部件制造总股本排序", null,"stock"));
    	//System.out.println(parserQuery("002021市盈率", null,"stock"));
    	System.out.println(parserQuery("2013年电子信息涨跌排行", null,"stock", true));
    }
}
