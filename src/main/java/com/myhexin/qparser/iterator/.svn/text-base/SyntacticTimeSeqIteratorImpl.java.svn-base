/**
 * 
 */
package com.myhexin.qparser.iterator;

import java.util.Iterator;
import java.util.List;

import com.myhexin.qparser.define.EnumDef.LogicType;
import com.myhexin.qparser.node.BoundaryNode;
import com.myhexin.qparser.node.SemanticNode;

/**
 * @author chenhao
 *
 */
public class SyntacticTimeSeqIteratorImpl implements Iterator<BoundaryInfos> {
	private List<SemanticNode> semanticNodes;
	private BoundaryInfos nextBoundaryInfos;
	private boolean hasNext;
	private int nextStart = 0;
	private int nextEnd = 0;
	private SyntacticIteratorImpl syntacticIteratorImpl;

	public SyntacticTimeSeqIteratorImpl(List<SemanticNode> nodes) {
		this.semanticNodes = nodes;
		this.nextEnd = semanticNodes.size() - 1;
	}

	public SyntacticTimeSeqIteratorImpl(List<SemanticNode> nodes, int start, int end) {
		this.semanticNodes = nodes;
		this.nextStart = start;
		this.nextEnd = end;
	}

	@Override
	public boolean hasNext() {
		if (nextBoundaryInfos == null) {
			nextBoundaryInfos = getNext();
		}
		return hasNext;
	}

	@Override
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

	public SyntacticIteratorImpl nextSyntacticIterator() {
		return syntacticIteratorImpl;
	}

	@Override
	public void remove() {

	}

	private BoundaryInfos getNext() {
		BoundaryInfos nextBoundaryInfos = null;

		int bStart = -1;
		int bEnd = -1;
		int currentStart = nextStart;
		int currentEnd = nextEnd;

		String syntacticPatternId = "";
		boolean isThereStart = false;
		boolean isThereEnd = false;
		hasNext = false;
		for (int i = currentStart; i <= currentEnd; i++) {
			SemanticNode currentNode = semanticNodes.get(i);
			if (!isThereStart && !isThereEnd && currentNode.isBoundaryStartNode()
					&& ((BoundaryNode) currentNode).contextLogicType == LogicType.TIMESEQUENCE) {
				isThereStart = true;
				hasNext = true;
				bStart = i;
				syntacticPatternId = ((BoundaryNode) currentNode).syntacticPatternId;
			} else if (isThereStart && currentNode.isBoundaryEndNode()) {
				//和SyntacticIteratorImpl不同,每次遇到boundaryend 都会更新bEnd,直到遇到一个timesequnce的句式
				bEnd = i;
			} else if (isThereStart && currentNode.isBoundaryStartNode()
					&& ((BoundaryNode) currentNode).contextLogicType == LogicType.TIMESEQUENCE) {
				//找到下个句式开始的位置,停止
				nextStart = i;
				break;
			}
		}

		//说明之后没有句式了,nextstart移动到末尾,这样之后就不用遍历了
		if (hasNext && nextStart == currentStart) {
			nextStart = semanticNodes.size();
		}

		if (isThereStart) {
			nextBoundaryInfos = new BoundaryInfos(currentStart, bStart, bEnd, nextStart);
			syntacticIteratorImpl = new SyntacticIteratorImpl(semanticNodes, currentStart, nextStart - 1);
			nextBoundaryInfos.syntacticPatternId = syntacticPatternId;
		}

		return nextBoundaryInfos;

	}

}
