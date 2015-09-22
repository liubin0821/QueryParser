package com.myhexin.qparser.iterator;

import java.util.List;

import com.myhexin.qparser.define.EnumDef.NodeType;
import com.myhexin.qparser.node.SemanticNode;


/**
 * 通过UnknownNode 或 _&_分Chunk
 * 
 * @author 刘小峰 liuxiaofeng@myhexin.com
 * @Version 创建时间 2015-2-10
 *
 */
public class ChunkSemicolonIteratorImpl extends ChunkIteratorImpl {
	public ChunkSemicolonIteratorImpl(List<SemanticNode> semanticNodes) {
		super(semanticNodes);
	}


	//private final static Pattern sepPattern = Pattern.compile("(_&_|;|；)");
	
	/**
	 * 
	 * @param sNode
	 * @return
	 */
	@Override
	protected boolean isChunkNode(SemanticNode sNode) {
		if (sNode.getType() == NodeType.UNKNOWN && (sNode.getText().equals("_&_") || sNode.getText().equals(";") || sNode.getText().equals("；")) ) {
			return true;
		}
		return false;
	}
	
}
