package com.myhexin.qparser.phrase.parsePlugins;

import java.util.ArrayList;

import com.myhexin.qparser.define.EnumDef.NodeType;
import com.myhexin.qparser.node.BoundaryNode;
import com.myhexin.qparser.node.FocusNode;
import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.onto.ClassNodeFacade;
import com.myhexin.qparser.onto.PropNodeFacade;
import com.myhexin.qparser.phrase.ParserAnnotation;

public class PhraseParserPluginDealWithDateValueForIndex extends
		PhraseParserPluginAbstract {

	
	
	public PhraseParserPluginDealWithDateValueForIndex() {
		super("Deal_With_Date_Value_For_Index");
	}

	public ArrayList<ArrayList<SemanticNode>> process(ParserAnnotation annotation) {
		ArrayList<SemanticNode> nodes = annotation.getNodes();
		//this.ENV = ENV;
		ArrayList<ArrayList<SemanticNode>> rlist = new ArrayList<ArrayList<SemanticNode>>(1);
		int score = isDateValueForIndexRight(nodes);
		if (score > 0)
			rlist.add(nodes);
		
		return rlist;
	}

	private int isDateValueForIndexRight(ArrayList<SemanticNode> nodes) {
		if (nodes == null)
			return 0;
		ArrayList<Integer> boundaryList = getSyntacitcPatternList(nodes);
		if (boundaryList.size() <= 2)
			return 100; // 没有句式

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
					if (BoundaryNode.getImplicitPattern(bNode.syntacticPatternId) != null)
						continue;
					start = (int) boundaryList.get(i - 1);
					end = (int) boundaryList.get(i + 2);
					bStart = pos;
					bEnd = (int) boundaryList.get(i + 1);
					if (!isDateValueForIndexRightBySyntacitc(nodes, bNode, start, end,
							bStart, bEnd))
						return 0;
				}
			}
		}
		return 100;
	}

	// 判断是否存在值属性为时间节点的index且存在时间节点的情况
	private boolean isDateValueForIndexRightBySyntacitc(ArrayList<SemanticNode> nodes,
			BoundaryNode bNode, int start, int end, int bStart, int bEnd) {
		int dateValueNum = 0;
		int dateValueIndexNum = 0;
		// 计算原问句中时间节点的数量
		for (int i = start; i < end; i++) {
			SemanticNode sNode = nodes.get(i);
			if (sNode.type == NodeType.DATE) {
				dateValueNum++;
			}
		}
		
		// 计算解析结果中值属性为时间节点的index的数量
		BoundaryNode.SyntacticPatternExtParseInfo info = bNode.getSyntacticPatternExtParseInfo(false);
		if (info == null)
			return true;
		ArrayList<Integer> elelist;
		for (int j = 1; (elelist = info.getElementNodePosList(j)) != null; j++) {
			for (int pos : elelist) {
				int i = 0;
				SemanticNode sNode = null;

				if (pos != -1) { // 显式指标
					i = bStart + pos;
					sNode = nodes.get(i);
				} else { // 默认指标
					i = bStart;
					sNode = info.absentDefalutIndexMap.get(j);
				}
				if (sNode == null || sNode.type != NodeType.FOCUS)
					continue;
				FocusNode fNode = (FocusNode) sNode;
				if (!fNode.hasIndex())
					continue;
				ClassNodeFacade cNode = fNode.getIndex();
				PropNodeFacade pNode = cNode!=null ? cNode.getPropOfValue() : null;
				if (pNode!=null && pNode.isDateProp())
					dateValueIndexNum++;
			}
		}
		// 暂时还是都砍掉吧
		if (dateValueIndexNum > 0)
			return false;
		/*
		if (dateValueNum > 0 && dateValueIndexNum > 0)
			return false;
		*/
		return true;
	}

	/**
	 * 取boundaryNode的位置信息，并在头上增加0位置，最后增加最后一个节点位置
	 * 
	 * 结果 [0, bstartpos1, bendpos1, bstartpos2, bendpos2 ... bendposn,
	 * nodes.size()]
	 * 
	 * @param nodes
	 * @return ArrayList<Integer> 位置信息数组
	 */
	private ArrayList<Integer> getSyntacitcPatternList(
			ArrayList<SemanticNode> nodes) {
		int len = nodes.size();
		ArrayList<Integer> ret = new ArrayList<Integer>();
		ret.add(0);
		for (int i = 0; i < len; i++) {
			SemanticNode node = nodes.get(i);
			if (node.type == NodeType.BOUNDARY) {
				ret.add(i);
			}
		}
		ret.add(len);
		return ret;
	}
}
