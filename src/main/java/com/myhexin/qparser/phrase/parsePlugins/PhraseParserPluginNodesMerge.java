package com.myhexin.qparser.phrase.parsePlugins;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import com.myhexin.DB.mybatis.mode.NodeMerge;
import com.myhexin.qparser.date.DatePatterns;
import com.myhexin.qparser.date.ParserInterface;
import com.myhexin.qparser.define.EnumDef.NodeType;
import com.myhexin.qparser.node.ConsistPeriodNode;
import com.myhexin.qparser.node.DateNode;
import com.myhexin.qparser.node.FocusNode;
import com.myhexin.qparser.node.NumNode;
import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.node.TimeNode;
import com.myhexin.qparser.node.UnknownNode;
import com.myhexin.qparser.number.NumUtil;
import com.myhexin.qparser.onto.ClassNodeFacade;
import com.myhexin.qparser.onto.PropNodeFacade;
import com.myhexin.qparser.phrase.ParserAnnotation;
import com.myhexin.qparser.resource.model.NodeMergeInfo;

public class PhraseParserPluginNodesMerge extends PhraseParserPluginAbstract{
	public static org.slf4j.Logger logger_ = org.slf4j.LoggerFactory.getLogger(PhraseParserPluginNumParser.class.getName());
	
	

	public PhraseParserPluginNodesMerge() {
        super("Nodes_Merge");
    }
    
	public ArrayList<ArrayList<SemanticNode>> process(ParserAnnotation annotation) {
		
		//Environment ENV = annotation.get(ParserKeys.EnvironmentKey.class);
		ArrayList<SemanticNode> nodes = annotation.getNodes();
		NumUtil.convertChineseToArabic(nodes);
		return nodesMerger(nodes, annotation.getBacktestTime());
    }
	
	
	private NodeMergeInfo instance = NodeMergeInfo.getInstance();
	
	/**
	 * 合并两个相邻的节点，合并的规则在数据表中配置。
	 * 
	 * @param nodes
	 * @return
	 */
	private ArrayList<SemanticNode> merge(ArrayList<SemanticNode> nodes) {
		 for( int i=0; i<nodes.size(); i++) {
			 SemanticNode previousNode = nodes.get(i);
			 if(previousNode.getType()==NodeType.ENV) {
				 continue;
			 }
			 SemanticNode nextNode = null; 
			 if(i+1 < nodes.size()) {
				 nextNode = nodes.get(i+1);
			 }

			 //比较前后节点,看能不能合并
			 if(previousNode!=null && nextNode!=null) {
				 //临时处理方案，解决形如 5月17 的问句无法解析成时间的问题, added by huangmin
				 String previousNodeText = previousNode.getText();
				 String nextNodeText = nextNode.getText();	
				 NodeMerge nodeMerge = instance.canMerge( previousNodeText, nextNodeText );

				 if(nodeMerge!=null) {
					 String mergedText = previousNodeText + nextNodeText;
					 //String convertPattern = nodeMerge.getConvertPattern();
					 String nodeType = nodeMerge.getNode_type();
					 String convertText = nodeMerge.getChange_to_text();
					 if( !nodeMerge.isRegex() && StringUtils.isNotBlank( convertText ) ) {
						 mergedText = convertText;
					 }
					 SemanticNode node = getNodeByType( nodeType, mergedText );
					 if(node!=null) {
						 node.setNodeMergeInfo(nodeMerge);
						 nodes.set(i, node);
						 nodes.remove(i+1);
						 i--;
					 }
				 }
			 }
		 }
		 return nodes;
	}
	
	/**
	 * 根据不同的node类型获取实例。
	 * 
	 * @author huangmin
	 * 
	 * @param nodeType
	 * @param mergedText
	 * @return
	 */
	private SemanticNode getNodeByType(String nodeType, String mergedText) {
		SemanticNode node = null;
		switch (nodeType) {
		case "Date":
			node = new DateNode(mergedText);
			break;
		case "Num":
			node = new NumNode(mergedText);
			break;
		case "Time":
			node = new TimeNode(mergedText);
			break;
		case "ConsistPeriod":
			node = new ConsistPeriodNode(mergedText);
			break;
		default:
			node = new UnknownNode(mergedText);
		}

		return node;
	}	
	
