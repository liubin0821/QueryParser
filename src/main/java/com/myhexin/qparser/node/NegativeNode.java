package com.myhexin.qparser.node;

import java.util.HashMap;

import com.myhexin.qparser.Query;
import com.myhexin.qparser.define.EnumDef.NodeType;
import com.myhexin.qparser.except.BadDictException;

public class NegativeNode extends SemanticNode {
	@Override
	protected SemanticNode copy() {
		NegativeNode rtn = new NegativeNode();
	    	rtn.boolType_=boolType_;
			super.copy(rtn);
			return rtn;
	}
	private NegativeNode(){}
    public NegativeNode(String text) {
        super(text);
        type = NodeType.NEGATIVE;
    }

    @Override
    public void parseNode(HashMap<String, String> k2v, Query.Type qtype) throws BadDictException {
    }
    
    public boolean boolType_ = false;
}
