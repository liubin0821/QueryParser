package com.myhexin.qparser.iterator;

import java.util.Iterator;
import java.util.List;

import com.myhexin.qparser.node.BoundaryNode;
import com.myhexin.qparser.node.SemanticNode;

public class SyntacticIteratorImpl implements Iterator<BoundaryInfos> {
	private List<SemanticNode> semanticNodes;
	private BoundaryInfos nextBoundaryInfos;
	private boolean hasNext;
	private int nextStart = 0;
	private int nextEnd = 0;
	

	public SyntacticIteratorImpl(List<SemanticNode> semanticNodes) {
		this.semanticNodes = semanticNodes;
		this.nextEnd = semanticNodes.size() - 1;
	}

	public SyntacticIteratorImpl(List<SemanticNode> nodes, int start, int end) {
		this.semanticNodes = nodes;
		this.nextStart = start;
		this.nextEnd = end;
	}

	public boolean hasNext() {
		if (nextBoundaryInfos == null) {
			nextBoundaryInfos = getNext();
		}
		return hasNext;
	}

	public BoundaryInfos next() {
		BoundaryInfos rtnBoundaryInfos = null;
		if (nextBoundaryInfos != null) {
			rtnBoundaryInfos = nextBoundaryInfos;
		} else {
			rtnBoundaryInfos = getNext();
		}
		nextBoundaryInfos = null;
		return rtnBoundaryInfos;
	}
	
	private BoundaryInfos getNext() {
		BoundaryInfos nextBoundaryInfos = null;

		int bStart = -1;
		int bEnd = -1;
		int currentStart = nextStart;
		int currentEnd = -1;

		String syntacticPatternId = "";
		boolean isThereStart = false;
		boolean isThereEnd = false;
		hasNext = false;
		for (int i = currentStart; i <= semanticNodes.size() - 1; i++) {
			SemanticNode currentNode = semanticNodes.get(i);
			if (!isThereStart && !isThereEnd && currentNode.isBoundaryStartNode()) {
				isThereStart = true;
				hasNext = true;
				bStart = i;
				syntacticPatternId = ((BoundaryNode) currentNode).syntacticPatternId;
			} else if (isThereStart && !isThereEnd && currentNode.isBoundaryEndNode()) {
				isThereEnd = true;
				bEnd = i;
			} else if (isThereStart && isThereEnd && currentNode.isBoundaryStartNode()) {
				currentEnd = i - 1;
				break;
			}
		}
		//下一个句式开始的位置应该是上一个boundary节点后一个位置
		nextStart = bEnd + 1;

		if (hasNext && currentEnd == -1) {
			currentEnd = semanticNodes.size() - 1;
		}

		if (isThereStart && isThereEnd) {
			nextBoundaryInfos = new BoundaryInfos(currentStart, bStart, bEnd, currentEnd);
			nextBoundaryInfos.syntacticPatternId = syntacticPatternId;
		}

		return nextBoundaryInfos;
	}




	@Override
	public void remove() {

	}
}
