package com.myhexin.qparser.iterator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.myhexin.qparser.define.EnumDef.NodeType;
import com.myhexin.qparser.node.BoundaryNode;
import com.myhexin.qparser.node.FocusNode;
import com.myhexin.qparser.node.SemanticNode;

public class IndexIteratorImpl implements Iterator<FocusNode> {
	private List<SemanticNode> semanticNodes;
	private int bStart;
	private int elemSize;
	private int elemPos;
	private int listSize;
	private int listPos;
	private int current;
    
	/**
	 * 整个问句中指标的迭代器的构造函数。
	 * 
	 * @param semanticNodes
	 */
	public IndexIteratorImpl(List<SemanticNode> semanticNodes) {
		this.semanticNodes = semanticNodes;
		this.bStart = -1;
		this.elemSize = -1;
		this.elemPos = -1;
		this.listSize = -1;
		this.listPos = -1;
		this.current = -1;
	}

	/**
	 * 单个句式中指标的迭代器的构造函数
	 * 
	 * @param semanticNodes
	 * @param Start
	 * @param End
	 */
	public IndexIteratorImpl(List<SemanticNode> semanticNodes, int Start, int End) {
		this.semanticNodes = semanticNodes.subList(Start, End);
		this.bStart = -1;
		this.elemSize = -1;
		this.elemPos = -1;
		this.listSize = -1;
		this.listPos = -1;
		this.current = -1;
	}

