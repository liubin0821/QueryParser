package com.myhexin.qparser.phrase.parsePlugins;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.myhexin.qparser.define.EnumDef.LogicType;
import com.myhexin.qparser.define.EnumDef.NodeType;
import com.myhexin.qparser.iterator.BoundaryInfos;
import com.myhexin.qparser.iterator.SyntacticIteratorImpl;
import com.myhexin.qparser.node.BoundaryNode;
import com.myhexin.qparser.node.BoundaryNode.IMPLICIT_PATTERN;
import com.myhexin.qparser.node.FocusNode;
import com.myhexin.qparser.node.LogicNode;
import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.node.StrNode;
import com.myhexin.qparser.phrase.ParserAnnotation;

/**
 * 临时处理，处理上下句式之间的与或非逻辑
 */
public class PhraseParserPluginRelationshipOfAndOrNotOld extends PhraseParserPluginAbstract{
	

	public static org.slf4j.Logger logger_ = org.slf4j.LoggerFactory.getLogger(PhraseParserPluginRelationshipOfAndOrNotOld.class.getName());
	
    public PhraseParserPluginRelationshipOfAndOrNotOld() {
        super("Relationship_Of_And_Or_Not_Old");
    }
    
	public ArrayList<ArrayList<SemanticNode>> process(ParserAnnotation annotation) {
		ArrayList<SemanticNode> nodes = annotation.getNodes();
    	return relationshipOfAndOrNot(nodes);
    }
	
	private static Set<String> timeSequenceSet;
	private static String[] timeSequenceArray = new String[] { "先", "然后", "再" };
	
	static{
		timeSequenceSet = new HashSet<String>(Arrays.asList(timeSequenceArray));
	}
    
    /**
     * 原来的逻辑
     * 系统中如果前后为相同类型的字符串，比如均为所属概念，表示并列，取并集
     * 
     */
    public ArrayList<ArrayList<SemanticNode>> relationshipOfAndOrNot(ArrayList<SemanticNode> nodes) {
    	if (nodes == null || nodes.size() == 0)
    		return null;
    	
    	ArrayList<ArrayList<SemanticNode>> tlist = new ArrayList<ArrayList<SemanticNode>>(1);
        Iterator iterator = new SyntacticIteratorImpl(nodes);
        if (!iterator.hasNext()) {
			tlist.add(nodes);
			return tlist; // 没有句式
		}
        
        BoundaryNode lastBNode = null;
        StrNode lastStrNode = null;
        int lastBoundaryPos = 0;
    	while (iterator.hasNext()) {
    		BoundaryInfos boundaryInfos = (BoundaryInfos) iterator.next();
    		BoundaryNode bNode = (BoundaryNode) nodes.get(boundaryInfos.bStart);
    		StrNode strNode = null;
    		BoundaryNode.SyntacticPatternExtParseInfo info = bNode.getSyntacticPatternExtParseInfo(false);
            String patternId = bNode.getSyntacticPatternId();
			//句式之间的节点
			for (int i = boundaryInfos.start; i < boundaryInfos.bStart; i++) {
				SemanticNode sn = nodes.get(i);
				if (lastBNode != null && isLogicOr(sn)) {
					lastBNode.contextLogicType = LogicType.OR;
					break;
				} else if (lastBNode != null && isLogicAnd(sn)) {
					lastBNode.contextLogicType = LogicType.AND;
					break;
				} else if(hasExplicitOrLogic(nodes,boundaryInfos)) {
					lastBNode.contextLogicType = LogicType.OR;
					break;
				}
			}

			if (BoundaryNode.getImplicitPattern(patternId) != null) {
				if (patternId.equals(IMPLICIT_PATTERN.KEY_VALUE.toString())) {
					ArrayList<Integer> elelist;
					for (int j = 2; (elelist = info.getElementNodePosList(j)) != null; j++) {
						for (int pos : elelist) {
							if (pos == -1) {
								continue;
							} else if (pos != -1) {
								if (nodes.get(boundaryInfos.bStart + pos) == null || nodes.get(boundaryInfos.bStart + pos).isBoundToIndex() )
									continue;
								
								strNode = getStrValInstance(nodes.get(boundaryInfos.bStart + pos));
								if( lastBNode != null ) {
									if (hasExplicitOrLogic(nodes,boundaryInfos)) {
										lastBNode.contextLogicType = LogicType.OR;
									} else {
										lastBNode.contextLogicType = LogicType.AND;
									}
								}
							}
						}
					}
				} else if (patternId.equals(IMPLICIT_PATTERN.STR_INSTANCE.toString())) {
					ArrayList<Integer> elelist;
					for (int j = 1; (elelist = info.getElementNodePosList(j)) != null; j++) {
						for (int pos : elelist) {
							if (pos == -1) {
								continue;
							} else if (pos != -1) {
								if (nodes.get(boundaryInfos.bStart + pos) == null || nodes.get(boundaryInfos.bStart + pos).isBoundToIndex() )
									continue;
								
								strNode = getStrValInstance(nodes.get(boundaryInfos.bStart + pos));
								//changed by huangmin, 问句始终被切分成AND的关系，除非问句中明确指定或，或者之类的词
								if( lastBNode != null ) {
									if ( hasExplicitOrLogic(nodes,boundaryInfos) ) {
										lastBNode.contextLogicType = LogicType.OR;
									} else {
										lastBNode.contextLogicType = LogicType.AND;
									}
								}
							}
						}
					}
				}
			}
			
			for (int k = lastBoundaryPos + 1; k < boundaryInfos.bStart; k++) {
				SemanticNode node = nodes.get(k);
				if(timeSequenceSet.contains(node.getText())){
					//					if (lastBNode != null) {
					//						lastBNode.contextLogicType = LogicType.TIMESEQUENCE;
					//					}
					bNode.contextLogicType = LogicType.TIMESEQUENCE;
					break;
				}
				
			}
			
			lastBNode = bNode;
			lastStrNode = strNode;
			lastBoundaryPos = boundaryInfos.bEnd;
    	}
    	
		tlist.add(nodes);
		return tlist;
    }
    
