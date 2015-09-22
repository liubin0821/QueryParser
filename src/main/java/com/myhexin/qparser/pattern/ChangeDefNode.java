/**
 * Copyright©Zhejiang Hexin Flush Network Services Ltd. All rights reserved.
 * Zhejiang Hexin Flush Network Services Ltd.
 * 
 * http://www.10jqka.com.cn/
 */
package com.myhexin.qparser.pattern;

import java.util.HashMap;

import com.myhexin.qparser.Query.Type;
import com.myhexin.qparser.define.EnumDef.NodeType;
import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.onto.ClassNodeFacade;

public class ChangeDefNode extends SemanticNode {
	public ChangeDefNode(String text) {
		super(text);
		this.type = NodeType.CHANG_DEF;
	}
	/**
	 * @param args
	 */
	
	@Override
	public void parseNode(HashMap<String, String> k2v, Type qtype){
	}
	public ClassNodeFacade defClass_ = null;
	/** 被复制节点在Pattern中的位置 */
	public int changeGroupIndex;
	/** 被复制节点的text所指的节点位置 */
	public int[] textIndexArr ;
	@Override
	public SemanticNode copy() {
		ChangeDefNode rtn = new ChangeDefNode(super.text);
		rtn.changeGroupIndex=changeGroupIndex;
		rtn.defClass_=defClass_;
		rtn.textIndexArr=textIndexArr;
		super.copy(rtn);
		return rtn;
	}
}
