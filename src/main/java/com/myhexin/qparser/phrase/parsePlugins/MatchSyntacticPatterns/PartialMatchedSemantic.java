/**
 * 
 */
package com.myhexin.qparser.phrase.parsePlugins.MatchSyntacticPatterns;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.myhexin.qparser.define.EnumDef.NodeType;
import com.myhexin.qparser.define.EnumDef.PropType;
import com.myhexin.qparser.node.BoundaryNode;
import com.myhexin.qparser.node.DateNode;
import com.myhexin.qparser.node.FocusNode;
import com.myhexin.qparser.node.NodeUtil;
import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.onto.ClassNodeFacade;
import com.myhexin.qparser.onto.PropNodeFacade;

/**
 * @author chenhao
 * 部分完成匹配的节点集合
 */
public class PartialMatchedSemantic {
	//某一个句式匹配完的结果
	private ArrayList<SemanticNode> _partialMatchednodes = new ArrayList<SemanticNode>();
	//保存当前拼接多段的结果
	private ArrayList<ArrayList<SemanticNode>> _partialMatchednodesList = new ArrayList<ArrayList<SemanticNode>>();
	//集合相对于整体而言的开始位置
	private int _startPos = 0;
	//集合相对于整体而言的开始位置
	private int _endPos = 0;
	//节点内隐式句式的数量
	private int _implicitSyntacticNum = 0;
	//节点内隐式句式的数量
	private int _syntacticNum = 0;

	private PartialMatchedSemanticEvalItems _evalItems = null;

	public PartialMatchedSemantic() {
	}

	public PartialMatchedSemantic(ArrayList<ArrayList<SemanticNode>> partialMatchednodesList, int start, int end,
			int implicitPatternNum, int syntacticNum) {
		for (ArrayList<SemanticNode> partialMatchednodes : partialMatchednodesList) {
			this._partialMatchednodesList.add(partialMatchednodes);
		}
		this._startPos = start;
		this._endPos = end;
		this._implicitSyntacticNum = implicitPatternNum;
		this._syntacticNum = syntacticNum;
	}

	public PartialMatchedSemantic(ArrayList<SemanticNode> partialMatchednodes) {
		this._partialMatchednodes = partialMatchednodes;
		if (partialMatchednodes.get(0).type == NodeType.BOUNDARY) {
			BoundaryNode boundary = (BoundaryNode) partialMatchednodes.get(0);
			if (boundary.isStart()) {
				BoundaryNode.SyntacticPatternExtParseInfo extInfo = boundary.getSyntacticPatternExtParseInfo(true);
				_startPos = extInfo.getElementNodePosMin();
				_endPos = extInfo.getElementNodePosMax();
			}
		}
	}

	/**
	 * 复制节点方法,复制完以后用于拼接其他partialMatchedSemantic
	 * @return
	 */
	public PartialMatchedSemantic copyNode(){
		PartialMatchedSemantic newNode = new PartialMatchedSemantic(this._partialMatchednodesList, this._startPos,
				this._endPos, this._implicitSyntacticNum, this._syntacticNum);
		return newNode;
	}

	public int getStartPos() {
		return _startPos;
	}

	public int getEndPos() {
		return _endPos;
	}

	public int getImplicitSyntacticNum() {
		return _implicitSyntacticNum;
	}


	public ArrayList<SemanticNode> getPartialMatchednodes() {
		return _partialMatchednodes;
	}

	/**
	 * 获取当前拼接完成的结果,对envNode设置当前句式数,打分时会有用
	 * @return
	 */
	public ArrayList<SemanticNode> getFinalResult() {
		ArrayList<SemanticNode> reulst = new ArrayList<SemanticNode>();
		List<SemanticNode> listWithoutBoundary = new ArrayList<SemanticNode>();

		//先遍历一遍确定每个节点的位置,TODO以后可以优化
		for (ArrayList<SemanticNode> nodes : _partialMatchednodesList) {
			for (SemanticNode node : nodes) {
				if (!node.isBoundaryNode()) {
					listWithoutBoundary.add(node);
				}
			}
		}

		//将句式匹配缺省的指标的绑定信息补充完整
		//例:大股东持股数量大于2011年
		//【162:10 (大股东持股数量|b-synt-大股东持股数量[STOCK][|大股东持股数量[STOCK]:0.00])(大于|b-synt)】(2011年|b-index)
		//时间节点在句式外,要把对应的指标绑上去,其实在matchOneSyntacticPattern的时候已经绑上去了,只是返回的resultPerKeywordPerPattern里
		//没有时间节点,所以丢失了
		for (ArrayList<SemanticNode> nodes : _partialMatchednodesList) {
			for (SemanticNode node : nodes) {
				if (node.isBoundaryStartNode()) {
					BoundaryNode boundaryNode = (BoundaryNode) node;
					Map<Integer, SemanticNode> absentDefalutIndexMap = boundaryNode
							.getSyntacticPatternExtParseInfo(false).absentDefalutIndexMap;
					for (Entry<Integer, SemanticNode> entry : absentDefalutIndexMap.entrySet()) {
						int pos = entry.getKey();
						pos += boundaryNode.getCurrentPos();
						SemanticNode boundToIndex = entry.getValue();
						//除掉boundary头尾的以后的长度小于pos,
						//或者absentmap里的指标名和对应位置的名字不一样,说明有缺省
						if (pos > nodes.size() - 2 || nodes.get(pos).getText() != boundToIndex.getText()) {
							bindDateNodeToAbsentIndex(listWithoutBoundary, boundToIndex);
						}

					}
				}
			}
			reulst.addAll(nodes);
		}

		if (reulst.size() > 0) {
			SemanticNode envNode = NodeUtil.copyNode(reulst.get(0));
			envNode.syntacticNumSelfIncrease(_syntacticNum);
			reulst.set(0, envNode);
		}
		return reulst;
	}

