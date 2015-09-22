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
import com.myhexin.qparser.define.EnumDef.OperatorType;
import com.myhexin.qparser.except.QPException;
import com.myhexin.qparser.node.ChangeNode;
import com.myhexin.qparser.node.SemanticNode;

/**
 * the Class ChangeNumNode
 */
public class ChangeNumNode extends SemanticNode{

	/**
	 * @param text
	 */
	public ChangeNumNode(String text) {
		super(text);
		this.type = NodeType.CHANGE_NUM_NODE;
	}

	@Override
	public void parseNode(HashMap<String, String> k2v, Query.Type qtype) throws QPException {
	}
	public OperatorType operatorType;
	public int numNodePosition;
	public double number;
	public boolean isPassive = false;
	@Override
	public SemanticNode copy() {
		ChangeNumNode rtn = new ChangeNumNode(super.text);
		rtn.isPassive=isPassive;
		rtn.number=number;
		rtn.numNodePosition=numNodePosition;
		rtn.operatorType=operatorType;

		super.copy(rtn);
		return rtn;
	}
}
