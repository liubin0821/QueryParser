package com.myhexin.qparser.iterator;

import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import com.myhexin.qparser.define.EnumDef.NodeType;
import com.myhexin.qparser.node.SemanticNode;


/**
 * 通过UnknownNode 或 _&_分Chunk
 * 
 * @author 刘小峰 liuxiaofeng@myhexin.com
 * @Version 创建时间 2015-2-10
 *
 */
public class ChunkIteratorImpl implements Iterator<ChunkInfos> {
	private List<SemanticNode> semanticNodes;
	private ChunkInfos currentChunkInfos;
	private boolean isNext;
	private ChunkInfos nextChunkInfos;
	private final static Pattern sepPattern = Pattern.compile("(_&_|,|;|\\.|。)");

	public ChunkIteratorImpl(List<SemanticNode> semanticNodes) {
		this.semanticNodes = semanticNodes;
		this.currentChunkInfos = null;
		this.isNext = true;
		this.nextChunkInfos = null;
	}

	public boolean hasNext() {
		boolean isNextTemp = isNext;
		isNext = false;
		if (isNextTemp) {
			nextChunkInfos = getNext();
			if (nextChunkInfos != null) {
				return true;
			} else {
				return false;
			}
		} else if (nextChunkInfos != null) {
			return true;
		}
		return false;
	}

	public ChunkInfos next() {
		boolean isNextTemp = isNext;
		isNext = true;
		if (isNextTemp) {
			nextChunkInfos = getNext();
		}
		currentChunkInfos = nextChunkInfos;
		nextChunkInfos = null;
		return currentChunkInfos;
	}
	
	private ChunkInfos getNext() {
		ChunkInfos nextChunkInfos = null;
		int start = 0;
		int end = semanticNodes.size()-1;
		if (currentChunkInfos != null)
			start = currentChunkInfos.end+2;
		boolean isThereNextChunk = false;
		for (int i = start ; i < semanticNodes.size(); i++) {
			if (isChunkNode(semanticNodes.get(i))) {
				isThereNextChunk = true;
				end = i-1;
				break;
			} else if (i == end) {
				isThereNextChunk = true;
				break;
			}
		}
		if (isThereNextChunk) {
			nextChunkInfos = new ChunkInfos(start, end);
		} else {
			nextChunkInfos = null;
		}
		return nextChunkInfos;
	}

	
	/**
	 * 
	 * @param sNode
	 * @return
	 */
	protected boolean isChunkNode(SemanticNode sNode) {
		if (sNode.getType() == NodeType.UNKNOWN) {
			if (sepPattern.matcher(sNode.getText()).matches())
			return true;
		}
		return false;
	}
	
	@Override
	public void remove() {
		// TODO Auto-generated method stub
		
	}
}
