package com.myhexin.qparser.resource.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.myhexin.DB.mybatis.MybatisHelp;
import com.myhexin.DB.mybatis.mode.NodeMerge;
import com.myhexin.qparser.ApplicationContext.ApplicationContextHelper;
import com.myhexin.qparser.ApplicationContext.BeanNameConsts;
import com.myhexin.qparser.date.ParserInterface;
import com.myhexin.qparser.date.parse.ConsistPeriodCalcParser;
import com.myhexin.qparser.date.parse.DateRelativeCalcParser;
import com.myhexin.qparser.date.parse.TradeCalcParser;


public class NodeMergeInfo implements ResourceInterface{
	private static NodeMergeInfo instance = new NodeMergeInfo();
	
	//所有合并配置
	private List<NodeMerge> merges = null;
	private Map<String, ParserInterface> parserMap = new HashMap<String, ParserInterface>();
	
	//非正则的配置,包括A->B的转换
	private Map<String,NodeMerge> textMergeMap = null;
	
	private NodeMergeInfo() {
		parserMap.put("TRADE_DAY_CALC", new TradeCalcParser() );
		parserMap.put("DAY_RELATIVE_CALC", new DateRelativeCalcParser() );
		//parserMap.put("TIME_CALC", new TimeCalcParser());
		parserMap.put("CONSIST_PERIOD_CALC", new ConsistPeriodCalcParser());
	}
	
	public static NodeMergeInfo getInstance() {
		return instance;
	}
	
	
	
	
	public ParserInterface getParserInstance(String pattern) {
		return parserMap.get(pattern);
	}
	
	@Override
	public void reload() {
		MybatisHelp mybatisHelp = ApplicationContextHelper.getBean(BeanNameConsts.MYBATIS_HELP);
		List<NodeMerge> nodeMergeList = mybatisHelp.getNodesParserMapper().selectNodeMergeList();
		if(nodeMergeList!=null) {
			List<NodeMerge> newMergeList = new ArrayList<NodeMerge>();
			Map<String,NodeMerge> temptextMergeMap = new HashMap<String, NodeMerge>();
			for(NodeMerge node : nodeMergeList) {
				if(node.isRegex() && node.build()) {
					newMergeList.add(node);
				}else{
					temptextMergeMap.put(node.getMergePattern(), node);
				}
			}
			
			merges = newMergeList;
			textMergeMap = temptextMergeMap;
		}
		
	}
	
	
	public NodeMerge matchConfig(String text) {
		if(textMergeMap!=null) {
			NodeMerge node = textMergeMap.get(text);
			return node;
		}
		return null;
	}
	
	//TODO 这里值得优化
	public NodeMerge canMerge(String text1, String text2) {
		if(merges==null && textMergeMap==null) return null;
		
		if(textMergeMap!=null) {
			NodeMerge node = textMergeMap.get(text1 + text2);
			if(node!=null) {
				return node;
			}
		}
		
		if(merges!=null) {
			for(NodeMerge node : merges) {
				if(node.match(text1, text2)) {
					return node;
				}
			}
		}
		
		return null;
	}
}