	/* 
	 * 做A->B转换
	 * 
	 * 
	 * @param nodes
	 * @return
	 */
	private ArrayList<SemanticNode> processChange(ArrayList<SemanticNode> nodes) {
		 for(int i=0;i<nodes.size();i++) {
			 SemanticNode node = nodes.get(i);
			 if(node.getType()==NodeType.ENV) continue;
			 
			 NodeMerge nodeMerge = instance.matchConfig(node.getText());
			 if(nodeMerge!=null && nodeMerge.isRegex()==false && nodeMerge.getChange_to_text()!=null) {
				 String node_type = nodeMerge.getNode_type();
				 if(node_type.equals("Date")) {
					 node = new DateNode(nodeMerge.getChange_to_text());
					 nodes.set(i, node);
				 }else if(node_type.equals("Num")) {
					 node = new NumNode(nodeMerge.getChange_to_text());
					 nodes.set(i, node);
				 }else if(node_type.equals("Time")) {
					 node = new TimeNode(nodeMerge.getChange_to_text());
					 nodes.set(i, node);
				 }else{
					 node = new UnknownNode(nodeMerge.getChange_to_text());
					 nodes.set(i, node);
				 }
			 }
		 }
		 return nodes;
	}

	
	private void parseNode(ArrayList<SemanticNode> nodes,Calendar backtestTime) {
		for(SemanticNode node : nodes) {
			if(node!=null && node.getNodeMerge()!=null) {
				ParserInterface parser = instance.getParserInstance(node.getNodeMerge().getConvertPattern());
				if(parser!=null) {
					parser.parse(node, backtestTime);
				}
			}
		}
	}
	
    private ArrayList<ArrayList<SemanticNode>> nodesMerger(ArrayList<SemanticNode> nodes, Calendar backtestTime) {
        ArrayList<ArrayList<SemanticNode>> tlist = new ArrayList<ArrayList<SemanticNode>>(1);
        
        //TODO 这里值得优化,merge了3次
        ArrayList<SemanticNode> newNodes = merge(nodes);
        newNodes = merge(newNodes);
        newNodes = processChange(newNodes);
        parseNode(newNodes, backtestTime);
        
        /* 对于多重均线的情况进行处理，比如 5日10日20日线多头排列 */
		parseNodeForMultiNDay(newNodes);
        /* 对于多重年份的情况进行处理，比如 2011,2012,2013净利润 */
		parseNodeForMultiYear(newNodes);
        /*nodes = merger.merge(nodes);
        if (nodes == null || nodes.size() == 0)
        	return null;*/
        tlist.add(newNodes);
        return tlist;
    }
    
