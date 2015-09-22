/**
 * Copyright©Zhejiang Hexin Flush Network Services Ltd. All rights reserved.
 * Zhejiang Hexin Flush Network Services Ltd.
 * 
 * http://www.10jqka.com.cn/
 */
package com.myhexin.qparser.pattern;

import java.util.HashMap;

import com.myhexin.qparser.Query;
import com.myhexin.qparser.define.EnumDef.NodeType;
import com.myhexin.qparser.except.QPException;
import com.myhexin.qparser.node.SemanticNode;

/**
 * the Class CopyNode
 *
 */
public class CopyNode extends SemanticNode{

	/**
	 * @param text
	 */
	public CopyNode(String text) {
		super(text);
		this.type = NodeType.COPY_NODE;
	}
	
	@Override
	public void parseNode(HashMap<String, String> k2v, Query.Type qtype) throws QPException {
		// TODO Auto-generated method stub
		
	}
	
	/** 被复制节点在Pattern中的位置 */
	public int groupIndex;

	@Override
	public SemanticNode copy() {
		CopyNode rtn = new CopyNode(super.text);
		rtn.groupIndex=groupIndex;

		super.copy(rtn);
		return rtn;
	}
}