	private static StrNode getStrValInstance(SemanticNode node) {
    	if (node == null || node.isCombined == true)
    		return null;
    	StrNode strNode = null;
        if (node.type == NodeType.STR_VAL) {
            strNode = (StrNode)node;
        } else if (node.type == NodeType.FOCUS) {
        	FocusNode focusNode = (FocusNode) node;
        	if (focusNode.hasString()) {
        		strNode = focusNode.getString();
        	}
        }
        return strNode;
    }
    
	// 判断两个字符串是否存在相同的类型
	private boolean isTheSameTypeStrNode(StrNode lastStrNode, StrNode strNode) {
		if (lastStrNode == null || strNode == null )
			return false;

		for (String st : strNode.subType) {
			if (lastStrNode.subType.contains(st))
				return true;
		}
		return false;
	}
	
	private boolean hasExplicitAndLogic(ArrayList<SemanticNode> nodes, BoundaryInfos boundaryInfos){
		for(int i=boundaryInfos.start;i<boundaryInfos.bStart;i++){
			if(nodes.get(i) instanceof FocusNode){
				FocusNode fcNode = (FocusNode)nodes.get(i); 
				if(fcNode.getText().equals("且"))
					return true;
			}
				
		}
		return false;
	}
	
	/**
	 * 判断问句中是否显式含有或之类的关键词，以便不做问句切分。
	 * 
	 * @param nodes
	 * @param boundaryInfos
	 * @return
	 */
	private boolean hasExplicitOrLogic(ArrayList<SemanticNode> nodes, BoundaryInfos boundaryInfos) {
		for( int i=boundaryInfos.start; i<boundaryInfos.bStart; i++) {
			SemanticNode node = nodes.get(i);
			if(node instanceof FocusNode){
				FocusNode fcNode = (FocusNode)node;
				String fcNodeText = fcNode.getText();
				if(StringUtils.isNotBlank(fcNodeText) && ("或".equals(fcNodeText) || "或者".equals(fcNodeText) ) ) {
					return true;
				}
			}
		}
		return false;
	}	

	/**
	 * 判断是否为逻辑节点or
	 * @param sn
	 * @return
	 */
	private boolean isLogicOr(SemanticNode sn) {
		if (sn.type == NodeType.LOGIC && ((LogicNode) sn).logicType == LogicType.OR)
			return true;
		else if (sn.type == NodeType.FOCUS && ((FocusNode) sn).hasLogic()
				&& ((FocusNode) sn).getLogic().logicType == LogicType.OR)
			return true;
		else
			return false;
	}

	/**
	 * 判断是否为逻辑节点and
	 * @param sn
	 * @return
	 */
	private boolean isLogicAnd(SemanticNode sn) {
		if (sn.type == NodeType.LOGIC && ((LogicNode) sn).logicType == LogicType.AND)
			return true;
		else if (sn.type == NodeType.FOCUS && ((FocusNode) sn).hasLogic()
				&& ((FocusNode) sn).getLogic().logicType == LogicType.AND)
			return true;
		else
			return false;
	}
}