    /**
     * 对多个的紧按着的年份进行解析，以便支持多个年份的解析，比如问句：2011,2012,2013净利润，年份之间支持多种分隔符
     * 应该解析为：2011年的归属于母公司所有者的净利润_&_2012年的归属于母公司所有者的净利润_&_2013年的归属于母公司所有者的净利润
     * 
     * @author huangmin
     *
     * @param newNodes
     * @throws Exception
     */
	private void parseNodeForMultiYear(ArrayList<SemanticNode> newNodes) {
		Map<String, Integer> nodeTextMap = new HashMap<String, Integer>();
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < newNodes.size(); i++) {
			String text = newNodes.get(i).getText();
			if(nodeTextMap.get(text) == null) {
				nodeTextMap.put(text, i);
			}
			sb.append(text);
		}
		Matcher matcher = DatePatterns.MulTI_YEAR.matcher(sb.toString()); //看看是否有多重年份存在
		while (matcher.find()) { //二维多重年份
			String yearStr = matcher.group(); //形如2011,2012,2013，或者2011年,2012年,2013
			if(StringUtils.isNotEmpty(yearStr) && !DatePatterns.ONE_YEAR.matcher(yearStr).matches()) {
				String split = StringUtils.EMPTY;
				if(yearStr.contains(",")) {
					split = ",";
				}else if(yearStr.contains(" ")) {
					split = " ";
				}else if(yearStr.contains("，")) {
					split = "，";
				}else if(yearStr.contains("、")) {
					split = "、";
				}else if(yearStr.contains(".")) {
					split = ".";
				}
				if(StringUtils.isEmpty(split)) {
					continue;
				}
				List<String> yearList = new ArrayList<String>();
				String[] yearArr = yearStr.split(split);
				for(String dYear : yearArr) {
					Matcher dmatcher = DatePatterns.ONE_YEAR.matcher(dYear);
					if(dmatcher.find()) {
						String year = dmatcher.group();
						yearList.add(year);
					}
				}
				if(yearList.size() > 1) {
					int searchStartPos = getTheStartPosForCut(yearList, nodeTextMap, "年");
					if(searchStartPos == -1) {
						continue;
					}
					int nodesSize = newNodes.size();
					if(searchStartPos < nodesSize) {
						int searchEndPos = -1;
						boolean yearFound = false;
						for (int i = searchStartPos; i < nodesSize; i++ ) { //看看后面有没有报告期属性的指标
							SemanticNode snode = newNodes.get(i);
							if(isSeparator(snode)) { //碰到分隔符，则不用再找了
								break;
							}
							searchEndPos = i;
							if(!yearFound) {
								yearFound = findThePropWithIndex(snode,"报告期" );
							}
						}
						
						if(yearFound) { //假如找到报告期属性，则拷贝这些指标节点
							List<SemanticNode> fetchNodeList = cutTheFoundList(newNodes, searchStartPos, searchEndPos); //截取要插入的节点列表
							removeSeparatorNode(yearList, nodeTextMap, newNodes, DatePatterns.ONE_YEAR); //删除所有分隔符节点
							//为每个年份添加指标节点，以便能正确的绑定到这些指标的报告期属性上
							insertIndexToText(nodeTextMap, newNodes, fetchNodeList, yearList);
						}
					}
				}
			}
		}
	}

    /**
     * 对多个的n日进行解析，以便支持多个均线日期的解析，比如问句：5日10日20日线多头排列，n日之间支持多种分隔符
     * 应该解析为：5日的均线多头排列_&_10日的均线多头排列_&_20日的均线多头排列
     * 
     * @author huangmin
     *
     * @param newNodes
     * @throws Exception
     */
	private void parseNodeForMultiNDay(ArrayList<SemanticNode> newNodes) {
		Map<String, Integer> nodeTextMap = new HashMap<String, Integer>();
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < newNodes.size(); i++) {
			String text = newNodes.get(i).getText();
			if(nodeTextMap.get(text) == null) {
				nodeTextMap.put(text, i);
			}
			sb.append(text);
		}
		Matcher matcher = DatePatterns.MulTI_NDay.matcher(sb.toString()); //看看是否有多重n日存在
		while (matcher.find()) { //二维多重n日
			String nDayStr = matcher.group(); //形如5日20日30日，或者5日,20日,30日
			if(StringUtils.isNotEmpty(nDayStr) && !DatePatterns.ONE_NDay.matcher(nDayStr).matches()) {
				List<String> nDayList = new ArrayList<String>();
				String[] nDayArr = nDayStr.split("日");
				for(String dnDay : nDayArr) {
					Matcher dmatcher = DatePatterns.DRAFT_NDay.matcher(dnDay);
					if(dmatcher.find()) {
						String nDay = dmatcher.group(2);
						nDayList.add(nDay+"日");
					}
				}
				if(nDayList.size() > 1) {
					int searchStartPos = getTheStartPosForCut(nDayList, nodeTextMap, "日");
					if(searchStartPos == -1) {
						continue;
					}
					int nodesSize = newNodes.size();
					if(searchStartPos < nodesSize) {
						int searchEndPos = -1;
						boolean nDayFound = false;
						for (int i = searchStartPos; i < nodesSize; i++ ) { //看看后面有没有均线属性的指标
							SemanticNode snode = newNodes.get(i);
							if(isSeparator(snode)) { //碰到分隔符，则不用再找了
								break;
							}
							searchEndPos = i;
							if(!nDayFound) {
								nDayFound = findThePropWithIndex(snode,"n日" );
							}
						}
						
						if(nDayFound) { //假如找到n日属性，则拷贝这些指标节点
							List<SemanticNode> fetchNodeList = cutTheFoundList(newNodes, searchStartPos, searchEndPos); //截取要插入的节点列表
							removeSeparatorNode(nDayList, nodeTextMap, newNodes, DatePatterns.ONE_NDay); //删除所有分隔符节点
							//为每个n日添加指标节点，以便能正确的绑定到这些指标的n日属性上
							insertIndexToText(nodeTextMap, newNodes, fetchNodeList, nDayList);
						}
					}
				}
			}
		}
	}
	
	/**
	 * 获取截取的开始位置。
	 * 
	 * @author huangmin
	 *
	 * @param textList
	 * @param nodeTextMap
	 * @param textUnit
	 * @return
	 */
	private int getTheStartPosForCut(List<String> textList, Map<String, Integer> nodeTextMap, String textUnit) {
		int textSize = textList.size();
		String lastText = textList.get(textSize - 1);
		String lastTextNoUnit = lastText.replaceAll(textUnit, StringUtils.EMPTY);
		int searchStartPos = -1;
		//获取最后一个节点的位置，有可能有单位，也可能没有
		if(nodeTextMap.containsKey(lastText) || nodeTextMap.containsKey(lastTextNoUnit)) {
			searchStartPos = (nodeTextMap.get(lastText) != null)? nodeTextMap.get(lastText) : nodeTextMap.get(lastTextNoUnit);
			searchStartPos++; //真正查找的开始位置不能包含最后的节点
		}
		
		return searchStartPos;
	}
	
	/**
	 * 插入这些截取的节点列表到每个节点(可能是时间节点，比如 10日，2013年等等)的后面
	 * 
	 * @author huangmin
	 *
	 * @param nodeTextMap
	 * @param newNodes
	 * @param fetchNodeList
	 * @param textList
	 */
	private void insertIndexToText(Map<String, Integer> nodeTextMap, ArrayList<SemanticNode> newNodes,
			List<SemanticNode> fetchNodeList, List<String> textList) {
		int textSize = textList.size();
		for(int i = 0; i < textSize - 1; i++) { //最后一个不需要参与绑定指标
			String text = textList.get(i);
			int textPosVal = nodeTextMap.get(text); //获取所在节点的位置
			textPosVal++; //将指标节点列表插入到节点的后面
			newNodes.addAll(textPosVal, fetchNodeList);
			refreshNodeMap(nodeTextMap, newNodes);
		}
	}
	
	/**
	 * 截取指定范围的节点列表。
	 * 
	 * @author huangmin
	 *
	 * @param newNodes
	 * @param searchStartPos 截取的开始位置
	 * @param searchEndPos 截取的结束位置
	 * @return
	 */
	private List<SemanticNode> cutTheFoundList(ArrayList<SemanticNode> newNodes, int searchStartPos, int searchEndPos) {
		List<SemanticNode> copiedNodeList = new ArrayList<SemanticNode>();
		List<SemanticNode> fetchNodeList = new ArrayList<SemanticNode>();
		
		CollectionUtils.addAll(copiedNodeList, new Object[newNodes.size()]); //生成一致的长度
		Collections.copy(copiedNodeList, newNodes); //拷贝当前的nodes
		for(int i = searchStartPos; i <= searchEndPos; i++ ) {
			SemanticNode snode = copiedNodeList.get(i);
			fetchNodeList.add(snode);
		}
		
		return fetchNodeList;
	}
	
	/**
	 * 删除指定节点之间所有的分隔符节点。
	 * 
	 * @author huangmin
	 *
	 * @param textList
	 * @param nodeTextMap
	 * @param newNodes
	 * @param textPattern
	 */
	private void removeSeparatorNode(List<String> textList, Map<String, Integer> nodeTextMap, 
			ArrayList<SemanticNode> newNodes, Pattern textPattern) {
		int textSize = textList.size();
		for(int i = 0; i < textSize - 1; i++) {
			String text = textList.get(i);
			Integer textPos = nodeTextMap.get(text); //获取所在节点的位置
			if(textPos != null) {
				int textPosVal = textPos.intValue();
				textPosVal++; //不能包含自身节点
				//删除所有分隔符节点
				while(newNodes.get(textPosVal) != null 
						&& !(textPattern.matcher(newNodes.get(textPosVal).getText()).matches())) {
					newNodes.remove(textPosVal);
					refreshNodeMap(nodeTextMap, newNodes);
				}
			}
		}
	}
	
	/**
	 * 看看指定的属性是否在当前的指标中存在。
	 * 
	 * @author huangmin
	 *
	 * @param snode
	 * @param propName
	 * @return
	 */
	private boolean findThePropWithIndex(SemanticNode snode, String propName) {
		if(snode != null && snode instanceof FocusNode) {
			FocusNode fnode = (FocusNode)snode;
			ClassNodeFacade cfnode = fnode.getIndex();
			if(cfnode == null) {
				return false;
			}
			List<PropNodeFacade> propList = cfnode.getAllProps();
			if(propList == null || propList.size() == 0) {
				return false;
			}
			for(PropNodeFacade pfnode : propList) {
				if(propName.equals(pfnode.getText())) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	/**
	 * 刷新缓存。
	 * 
	 * @author huangmin
	 *
	 * @param nodeTextMap
	 * @param newNodes
	 */
	private void refreshNodeMap(Map<String, Integer> nodeTextMap, ArrayList<SemanticNode> newNodes) {
		if(nodeTextMap != null && newNodes != null) {
			nodeTextMap.clear();
			for(int j = 0; j < newNodes.size(); j++) {
				String text = newNodes.get(j).getText();
				nodeTextMap.put(text, j);
			}
		}
	}
	
    // 判断是否为分隔符
	private boolean isSeparator(SemanticNode sn) {
		Pattern SEQUENCE_MAY_BREAK_TAG = Pattern.compile("^(\\s|,|;|\\.|。)$");
		boolean rtn = false;
		if (sn != null && SEQUENCE_MAY_BREAK_TAG.matcher(sn.getText()).matches()) {
			rtn = true;
		}
		return rtn;
	}	
}