	/**
	 * 判断是否有下一个
	 */
	public boolean hasNext() {
		SemanticNode next = getNext(false);
		if (next != null) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 得到下一个指标
	 */
	public FocusNode next() {
		FocusNode next = getNext(true);
		if (next != null) {
			return next;
		} else {
			return null;
		}
	}

	/**
	 * 判断是否有上一个
	 */
	public boolean hasPrev() {
		SemanticNode prev = getPrev(false);
		if (prev != null) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 得到上一个指标
	 */
	public FocusNode prev() {
		FocusNode prev = getPrev(true);
		if (prev != null) {
			return prev;
		} else {
			return null;
		}
	}

	public void first() {
		bStart = -1;
		current = -1;
	}
	
	public void last() {
		bStart = semanticNodes.size();
		current = semanticNodes.size();
	}
	
	/**
	 * 得到当前指标的语义节点即SemanticNode
	 * @return
	 */
	public SemanticNode getCurrentSemanticNode() {
        if (current >= 0 && current < semanticNodes.size()) 
        	return semanticNodes.get(current);
        return null; //当越界时返回
	}

	/**
	 * 用于对List<SemanticNode>向后遍历
	 * 
	 * @return
	 */
	private FocusNode getNext(boolean forward) {
		FocusNode fn = null;
		int tempBStart = bStart;
		int tempElemSize = elemSize;
		int tempElemPos = elemPos;
		int tempListSize = listSize;
		int tempListPos = listPos;
		int tempCurrent = current;
		BoundaryNode bNode = null;
		if (tempBStart == -1 || (tempElemPos == tempElemSize && tempListPos == tempListSize-1)) {
			for (int i = tempBStart+1; i < semanticNodes.size(); i++) {
				if (isBoundaryStartNode(semanticNodes.get(i))) {
					bNode = (BoundaryNode) semanticNodes.get(i);
					tempBStart = i;
					tempElemSize = bNode.getSyntacticPatternExtParseInfo(false).elementNodePostList.size();
					tempElemPos = 1;
					tempListPos = 0;
					break;
				}
			}
		} else {
			bNode = (BoundaryNode) semanticNodes.get(tempBStart);
			if (tempElemPos < tempElemSize && tempListPos == tempListSize-1) {
				tempElemPos++;
				tempListPos = 0;
			} else {
				tempListPos++; 
			}
		}
		
		if (bNode == null)
			return null;
		BoundaryNode.SyntacticPatternExtParseInfo info = bNode.getSyntacticPatternExtParseInfo(false);
        if (info == null)
            return null;
        
		ArrayList<Integer> elelist;
		boolean isNextIndex = false;
        for (int i = tempElemPos; (elelist = info.getElementNodePosList(i)) != null; i++) {
            for (int j = tempListPos; j < elelist.size(); j++) {
            	int pos = elelist.get(j);
            	tempElemPos = i;
            	tempListSize = elelist.size();
            	tempListPos = j;
                if (pos == -1) {
                	tempCurrent = tempBStart;
                	SemanticNode defaultNode = info.absentDefalutIndexMap.get(i);
                	if (defaultNode != null && defaultNode.type == NodeType.FOCUS && ((FocusNode)defaultNode).hasIndex()) {
	                	fn = (FocusNode)defaultNode;
	                	isNextIndex = true;
	                	break;
                	}
                } else if (semanticNodes.get(tempBStart + pos).type == NodeType.FOCUS 
                		&& ((FocusNode) semanticNodes.get(tempBStart + pos)).hasIndex()) {
                	tempCurrent = tempBStart + pos;
                	fn = ((FocusNode) semanticNodes.get(tempBStart + pos));
                	isNextIndex = true;
                	break;
                }
            }
            if (isNextIndex)
            	break;
            else if (tempElemPos == tempElemSize && tempListPos == tempListSize-1) {
    			for (int k = tempBStart+1; k < semanticNodes.size(); k++) {
    				if (isBoundaryStartNode(semanticNodes.get(k))) {
    					bNode = (BoundaryNode) semanticNodes.get(k);
    					info = bNode.getSyntacticPatternExtParseInfo(false);
    					tempBStart = k;
    					tempElemSize = bNode.getSyntacticPatternExtParseInfo(false).elementNodePostList.size();
    					tempElemPos = 1;
    					tempListPos = 0;
    					i = 0;
    					break;
    				}
    			}
    		} 
            else 
            	tempListPos = 0;
        }
        if (forward) {
        	bStart = tempBStart;
        	elemSize = tempElemSize;
        	elemPos = tempElemPos;
        	listSize = tempListSize;
        	listPos = tempListPos;
        	current = tempCurrent;
        }
		return fn;
	}
	
	/**
	 * 用于对List<SemanticNode>向前遍历
	 * 
	 * @return
	 */
	private FocusNode getPrev(boolean backward) {
		FocusNode fn = null;
		int tempBStart = bStart;
		int tempElemSize = elemSize;
		int tempElemPos = elemPos;
		int tempListSize = listSize;
		int tempListPos = listPos;
		int tempCurrent = current;
		BoundaryNode bNode = null;
		if (tempBStart == semanticNodes.size() || (tempElemPos == 1 && tempListPos == 0)) {
			for (int i = tempBStart-1; i >= 0; i--) {
				if (isBoundaryStartNode(semanticNodes.get(i))) {
					bNode = (BoundaryNode) semanticNodes.get(i);
					tempBStart = i;
					tempElemSize = bNode.getSyntacticPatternExtParseInfo(false).elementNodePostList.size();
					tempElemPos = tempElemSize;
					tempListPos = bNode.getSyntacticPatternExtParseInfo(false).getElementNodePosList(tempElemPos).size()-1;
					break;
				}
			}
		} else {
			bNode = (BoundaryNode) semanticNodes.get(tempBStart);
			if (tempElemPos > 0 && tempListPos == 0) {
				tempElemPos--;
				tempListPos = bNode.getSyntacticPatternExtParseInfo(false).getElementNodePosList(tempElemPos).size()-1;
			} else {
				tempListPos--;
			}
		}
		
		if (bNode == null)
			return null;
		BoundaryNode.SyntacticPatternExtParseInfo info = bNode.getSyntacticPatternExtParseInfo(false);
        if (info == null)
            return null;
		ArrayList<Integer> elelist;
		boolean isPrevIndex = false;
        for (int i = tempElemPos; (elelist = info.getElementNodePosList(i)) != null; i--) {
            for (int j = tempListPos; j >= 0; j--) {
            	int pos = elelist.get(j);
            	tempElemPos = i;
            	tempListSize = elelist.size();
            	tempListPos = j;
                if (pos == -1) {
                	tempCurrent = tempBStart;
                	SemanticNode defaultNode = info.absentDefalutIndexMap.get(i);
                	if (defaultNode != null && defaultNode.type == NodeType.FOCUS && ((FocusNode)defaultNode).hasIndex()) {
	                	fn = (FocusNode)defaultNode;
	                	isPrevIndex = true;
	                	break;
                	}
                } else if (semanticNodes.get(tempBStart + pos).type == NodeType.FOCUS 
                		&& ((FocusNode) semanticNodes.get(tempBStart + pos)).hasIndex()) {
                	tempCurrent = tempBStart + pos;
                	fn = ((FocusNode) semanticNodes.get(tempBStart + pos));
                	isPrevIndex = true;
                	break;
                }
            }
            if (isPrevIndex)
            	break;
            else if (tempElemPos == 1 && tempListPos == 0) {
    			for (int k = tempBStart-1; k >= 0; k--) {
    				if (isBoundaryStartNode(semanticNodes.get(k))) {
    					bNode = (BoundaryNode) semanticNodes.get(k);
    					info = bNode.getSyntacticPatternExtParseInfo(false);
    					tempBStart = k;
    					tempElemSize = bNode.getSyntacticPatternExtParseInfo(false).elementNodePostList.size();
    					tempElemPos = tempElemSize;
    					tempListPos = bNode.getSyntacticPatternExtParseInfo(false).getElementNodePosList(tempElemPos).size()-1;
    					i = tempElemSize + 1;
    					break;
    				}
    			}
    		} 
            else
            	tempListPos = bNode.getSyntacticPatternExtParseInfo(false).getElementNodePosList(i-1).size()-1;
        }
        if (backward) {
        	bStart = tempBStart;
        	elemSize = tempElemSize;
        	elemPos = tempElemPos;
        	listSize = tempListSize;
        	listPos = tempListPos;
        	current = tempCurrent;
        }
		return fn;
	}
	
	/**
	 * 判断是否为BoundaryNode节点并且类型还是Start
	 * @param sNode
	 * @return
	 */
	private boolean isBoundaryStartNode(SemanticNode sNode) {
		if (sNode.getType() == NodeType.BOUNDARY) {
			BoundaryNode bNode = (BoundaryNode) sNode;
			if (bNode.isStart()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void remove() {
		// TODO Auto-generated method stub

	}
}