	/**
	 * merge的是没有boundary的节点,也就是一些夹在句式之间没有被匹配到的节点,最后会处理成FREE_VAR
	 * @param nodes 新插入的节点
	 */
	public void mergeNodeWithOutBoundary(List<SemanticNode> nodes) {
		if (nodes == null || nodes.size() == 0) {
			return;
		}

		int implicitPatternNumIncrease = 0;
		ArrayList<SemanticNode> implicitNodes = MatchSyntacticPatterns.matchImplicitBinaryRelation(nodes, 0,
				nodes.size());
		for (SemanticNode node : implicitNodes) {
			if (node.type == NodeType.BOUNDARY && ((BoundaryNode) node).isStart())
				implicitPatternNumIncrease++;
		}
		_implicitSyntacticNum += implicitPatternNumIncrease;
		_syntacticNum += implicitPatternNumIncrease;
		_endPos += nodes.size();
		_partialMatchednodesList.add(implicitNodes);
	}

	/**
	 * merge的是有boundary的节点
	 * @param partialMatchednodes 新插入的节点
	 */
	public void mergeNodeWithBoundary(ArrayList<SemanticNode> partialMatchednodes) {
		if (partialMatchednodes == null || partialMatchednodes.size() == 0) {
			return;
		}
		int start = 0;
		int end = 0;
		if (partialMatchednodes.get(0).type == NodeType.BOUNDARY) {
			BoundaryNode boundary = (BoundaryNode) partialMatchednodes.get(0);
			if (boundary.isStart()) {
				BoundaryNode.SyntacticPatternExtParseInfo extInfo = boundary.getSyntacticPatternExtParseInfo(true);
				start = extInfo.getElementNodePosMin();
				end = extInfo.getElementNodePosMax();
				if (start >= 0) {
					extInfo.offsetElementNodePos(start - 1);
				}
			}
		}
		_syntacticNum += 1;
		_endPos += end - start + 1;
		_partialMatchednodesList.add(partialMatchednodes);
	}

	/***
	 * 获取评价剪枝需要用到的分数
	 * 
	 * @return
	 */
	public PartialMatchedSemanticEvalItems getEvalItems() {
		_evalItems = PartialMatchedSemanticEvalItems.getArgumentCount(getFinalResult());
		return _evalItems;
	}

	/***
	 * 把缺省指标绑定到时间上
	 * @param nodes
	 * @param existedIndexNode
	 */
	private void bindDateNodeToAbsentIndex(List<SemanticNode> nodes, SemanticNode existedIndexNode) {
		if (existedIndexNode.type == NodeType.FOCUS && ((FocusNode) existedIndexNode).hasIndex()) {
			ClassNodeFacade index = ((FocusNode) existedIndexNode).getIndex();
			if (index != null) {
				List<PropNodeFacade> propList = index.getClassifiedProps(PropType.DATE);

				if (propList != null) {
					for (PropNodeFacade pn : propList) {
						// 日期的值属性不绑定
						if (pn.isValueProp())
							continue;

						if (pn.isDateProp() && pn.getValue() != null) {
							SemanticNode bindToNode = pn.getValue();
							for (SemanticNode node : nodes) {
								if (bindToNode.getText() == node.getText() && node.isDateNode()) {
									bindNodeToProp(node, pn, index);
								}
							}

						}
					}
				}
			}
		}
	}

	//TODO 移到公共的地方
	private static Boolean bindNodeToProp(SemanticNode sn, PropNodeFacade pn, SemanticNode pnParent) {
		if (sn.type == NodeType.DATE && ((DateNode) sn).isCombined)
			return false;
		pn.setValue(sn);
		sn.setIsBoundToIndex(true);
		sn.setBoundToIndexProp(pnParent, pn);
		return true;
	}

}

