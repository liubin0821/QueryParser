package com.myhexin.qparser.phrase.parsePlugins;

import java.util.ArrayList;

import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.phrase.ParserAnnotation;
import com.myhexin.qparser.phrase.parsePlugins.RemoveSomeConditionNode.RemoveSomeConditionNode;

/*
 * 某些条件下, 额外节点会影响正确解析, 本插件删除这些节点
 * 
 * 如:不包含交通运输行业     (行业影响句式匹配)
 */
public class PhraseParserPluginRemoveSomeConditionNode extends
		PhraseParserPluginAbstract {
	
	public PhraseParserPluginRemoveSomeConditionNode() {
		super("Remove_Some_Condition_Node");
	}

	//static int  i= 0;
	public ArrayList<ArrayList<SemanticNode>> process(ParserAnnotation annotation) {
		ArrayList<SemanticNode> nodes = annotation.getNodes();
		return dealWithRemoveSomeConditionNode(nodes);
	}

	/**
	 * @author: 	    吴永行 
	 * @dateTime:	  2013-10-28 下午6:59:12
	 * @description:   	
	 * @param nodes
	 * @return
	 * 
	 */
	private ArrayList<ArrayList<SemanticNode>> dealWithRemoveSomeConditionNode(
			ArrayList<SemanticNode> nodes) {	
		ArrayList<ArrayList<SemanticNode>> rlist = new ArrayList<ArrayList<SemanticNode>>(1);
		RemoveSomeConditionNode.process(nodes);//处理代码
		rlist.add(nodes);
		return rlist;
	}
}
