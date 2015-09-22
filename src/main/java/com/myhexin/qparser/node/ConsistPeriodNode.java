/**
 * 
 */
package com.myhexin.qparser.node;

import java.util.HashMap;

import com.myhexin.qparser.Query.Type;
import com.myhexin.qparser.define.EnumDef.NodeType;
import com.myhexin.qparser.except.QPException;

/**
 * @author chenhao
 * 持续周期节点
 * 
 */
public class ConsistPeriodNode extends SemanticNode {
	//持续周期数
	private int periodNum;

	public ConsistPeriodNode(String text) {
		super(text);
		type = NodeType.CONSIST_PERIOD;
	}

	public ConsistPeriodNode(String text, int periodNum) {
		super(text);
		type = NodeType.CONSIST_PERIOD;
		this.periodNum = periodNum;
	}

	@Override
	protected SemanticNode copy() {
		ConsistPeriodNode newNode = new ConsistPeriodNode(this.text, this.periodNum);
		super.copy(newNode);
		return newNode;
	}

	@Override
	public void parseNode(HashMap<String, String> k2v, Type qtype) throws QPException {
		// TODO Auto-generated method stub

	}

	public int getPeriodNum() {
		return periodNum;
	}

	public void setPeriodNum(int periodNum) {
		this.periodNum = periodNum;
	}


}
