/**
 * @author: 	    吴永行 
 * @dateTime:	  2013-10-29 下午5:01:49
 * @description:   	
 * 
 */
package com.myhexin.qparser.phrase.parsePlugins;

import java.util.ArrayList;

import com.myhexin.qparser.define.EnumDef.NodeType;
import com.myhexin.qparser.node.BoundaryNode;
import com.myhexin.qparser.node.Environment;
import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.phrase.ParserAnnotation;
import com.myhexin.qparser.phrase.PhraseUtil;
import com.myhexin.qparser.phrase.parsePlugins.DateNumModify.DateNumModify;


public class PhraseParserPluginDateNumModify extends PhraseParserPluginAbstract {

	
	public PhraseParserPluginDateNumModify() {
		super("According_XML_Change_Node_To_Date_Or_Num");
	}

	//static int  i= 0;
	public ArrayList<ArrayList<SemanticNode>> process(ParserAnnotation annotation) {
		
		//Environment ENV = annotation.getEnv();
		ArrayList<SemanticNode> nodes = annotation.getNodes();
		return dealWithDateNumModify(nodes);
	}

	private ArrayList<ArrayList<SemanticNode>> dealWithDateNumModify(
			ArrayList<SemanticNode> nodes) {

		ArrayList<ArrayList<SemanticNode>> rlist = new ArrayList<ArrayList<SemanticNode>>(1);

		if (nodes == null)
			return rlist;
		ArrayList<Integer> boundaryList = PhraseUtil
				.getSyntacitcPatternList(nodes);
		if (boundaryList.size() <= 2){
			rlist.add(nodes);
			return rlist; // 没有句式
		}

		int start = 0;
		int end = 0;
		int bStart = 0;
		int bEnd = 0;
		SemanticNode sNode = null;
		BoundaryNode bNode = null;

		for (int i = 1; i < boundaryList.size() - 1; i++) {
			int pos = (int) boundaryList.get(i);
			sNode = nodes.get(pos);
			if (sNode != null && sNode.type == NodeType.BOUNDARY) {
				bNode = (BoundaryNode) sNode;
				if (bNode.isStart()) {
					start = (int) boundaryList.get(i - 1);
					end = (int) boundaryList.get(i + 2);
					bStart = pos;
					bEnd = (int) boundaryList.get(i + 1);
					// 处理时间节点的合并
					DateNumModify.process(nodes, start, bEnd);

					//System.out.println(boundaryList);
				}
			}
		}
		rlist.add(nodes);
		return rlist;
	}
}
