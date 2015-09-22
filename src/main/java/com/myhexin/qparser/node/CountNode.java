package com.myhexin.qparser.node;

import java.util.HashMap;

import com.myhexin.qparser.Query.Type;
import com.myhexin.qparser.define.EnumDef.NodeType;
import com.myhexin.qparser.except.QPException;


/**
 * 这个类就没什么用,只在PostTreeBuilder.java中有用
 * 
 * @author 刘小峰 liuxiaofeng@myhexin.com
 * @Version 创建时间 2015-3-23
 *
 */
public class CountNode extends SemanticNode {

    public CountNode(String text) {
        super(text);
        type = NodeType.COUNT;
    }

    @Override
    public void parseNode(HashMap<String, String> k2v, Type qtype)
            throws QPException {

    }

    public boolean getSequence() {
        return isSequence;
    }

    public void isSequence() {
        isSequence = true;
    }

    private boolean isSequence = false;
    
    public String toString(){
        return String.format("COUNT[isSequence:%s]", isSequence);
    }
	@Override
	protected SemanticNode copy() {
		CountNode rtn = new CountNode(super.text);
		rtn.isSequence=isSequence;

		super.copy(rtn);
		return rtn;
	}
}
